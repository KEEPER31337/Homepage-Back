package keeper.project.homepage.ctf.service;

import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.SYSTEM;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ctf.controller.CtfSpringTestHelper;
import keeper.project.homepage.ctf.dto.CtfChallengeAdminDto;
import keeper.project.homepage.ctf.dto.CtfChallengeCategoryDto;
import keeper.project.homepage.ctf.dto.CtfTeamDetailDto;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class CtfTeamServiceTest extends CtfSpringTestHelper {

/*
  @Mock
  private CtfTeamRepository teamRepository;
  @Mock
  private CtfTeamHasMemberRepository teamHasMemberRepository;
  @Mock
  private CtfSubmitLogRepository submitLogRepository;
  @Mock
  private CtfContestRepository contestRepository;
  @Mock
  private AuthService authService;
  @Mock
  private CtfUtilService ctfUtilService;
  @InjectMocks
  private CtfTeamService ctfTeamService;

  @Test
  void createTeam() {
    // given
    MemberEntity creator = MemberEntity.builder()
        .id(33L)
        .build();
    Long validCtfId = 123L;
    String ctfName = "testCtfName";
    String ctfDesc = "testCtfDesc";
    CtfContestEntity contest = CtfContestEntity.builder()
        .id(validCtfId)
        .name(ctfName)
        .description(ctfDesc)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .isJoinable(true)
        .build();
    String teamName = "testTeamName";
    String teamDesc = "testTeamDesc";
    CtfTeamDetailDto createTeamInfo = CtfTeamDetailDto.builder()
        .name(teamName)
        .description(teamDesc)
        .contestId(validCtfId)
        .build();
    CtfTeamEntity newTeamEntity = createTeamInfo.toEntity(contest, creator);
    // when
    doNothing().when(ctfUtilService).checkVirtualContest(any(Long.class));
    when(ctfUtilService.isJoinable(any(Long.class))).thenReturn(true);
    when(authService.getMemberEntityWithJWT()).thenReturn(creator);
    when(contestRepository.findById(validCtfId)).thenReturn(Optional.ofNullable(contest));
    when(teamHasMemberRepository.findAllByMember(creator)).thenReturn(new ArrayList<>());
    when(teamRepository.save(any())).thenReturn(newTeamEntity);
    when(teamHasMemberRepository.save(any())).thenReturn(null);
    doNothing().when(ctfUtilService).setAllDynamicScore();

    CtfTeamDetailDto result = ctfTeamService.createTeam(createTeamInfo);

    // then
    assert contest != null;
    Assertions.assertThat(result.getContestId()).isEqualTo(contest.getId());
    Assertions.assertThat(result.getCreatorId()).isEqualTo(creator.getId());
    Assertions.assertThat(result.getDescription()).isEqualTo(createTeamInfo.getDescription());
    Assertions.assertThat(result.getName()).isEqualTo(createTeamInfo.getName());
    Assertions.assertThat(result.getScore()).isEqualTo(0L);
  }
 */

  @Autowired
  private CtfTeamService ctfTeamService;

  @Autowired
  private CtfAdminService ctfAdminService;

  @Test
  @DisplayName("팀 생성 - 성공")
  void createTeam() {
    MemberEntity creator = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
        MemberRankName.일반회원);
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(creator.getId(), creator.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회원"))));
    String ctfName = "testCtfName";
    String ctfDesc = "testCtfDesc";
    CtfContestEntity contest = ctfContestRepository.save(CtfContestEntity.builder()
        .name(ctfName)
        .description(ctfDesc)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .isJoinable(true)
        .build());
    String teamName = "testTeamName";
    String teamDesc = "testTeamDesc";
    CtfTeamDetailDto createTeamInfo = CtfTeamDetailDto.builder()
        .name(teamName)
        .description(teamDesc)
        .contestId(contest.getId())
        .build();

    // when
    CtfTeamDetailDto result = ctfTeamService.createTeam(createTeamInfo);

    // then
    Assertions.assertThat(result.getContestId()).isEqualTo(contest.getId());
    Assertions.assertThat(result.getCreatorId()).isEqualTo(creator.getId());
    Assertions.assertThat(result.getDescription()).isEqualTo(createTeamInfo.getDescription());
    Assertions.assertThat(result.getName()).isEqualTo(createTeamInfo.getName());
    Assertions.assertThat(result.getScore()).isEqualTo(0L);
  }

  @Test
  void modifyTeam() {
  }

  @Test
  void joinTeam() {
  }

  @Test
  @DisplayName("팀 탈퇴 성공")
  void leaveTeam() {

    MemberEntity creator = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
        MemberRankName.일반회원);
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(creator.getId(), creator.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회원"))));
    String ctfName = "testCtfName";
    String ctfDesc = "testCtfDesc";
    CtfContestEntity contest = ctfContestRepository.save(CtfContestEntity.builder()
        .name(ctfName)
        .description(ctfDesc)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .isJoinable(true)
        .build());
    String teamName = "testTeamName";
    String teamDesc = "testTeamDesc";
    CtfTeamDetailDto createTeamInfo = CtfTeamDetailDto.builder()
        .name(teamName)
        .description(teamDesc)
        .contestId(contest.getId())
        .build();
    ctfTeamService.createTeam(createTeamInfo);

    String testTitle = "testTitle";
    String testContent = "testContent";
    String testFlag = "testFlag";
    Long testScore = 1234L;

    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(SYSTEM);

    List<CtfChallengeCategoryDto> categoryDtos = categories
        .stream()
        .map(ctfChallengeCategory -> CtfChallengeCategoryDto
            .builder()
            .id(ctfChallengeCategory.getId())
            .name(ctfChallengeCategory.getName()).build())
        .toList();

    CtfChallengeAdminDto createChallengeInfo = generateChallengeAdminDto(testScore, testFlag,
        testContent, testTitle, 123L, contest.getId(), categoryDtos, getStandardType());
    ctfAdminService.createChallenge(createChallengeInfo);

    // when
    CtfTeamDetailDto result = ctfTeamService.tryLeaveTeam(contest.getId());

    // then
    Assertions.assertThat(result.getContestId()).isEqualTo(contest.getId());
    Assertions.assertThat(result.getCreatorId()).isEqualTo(creator.getId());
    Assertions.assertThat(result.getDescription()).isEqualTo(createTeamInfo.getDescription());
    Assertions.assertThat(result.getName()).isEqualTo(createTeamInfo.getName());
    Assertions.assertThat(result.getScore()).isEqualTo(0L);
  }

  @Test
  void getTeamDetail() {
  }

  @Test
  void getTeamList() {
  }
}