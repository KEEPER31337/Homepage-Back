package keeper.project.homepage.service.ctf;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.controller.ctf.CtfSpringTestHelper.CtfChallengeType.STANDARD;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.MISC;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import keeper.project.homepage.ctf.dto.CtfChallengeAdminDto;
import keeper.project.homepage.admin.service.ctf.CtfAdminService;
import keeper.project.homepage.controller.ctf.CtfSpringTestHelper;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfSubmitLogEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.user.dto.ctf.CtfChallengeCategoryDto;
import keeper.project.homepage.user.dto.ctf.CtfChallengeTypeDto;
import keeper.project.homepage.user.dto.ctf.CtfFlagDto;
import keeper.project.homepage.user.dto.ctf.CtfTeamDetailDto;
import keeper.project.homepage.user.service.ctf.CtfChallengeService;
import keeper.project.homepage.user.service.ctf.CtfTeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CtfServiceTest extends CtfSpringTestHelper {

  @Autowired
  protected CtfAdminService ctfAdminService;

  @Autowired
  protected CtfTeamService ctfTeamService;

  @Autowired
  protected CtfChallengeService ctfChallengeService;

  private static final String TEST_FLAG1 = "TEST_FLAG_1";

  @Test
  @DisplayName("팀 생성 시 flag 제대로 생성 되는 지 테스트")
  public void createFlagTest() {
    // given
    MemberEntity creator = generateMemberEntity(회원, 정회원, 우수회원);
    CtfContestEntity contest = generateCtfContest(creator, true);

    // when
    String CREATE_TEAM_NAME1 = "CREATE_TEAM_NAME1";
    String CREATE_TEAM_DESC1 = "CREATE_TEAM_DESC1";
    MemberEntity member = generateMemberEntity(회원, 정회원, 우수회원);
    CtfTeamDetailDto createTeam = createTeam(contest, member, CREATE_TEAM_NAME1,
        CREATE_TEAM_DESC1);

    // then
    List<CtfFlagEntity> ctfFlagEntityList = ctfFlagRepository.findAllByCtfTeamEntityId(
        createTeam.getId());

    assertThat(ctfFlagEntityList.size()).isEqualTo(0);

    // when
    CtfChallengeAdminDto createChallenge = createChallenge(contest.getId(), creator, TEST_FLAG1);

    // then
    List<CtfFlagEntity> ctfFlagEntityList2 = ctfFlagRepository.findAllByCtfTeamEntityId(
        createTeam.getId());

    assertThat(ctfFlagEntityList2.size()).isEqualTo(1);

    // when
    String CREATE_TEAM_NAME2 = "CREATE_TEAM_NAME2";
    String CREATE_TEAM_DESC2 = "CREATE_TEAM_DESC2";
    MemberEntity member2 = generateMemberEntity(회원, 정회원, 우수회원);
    CtfTeamDetailDto createTeam2 = createTeam(contest, member2, CREATE_TEAM_NAME2,
        CREATE_TEAM_DESC2);

    // then
    List<CtfFlagEntity> ctfFlagEntityList3 = ctfFlagRepository.findAllByCtfChallengeEntityId(
        createChallenge.getChallengeId());

    assertThat(ctfFlagEntityList3.size()).isEqualTo(3);

  }

  @Test
  @DisplayName("팀 없이 문제 생성 시 flag 제대로 생성 되는 지 테스트")
  public void createFlagByNonTeamTest() {

    // given
    MemberEntity creator = generateMemberEntity(회원, 정회원, 우수회원);
    CtfContestEntity contest = generateCtfContest(creator, true);

    // when
    CtfChallengeAdminDto createChallenge = createChallenge(contest.getId(), creator, TEST_FLAG1);

    // then
    List<CtfFlagEntity> ctfFlagEntityList = ctfFlagRepository.findAllByCtfChallengeEntityId(
        createChallenge.getChallengeId());

    assertThat(ctfFlagEntityList.size()).isEqualTo(1);

  }

  @Test
  @DisplayName("flag 제출 로그 제대로 생성 되는 지 확인")
  public void submitLogTest() {
    // given
    MemberEntity creator = generateMemberEntity(회원, 정회원, 우수회원);
    CtfContestEntity contest = generateCtfContest(creator, true);
    CtfChallengeAdminDto createChallenge = createChallenge(contest.getId(), creator, TEST_FLAG1);
    MemberEntity submitter = generateMemberEntity(회원, 정회원, 우수회원);
    String CREATE_TEAM_NAME = "CREATE_TEAM_NAME";
    String CREATE_TEAM_DESC = "CREATE_TEAM_DESC";
    createTeam(contest, submitter, CREATE_TEAM_NAME, CREATE_TEAM_DESC);
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(submitter.getId(), submitter.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회원"))));

    // when
    CtfSubmitLogEntity submitLog = ctfChallengeService.setLog(createChallenge.getChallengeId(),
        CtfFlagDto.builder().content(TEST_FLAG1).build());

    // then
    assertThat(submitLog.getFlagSubmitted()).isEqualTo(TEST_FLAG1);
    assertThat(submitLog.getIsCorrect()).isEqualTo(true);
    assertThat(submitLog.getTeamName()).isEqualTo(CREATE_TEAM_NAME);
    assertThat(submitLog.getSubmitterLoginId()).isEqualTo(submitter.getLoginId());
    assertThat(submitLog.getSubmitterRealname()).isEqualTo(submitter.getRealName());
    assertThat(submitLog.getChallengeName()).isEqualTo(createChallenge.getTitle());
    assertThat(submitLog.getContestName()).isEqualTo(contest.getName());
  }

  private CtfTeamDetailDto createTeam(CtfContestEntity contest, MemberEntity member,
      String teamName, String teamDesc) {
    CtfTeamDetailDto createTeam = CtfTeamDetailDto.builder()
        .name(teamName)
        .description(teamDesc)
        .contestId(contest.getId())
        .build();
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(member.getId(), member.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회원"))));
    createTeam = ctfTeamService.createTeam(createTeam);
    return createTeam;
  }

  private CtfChallengeAdminDto createChallenge(Long contestId, MemberEntity member, String flag) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(member.getId(), member.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회원"))));
    final long epochTime = System.nanoTime();
    CtfChallengeAdminDto createChallenge = CtfChallengeAdminDto.builder()
        .title("TITLE_" + epochTime)
        .content("CONTENT_" + epochTime)
        .contestId(contestId)
        .flag(flag)
        .score(1000L)
        .isSolvable(true)
        .type(CtfChallengeTypeDto.builder()
            .id(STANDARD.getId())
            .build())
        .category(CtfChallengeCategoryDto.builder()
            .id(MISC.getId())
            .build())
        .build();
    return ctfAdminService.createProblem(createChallenge);
  }
}
