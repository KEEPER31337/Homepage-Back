package keeper.project.homepage.ctf.service;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회장;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.WEB;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.STANDARD;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import keeper.project.homepage.ctf.controller.CtfSpringTestHelper;
import keeper.project.homepage.ctf.dto.CtfChallengeAdminDto;
import keeper.project.homepage.ctf.dto.CtfChallengeCategoryDto;
import keeper.project.homepage.ctf.dto.CtfChallengeTypeDto;
import keeper.project.homepage.ctf.dto.CtfFlagDto;
import keeper.project.homepage.ctf.dto.CtfTeamDetailDto;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.util.service.CtfUtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CtfAdminServiceTest extends CtfSpringTestHelper {

  @Autowired
  private CtfAdminService ctfAdminService;
  @Autowired
  private CtfChallengeService ctfChallengeService;
  @Autowired
  private CtfTeamService ctfTeamService;
  @Autowired
  EntityManager em;

  private CtfContestEntity ctfContestEntity;
  private MemberEntity contestCreator;

  @BeforeEach
  void setUp() {
    contestCreator = generateMemberEntity(회장, 정회원, 일반회원);
    setAuthentication(contestCreator, 회장);
    ctfContestEntity = generateCtfContest(contestCreator);
  }

  private static void setAuthentication(MemberEntity contestCreator, MemberJobName memberJob) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(contestCreator.getId(),
            contestCreator.getPassword(),
            List.of(new SimpleGrantedAuthority(memberJob.getJobName()))));
  }

  @Test
  @DisplayName("문제 생성 테스트")
  void createChallenge() {
    CtfChallengeAdminDto result = createStandardChallenge(1234L, "flag", "content", "title", 123L);
    CtfFlagEntity flag = ctfFlagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
        result.getChallengeId(), CtfUtilService.VIRTUAL_TEAM_ID).orElseThrow();

    assertThat(result.getFlag()).isEqualTo("flag");
    assertThat(result.getRemainedSubmitCount()).isEqualTo(123L);
    assertThat(result.getType().getId()).isEqualTo(getStandardType().getId());
    assertThat(result.getDynamicInfo().getMaxScore()).isNull();
    assertThat(result.getDynamicInfo().getMinScore()).isNull();
    assertThat(result.getContent()).isEqualTo("content");
    assertThat(result.getTitle()).isEqualTo("title");
    assertThat(result.getScore()).isEqualTo(1234L);
    assertThat(result.getCategory().getId()).isEqualTo(getWebCategory().getId());
    assertThat(result.getContestId()).isEqualTo(ctfContestEntity.getId());
    assertThat(flag.getRemainedSubmitCount()).isEqualTo(123L);
    assertThat(flag.getIsCorrect()).isEqualTo(false);
  }

  private CtfChallengeAdminDto createStandardChallenge(long score) {
    return createStandardChallenge(score, getRandomUUID(), getRandomUUID(), getRandomUUID(), 15L);
  }

  private CtfChallengeAdminDto createStandardChallenge(long score, String flag, String content,
      String title, long maxSubmitCount) {
    setAuthentication(contestCreator, 회장);
    CtfChallengeAdminDto challengeAdminDto = CtfChallengeAdminDto.builder()
        .isSolvable(true)
        .flag(flag)
        .type(getStandardType())
        .dynamicInfo(null)
        .content(content)
        .title(title)
        .score(score)
        .category(getWebCategory())
        .contestId(ctfContestEntity.getId())
        .maxSubmitCount(maxSubmitCount)
        .build();

    return ctfAdminService.createChallenge(challengeAdminDto);
  }

  private static CtfChallengeCategoryDto getWebCategory() {
    return CtfChallengeCategoryDto.builder()
        .id(WEB.getId())
        .name(WEB.getName())
        .build();
  }

  private static CtfChallengeTypeDto getStandardType() {
    return CtfChallengeTypeDto.builder()
        .id(STANDARD.getId())
        .name(STANDARD.getName())
        .build();
  }

  @Test
  @DisplayName("[시나리오1] STANDRAD 문제 삭제 시 점수 반영 제대로 되는지 테스트")
  void deleteProblem_scenario1() {
    CtfChallengeAdminDto challenge1 = createStandardChallenge(100L);
    CtfChallengeAdminDto challenge2 = createStandardChallenge(200L);
    CtfChallengeAdminDto challenge3 = createStandardChallenge(400L);

    MemberEntity user1 = generateMemberEntity(회원, 정회원, 일반회원);
    CtfTeamDetailDto team1 = createCtfTeam(user1); // 1번, 2번 문제 해결
    MemberEntity user2 = generateMemberEntity(회원, 정회원, 일반회원);
    CtfTeamDetailDto team2 = createCtfTeam(user2); // 2번, 3번 문제 해결
    MemberEntity user3 = generateMemberEntity(회원, 정회원, 일반회원);
    CtfTeamDetailDto team3 = createCtfTeam(user3); // 1번, 2번, 3번 모두 해결

    assertThat(solveChallenge(challenge1, user1)).isTrue();
    assertThat(solveChallenge(challenge2, user1)).isTrue();
    assertThat(solveChallenge(challenge2, user2)).isTrue();
    assertThat(solveChallenge(challenge3, user2)).isTrue();
    assertThat(solveChallenge(challenge1, user3)).isTrue();
    assertThat(solveChallenge(challenge2, user3)).isTrue();
    assertThat(solveChallenge(challenge3, user3)).isTrue();

    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(300L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(600L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(700L);

    em.flush();
    em.clear();

    deleteCtfChallenge(challenge1.getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(200L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(600L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(600L);

    deleteCtfChallenge(challenge2.getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(0L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(400L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(400L);

    CtfChallengeAdminDto challenge4 = createStandardChallenge(800L);

    assertThat(solveChallenge(challenge4, user1)).isTrue();
    assertThat(solveChallenge(challenge4, user2)).isTrue();

    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(800L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(1200L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(400L);

    em.flush();
    em.clear();

    deleteCtfChallenge(challenge3.getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(800L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(800L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(0L);

    deleteCtfChallenge(challenge4.getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(0L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(0L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(0L);
  }

  private void deleteCtfChallenge(long challengeId) {
    setAuthentication(contestCreator, 회장);
    try {
      ctfAdminService.deleteProblem(challengeId);
    } catch (AccessDeniedException e) {
      throw new RuntimeException("내가 왜 검사 예외를 썼을까... ㅠㅠㅠ", e);
    }
  }

  private CtfTeamDetailDto createCtfTeam(MemberEntity teamLeader) {
    setAuthentication(teamLeader, 회원);
    return ctfTeamService.createTeam(CtfTeamDetailDto.builder()
        .contestId(ctfContestEntity.getId())
        .name(getRandomUUID())
        .description(getRandomUUID())
        .build()
    );
  }

  private boolean solveChallenge(CtfChallengeAdminDto challengeInfo, MemberEntity solvedUser) {
    setAuthentication(solvedUser, 회원);
    CtfFlagDto result = ctfChallengeService.checkFlag(challengeInfo.getChallengeId(),
        CtfFlagDto.builder()
            .content(challengeInfo.getFlag())
            .build()
    );
    return result.getIsCorrect();
  }

  private static String getRandomUUID() {
    return UUID.randomUUID().toString();
  }
}