package keeper.project.homepage.admin.service.systemadmin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import keeper.project.homepage.admin.dto.systemadmin.response.JobResponseDto;
import keeper.project.homepage.admin.dto.systemadmin.response.MemberJobTypeResponseDto;
import keeper.project.homepage.admin.dto.systemadmin.response.TypeResponseDto;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.user.service.member.MemberUtilService;
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
  private static final List<MemberJobEntity> jobs = new ArrayList<>();
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
    Stream.of("ROLE_회장", "ROLE_부회장", "ROLE_서기", "ROLE_출제자", "ROLE_회원")
        .forEach(jobName -> jobs.add(
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

  @Test
  @DisplayName("[SUCCESS] 회원 역할 리스트 불러오기 (회원, 출제자 제외)")
  void getJobList() {
    // mocking
    given(memberJobRepository.findAll())
        .willReturn(jobs);

    // when
    List<JobResponseDto> result = systemAdminService.getJobList();

    // then
    List<JobResponseDto> actual = jobs.stream().map(JobResponseDto::toDto).toList();
    assertThat(actual).containsAll(result);
    assertThat("ROLE_회원").isNotIn(result.stream().map(JobResponseDto::getName).toList());
    assertThat("ROLE_출제자").isNotIn(result.stream().map(JobResponseDto::getName).toList());
  }

  @Test
  @DisplayName("[SUCCESS] 역할 추가")
  void assignJob() {
    // given
    MemberEntity member = generateMemberEntity();
    MemberJobEntity job = jobs.get(0);
    MemberHasMemberJobEntity save = MemberHasMemberJobEntity.builder()
        .memberEntity(member)
        .memberJobEntity(job)
        .build();

    // mocking
    given(memberHasMemberJobRepository.save(any())).willReturn(save);
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
  @DisplayName("[SUCCESS] 역할 삭제")
  void deleteJob() {
    // given
    MemberJobEntity jobToBeDeleted = jobs.get(0);
    MemberJobEntity jobToBeRemained = jobs.get(1);
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

  private static MemberEntity generateMemberEntity(MemberJobEntity... memberJobEntities) {
    MemberEntity member = MemberEntity.builder()
        .id(memberSequence++)
        .generation(10.0f)
        .nickName("nickName" + memberSequence)
        .realName("realName" + memberSequence)
        .memberType(types.get(0))
        .memberJobs(new ArrayList<>())
        .build();
    for (MemberJobEntity memberJobEntity : memberJobEntities) {
      member.getMemberJobs().add(MemberHasMemberJobEntity
          .builder()
          .memberEntity(member)
          .memberJobEntity(memberJobEntity)
          .build());
    }
    return member;
  }

  @Test
  @DisplayName("[SUCCESS] 역할별 회원 전체 목록 가져오기 - 회원, 출제자 제외")
  void getMemberListHasJob() {

    // given
    MemberJobEntity master = jobs.get(0);
    MemberJobEntity subMaster = jobs.get(1);
    MemberJobEntity clerk = jobs.get(2);
    MemberJobEntity memberRole = jobs.get(3);
    MemberJobEntity challengeMaker = jobs.get(4);

    MemberEntity masterMember = generateMemberEntity(master);
    MemberEntity subMasterMember = generateMemberEntity(subMaster);
    MemberEntity clerkMember = generateMemberEntity(clerk);
    generateMemberEntity(memberRole);
    generateMemberEntity(challengeMaker);

    // mocking
    given(memberJobRepository.findAll()).willReturn(jobs);
    mockingFindAllByMemberJob(jobs.get(0), masterMember);
    mockingFindAllByMemberJob(jobs.get(1), subMasterMember);
    mockingFindAllByMemberJob(jobs.get(2), clerkMember);

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
    MemberJobEntity master = jobs.get(0);
    MemberJobEntity subMaster = jobs.get(1);
    MemberJobEntity clerk = jobs.get(2);

    MemberEntity masterMember = generateMemberEntity(master);
    MemberEntity member1 = generateMemberEntity(subMaster, clerk);
    MemberEntity member2 = generateMemberEntity(subMaster, clerk);
    MemberEntity member3 = generateMemberEntity(subMaster, clerk);

    // mocking
    given(memberJobRepository.findAll()).willReturn(jobs);
    mockingFindAllByMemberJob(jobs.get(0), masterMember);
    mockingFindAllByMemberJob(jobs.get(1), member1, member2, member3);
    mockingFindAllByMemberJob(jobs.get(2), member1, member2, member3);

    // when
    List<MemberJobTypeResponseDto> result = systemAdminService.getMemberListHasJob();

    // then
    assertThat(result.size()).isEqualTo(4);
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(masterMember));
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(member1));
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(member2));
    assertThat(result).contains(MemberJobTypeResponseDto.toDto(member3));
  }

  private void mockingFindAllByMemberJob(MemberJobEntity findJob, MemberEntity... resultMembers) {
    Long uniqueId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    given(memberHasMemberJobRepository.findAllByMemberJobEntity(findJob))
        .willReturn(Arrays.stream(resultMembers)
            .map(member -> new MemberHasMemberJobEntity(uniqueId, member, findJob))
            .toList());
  }
}