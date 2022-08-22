package keeper.project.homepage.admin.service.sysadmin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import keeper.project.homepage.admin.dto.sysadmin.response.JobResponseDto;
import keeper.project.homepage.admin.dto.sysadmin.response.MemberJobTypeResponseDto;
import keeper.project.homepage.admin.dto.sysadmin.response.TypeResponseDto;
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
class SysadminServiceTest {

  private static long memberSequence = 1L;
  private static long jobSequence = 1L;
  private static final List<MemberJobEntity> jobs = new ArrayList<>();
  private static long typeSequence = 1L;
  private static final List<MemberTypeEntity> types = new ArrayList<>();

  @InjectMocks
  private SysadminService sysadminService;
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
    List<JobResponseDto> result = sysadminService.getJobList();

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
    MemberJobTypeResponseDto result = sysadminService.assignJob(member.getId(), job.getId());

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
    MemberEntity member = generateMemberEntity(jobs.get(0), jobs.get(1));
    MemberJobEntity job = jobs.get(0);

    // mocking
    given(memberUtilService.getById(member.getId())).willReturn(member);
    given(memberUtilService.getJobById(job.getId())).willReturn(job);

    // when
    MemberJobTypeResponseDto result = sysadminService.deleteJob(member.getId(), job.getId());

    // then
    assertThat(member.getGeneration()).isEqualTo(result.getGeneration());
    assertThat(member.getId()).isEqualTo(result.getMemberId());
    assertThat(JobResponseDto.toDto(job)).isNotIn(result.getHasJobs());
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
}