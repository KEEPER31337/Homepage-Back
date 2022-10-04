package keeper.project.homepage.systemadmin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import keeper.project.homepage.systemadmin.dto.response.JobResponseDto;
import keeper.project.homepage.systemadmin.dto.response.MemberJobTypeResponseDto;
import keeper.project.homepage.systemadmin.dto.response.TypeResponseDto;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.systemadmin.exception.CustomClerkInaccessibleJobException;
import keeper.project.homepage.member.repository.MemberHasMemberJobRepository;
import keeper.project.homepage.member.repository.MemberJobRepository;
import keeper.project.homepage.member.service.MemberUtilService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class SystemAdminServiceTest {

  private static long memberSequence = 1L;
  private static long jobSequence = 1L;
  private static final List<MemberJobEntity> accessibleJobs = new ArrayList<>();
  private static final List<MemberJobEntity> inAccessibleJobs = new ArrayList<>();
  private static long typeSequence = 1L;
  private static final List<MemberTypeEntity> types = new ArrayList<>();

  @InjectMocks
  private SystemAdminService systemAdminService;
  @Mock
  private MemberUtilService memberUtilService;
  @Mock
  private MemberJobRepository memberJobRepository;
  @Mock
  private MemberHasMemberJobRepository memberHasMemberJobRepository;

  @BeforeAll
  static void initJobs() {
    Stream.of("ROLE_회장", "ROLE_부회장", "ROLE_서기")
        .forEach(jobName -> accessibleJobs.add(
            MemberJobEntity.builder()
                .id(jobSequence++)
                .name(jobName)
                .badge(ThumbnailEntity.builder()
                    .path("testJobPath_" + jobSequence)
                    .build())
                .build())
        );
    Stream.of("ROLE_출제자", "ROLE_회원")
        .forEach(jobName -> inAccessibleJobs.add(
            MemberJobEntity.builder()
                .id(jobSequence++)
                .name(jobName)
                .badge(ThumbnailEntity.builder()
                    .path("testJobPath_" + jobSequence)
                    .build())
                .build())
        );
  }

  @BeforeAll
  static void initTypes() {
    Stream.of("활동", "휴면", "졸업")
        .forEach(typeName -> types.add(
            MemberTypeEntity.builder()
                .id(typeSequence++)
                .name(typeName)
                .badge(ThumbnailEntity.builder()
                    .path("testTypePath_" + typeSequence)
                    .build())
                .build())
        );
  }

  @BeforeEach
  void setUp() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @AfterEach
  void cleanAll() {
    for (var accessibleJob : accessibleJobs) {
      accessibleJob.getMembers().clear();
    }
    for (var inAccessibleJob : inAccessibleJobs) {
      inAccessibleJob.getMembers().clear();
    }
    for (var type : types) {
      type.getMembers().clear();
    }
  }

  @Test
  @DisplayName("[SUCCESS] 회원 역할 리스트 불러오기 (회원, 출제자 제외)")
  void getJobList() {
    // mocking
    given(memberJobRepository.findAll())
        .willReturn(accessibleJobs);

    // when
    List<JobResponseDto> result = systemAdminService.getJobList();

    // then
    List<JobResponseDto> actual = accessibleJobs.stream().map(JobResponseDto::toDto).toList();
    assertThat(actual).containsAll(result);
    assertThat("ROLE_회원").isNotIn(result.stream().map(JobResponseDto::getName).toList());
    assertThat("ROLE_출제자").isNotIn(result.stream().map(JobResponseDto::getName).toList());
  }

  @Test
  @DisplayName("[SUCCESS] 역할 추가")
  void assignJob() {
    // given
    MemberEntity member = generateMemberEntity();
    MemberJobEntity job = accessibleJobs.get(0);
    MemberHasMemberJobEntity save = MemberHasMemberJobEntity.builder()
        .memberEntity(member)
        .memberJobEntity(job)
        .build();

    // mocking
    given(memberUtilService.getById(member.getId())).willReturn(member);
    given(memberUtilService.getJobById(job.getId())).willReturn(job);

    // when
    MemberJobTypeResponseDto result = systemAdminService.assignJob(member.getId(), job.getId());

    // then
    assertThat(member.getGeneration()).isEqualTo(result.getGeneration());
    assertThat(member.getId()).isEqualTo(result.getMemberId());
    assertThat(JobResponseDto.toDto(job)).isIn(result.getHasJobs());
    assertThat(TypeResponseDto.toDto(member.getMemberType())).isEqualTo(result.getType());
  }

  @Test
  @DisplayName("[EXCEPTION] 등록할 수 없는 역할 추가")
  void assignJob_inAccessibleJob() {
    // given
    MemberEntity member = generateMemberEntity();
    MemberJobEntity job = inAccessibleJobs.get(0);

    // mocking
    given(memberUtilService.getById(member.getId())).willReturn(member);
    given(memberUtilService.getJobById(job.getId())).willReturn(job);

    // when
    Throwable result = catchThrowable(
        () -> systemAdminService.assignJob(member.getId(), job.getId()));

    // then
    assertThat(result).isInstanceOf(CustomClerkInaccessibleJobException.class);
  }

  @Test
  @DisplayName("[SUCCESS] 역할 삭제")
  void deleteJob() {
    // given
    MemberJobEntity jobToBeDeleted = accessibleJobs.get(0);
    MemberJobEntity jobToBeRemained = accessibleJobs.get(1);
    MemberEntity member = generateMemberEntity(jobToBeDeleted, jobToBeRemained);

    // mocking
    given(memberUtilService.getById(member.getId())).willReturn(member);
    given(memberUtilService.getJobById(jobToBeDeleted.getId())).willReturn(jobToBeDeleted);

    // when
    MemberJobTypeResponseDto result = systemAdminService.deleteJob(member.getId(),
        jobToBeDeleted.getId());
    // then
    assertThat(member.getGeneration()).isEqualTo(result.getGeneration());
    assertThat(member.getId()).isEqualTo(result.getMemberId());
    assertThat(JobResponseDto.toDto(jobToBeDeleted)).isNotIn(result.getHasJobs());
    assertThat(JobResponseDto.toDto(jobToBeRemained)).isIn(result.getHasJobs());
    assertThat(TypeResponseDto.toDto(member.getMemberType())).isEqualTo(result.getType());
  }

  @Test
  @DisplayName("[EXCEPTION] 삭제할 수 없는 역할 삭제")
  void deleteJob_inAccessibleJob() {
    // given
    MemberJobEntity inAccessibleJob = inAccessibleJobs.get(0);
    MemberJobEntity accessibleJob1 = accessibleJobs.get(0);
    MemberJobEntity accessibleJob2 = accessibleJobs.get(1);
    MemberEntity member = generateMemberEntity(inAccessibleJob, accessibleJob1, accessibleJob2);

    // mocking
    given(memberUtilService.getById(member.getId())).willReturn(member);
    given(memberUtilService.getJobById(inAccessibleJob.getId())).willReturn(inAccessibleJob);

    // when
    Throwable result = catchThrowable(
        () -> systemAdminService.deleteJob(member.getId(),
            inAccessibleJob.getId()));

    // then
    assertThat(result).isInstanceOf(CustomClerkInaccessibleJobException.class);
  }

  private static MemberEntity generateMemberEntity(MemberJobEntity... memberJobEntities) {
    MemberEntity member = MemberEntity.builder()
        .id(memberSequence++)
        .generation(10.0f)
        .nickName("nickName" + memberSequence)
        .realName("realName" + memberSequence)
        .memberType(types.get(0))
        .build();
    for (MemberJobEntity memberJobEntity : memberJobEntities) {
      Long uniqueId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
      MemberHasMemberJobEntity memberHasMemberJob = MemberHasMemberJobEntity.builder()
          .id(uniqueId)
          .memberEntity(member)
          .memberJobEntity(memberJobEntity)
          .build();
      member.getMemberJobs().add(memberHasMemberJob);
      memberJobEntity.getMembers().add(memberHasMemberJob);
    }
    return member;
  }

  @Test
  @DisplayName("[SUCCESS] 역할별 회원 전체 목록 가져오기 - 회원, 출제자 제외")
  void getMemberListHasJob() {

    // given
    MemberJobEntity master = accessibleJobs.get(0);
    MemberJobEntity subMaster = accessibleJobs.get(1);
    MemberJobEntity clerk = accessibleJobs.get(2);
    MemberJobEntity memberRole = inAccessibleJobs.get(0);
    MemberJobEntity challengeMaker = inAccessibleJobs.get(1);

    MemberEntity masterMember = generateMemberEntity(master);
    MemberEntity subMasterMember = generateMemberEntity(subMaster);
    MemberEntity clerkMember = generateMemberEntity(clerk);
    generateMemberEntity(memberRole);
    generateMemberEntity(challengeMaker);

    // mocking
    given(memberJobRepository.findAll()).willReturn(accessibleJobs);
    mockingFindAllByMemberJobsIn(accessibleJobs);

    // when
    List<MemberJobTypeResponseDto> result = systemAdminService.getMemberListHasJob();

    // then
    assertThat(result.size()).isEqualTo(3);
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(masterMember));
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(subMasterMember));
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(clerkMember));
  }

  @Test
  @DisplayName("[SUCCESS] 역할별 회원 전체 목록 가져오기 - 회원 중복 없음")
  void getMemberListHasJobUniqueMember() {

    // given
    MemberJobEntity master = accessibleJobs.get(0);
    MemberJobEntity subMaster = accessibleJobs.get(1);
    MemberJobEntity clerk = accessibleJobs.get(2);

    MemberEntity masterMember = generateMemberEntity(master);
    MemberEntity member1 = generateMemberEntity(subMaster, clerk);
    MemberEntity member2 = generateMemberEntity(subMaster, clerk);
    MemberEntity member3 = generateMemberEntity(subMaster, clerk);

    // mocking
    given(memberJobRepository.findAll()).willReturn(accessibleJobs);
    mockingFindAllByMemberJobsIn(accessibleJobs);

    // when
    List<MemberJobTypeResponseDto> result = systemAdminService.getMemberListHasJob();

    // then
    assertThat(result.size()).isEqualTo(4);
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(masterMember));
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(member1));
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(member2));
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(member3));
  }

  private void mockingFindAllByMemberJobsIn(List<MemberJobEntity> findJobs) {
    List<MemberHasMemberJobEntity> result = new ArrayList<>();
    for (MemberJobEntity findJob : findJobs) {
      result.addAll(findJob.getMembers());
    }
    given(memberHasMemberJobRepository.findByMemberJobEntityIn(findJobs))
        .willReturn(result);
  }
}