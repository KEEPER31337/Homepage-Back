package keeper.project.homepage.service.ctf;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.controller.ctf.CtfSpringTestHelper.CtfChallengeType.STANDARD;
import static keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity.MISC;

import java.util.List;
import keeper.project.homepage.admin.dto.ctf.CtfChallengeAdminDto;
import keeper.project.homepage.admin.service.ctf.CtfAdminService;
import keeper.project.homepage.controller.ctf.CtfSpringTestHelper;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.ctf.CtfChallengeCategoryDto;
import keeper.project.homepage.user.dto.ctf.CtfChallengeTypeDto;
import keeper.project.homepage.user.dto.ctf.CtfTeamDetailDto;
import keeper.project.homepage.user.service.ctf.CtfTeamService;
import org.assertj.core.api.Assertions;
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

    Assertions.assertThat(ctfFlagEntityList.size()).isEqualTo(0);

    // when
    CtfChallengeAdminDto createChallenge = createChallenge(contest.getId(), creator);

    // then
    List<CtfFlagEntity> ctfFlagEntityList2 = ctfFlagRepository.findAllByCtfTeamEntityId(
        createTeam.getId());

    Assertions.assertThat(ctfFlagEntityList2.size()).isEqualTo(1);

    // when
    String CREATE_TEAM_NAME2 = "CREATE_TEAM_NAME2";
    String CREATE_TEAM_DESC2 = "CREATE_TEAM_DESC2";
    MemberEntity member2 = generateMemberEntity(회원, 정회원, 우수회원);
    CtfTeamDetailDto createTeam2 = createTeam(contest, member2, CREATE_TEAM_NAME2,
        CREATE_TEAM_DESC2);

    // then
    List<CtfFlagEntity> ctfFlagEntityList3 = ctfFlagRepository.findAllByCtfChallengeEntityId(
        createChallenge.getChallengeId());

    Assertions.assertThat(ctfFlagEntityList3.size()).isEqualTo(3);

  }

  @Test
  @DisplayName("팀 없이 문제 생성 시 flag 제대로 생성 되는 지 테스트")
  public void createFlagByNonTeamTest() {

    // given
    MemberEntity creator = generateMemberEntity(회원, 정회원, 우수회원);
    CtfContestEntity contest = generateCtfContest(creator, true);

    // when
    CtfChallengeAdminDto createChallenge = createChallenge(contest.getId(), creator);

    // then
    List<CtfFlagEntity> ctfFlagEntityList = ctfFlagRepository.findAllByCtfChallengeEntityId(
        createChallenge.getChallengeId());

    Assertions.assertThat(ctfFlagEntityList.size()).isEqualTo(1);

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

  private CtfChallengeAdminDto createChallenge(Long contestId, MemberEntity member) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(member.getId(), member.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회원"))));
    final long epochTime = System.nanoTime();
    String TEST_FLAG1 = "TEST_FLAG_1";
    CtfChallengeAdminDto createChallenge = CtfChallengeAdminDto.builder()
        .title("TITLE_" + epochTime)
        .content("CONTENT_" + epochTime)
        .contestId(contestId)
        .flag(TEST_FLAG1)
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
