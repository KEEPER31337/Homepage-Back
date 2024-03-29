package keeper.project.homepage.ctf.service;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회장;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.FORENSIC;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.WEB;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import keeper.project.homepage.ctf.controller.CtfSpringTestHelper;
import keeper.project.homepage.ctf.dto.CtfChallengeAdminDto;
import keeper.project.homepage.ctf.dto.CtfChallengeCategoryDto;
import keeper.project.homepage.ctf.dto.CtfDynamicChallengeInfoDto;
import keeper.project.homepage.ctf.dto.CtfFlagDto;
import keeper.project.homepage.ctf.dto.CtfTeamDetailDto;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory;
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
  @DisplayName("문제 생성 테스트 - 카테고리 1개")
  void createChallenge() {
    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(WEB);
    CtfChallengeAdminDto result = createStandardChallenge(1234L, "flag", "content", "title",
        categories, 123L);
    CtfFlagEntity flag = ctfFlagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
        result.getChallengeDto()
            .getCommonChallengeDto()
            .getChallengeId(), CtfUtilService.VIRTUAL_TEAM_ID).orElseThrow();

    assertThat(result.getFlag()).isEqualTo("flag");
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getRemainedSubmitCount()).isEqualTo(123L);
    assertThat(result.getType().getId()).isEqualTo(getStandardType().getId());
    assertThat(result.getDynamicInfo().getMaxScore()).isNull();
    assertThat(result.getDynamicInfo().getMinScore()).isNull();
    assertThat(result.getChallengeDto()
        .getContent()).isEqualTo("content");
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getTitle()).isEqualTo("title");
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getScore()).isEqualTo(1234L);
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getCategories().get(0).getId()).isEqualTo(categories.get(0).getId());
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getCategories().size()).isEqualTo(1);
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getContestId()).isEqualTo(ctfContestEntity.getId());
    assertThat(flag.getRemainedSubmitCount()).isEqualTo(123L);
    assertThat(flag.getIsCorrect()).isEqualTo(false);
  }

  @Test
  @DisplayName("문제 생성 테스트 - 카테고리 2개이상")
  void createChallengeHasManyCategory() {
    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(WEB);
    categories.add(FORENSIC);
    CtfChallengeAdminDto result = createStandardChallenge(1234L, "flag", "content", "title",
        categories, 123L);
    CtfFlagEntity flag = ctfFlagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
        result.getChallengeDto()
            .getCommonChallengeDto()
            .getChallengeId(), CtfUtilService.VIRTUAL_TEAM_ID).orElseThrow();

    assertThat(result.getFlag()).isEqualTo("flag");
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getRemainedSubmitCount()).isEqualTo(123L);
    assertThat(result.getType().getId()).isEqualTo(getStandardType().getId());
    assertThat(result.getDynamicInfo().getMaxScore()).isNull();
    assertThat(result.getDynamicInfo().getMinScore()).isNull();
    assertThat(result.getChallengeDto()
        .getContent()).isEqualTo("content");
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getTitle()).isEqualTo("title");
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getScore()).isEqualTo(1234L);
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getCategories().get(0).getId()).isEqualTo(categories.get(0).getId());
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getCategories().get(1).getId()).isEqualTo(categories.get(1).getId());
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getCategories().size()).isEqualTo(2);
    assertThat(result.getChallengeDto()
        .getCommonChallengeDto()
        .getContestId()).isEqualTo(ctfContestEntity.getId());
    assertThat(flag.getRemainedSubmitCount()).isEqualTo(123L);
    assertThat(flag.getIsCorrect()).isEqualTo(false);
  }

  private CtfChallengeAdminDto createStandardChallenge(long score,
      List<CtfChallengeCategory> categories) {
    return createStandardChallenge(score, getRandomUUID(), getRandomUUID(), getRandomUUID(),
        categories, 15L);
  }

  private CtfChallengeAdminDto createStandardChallenge(long score, String flag, String content,
      String title, List<CtfChallengeCategory> categories, long maxSubmitCount) {
    setAuthentication(contestCreator, 회장);

    List<CtfChallengeCategoryDto> categoryDtos = categories
        .stream()
        .map(ctfChallengeCategory -> CtfChallengeCategoryDto
            .builder()
            .id(ctfChallengeCategory.getId())
            .name(ctfChallengeCategory.getName()).build())
        .toList();

    CtfChallengeAdminDto challengeAdminDto = generateChallengeAdminDto(score, flag, content, title,
        maxSubmitCount, ctfContestEntity.getId(), categoryDtos, getStandardType());

    return ctfAdminService.createChallenge(challengeAdminDto);
  }

  private CtfChallengeAdminDto createDynamicChallenge(CtfDynamicChallengeInfoDto dynamicScore) {
    return createDynamicChallenge(dynamicScore, getRandomUUID(), getRandomUUID(), getRandomUUID(),
        15L);
  }

  private CtfChallengeAdminDto createDynamicChallenge(CtfDynamicChallengeInfoDto dynamicScore,
      String flag, String content, String title, long maxSubmitCount) {
    setAuthentication(contestCreator, 회장);

    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(WEB);

    List<CtfChallengeCategoryDto> categoryDtos = categories
        .stream()
        .map(ctfChallengeCategory -> CtfChallengeCategoryDto
            .builder()
            .id(ctfChallengeCategory.getId())
            .name(ctfChallengeCategory.getName()).build())
        .toList();

    CtfChallengeAdminDto challengeAdminDto = generateChallengeAdminDto(0L, flag, content, title,
        maxSubmitCount, ctfContestEntity.getId(), categoryDtos, getDynamicType(), dynamicScore);
    return ctfAdminService.createChallenge(challengeAdminDto);
  }

  @Test
  @DisplayName("[시나리오1] STANDRAD 문제 삭제 시 점수 반영 제대로 되는지 테스트")
  void deleteProblem_scenario1() {
    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(WEB);
    CtfChallengeAdminDto challenge1 = createStandardChallenge(100L, categories);
    CtfChallengeAdminDto challenge2 = createStandardChallenge(200L, categories);
    CtfChallengeAdminDto challenge3 = createStandardChallenge(400L, categories);

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

    deleteCtfChallenge(challenge1
        .getChallengeDto()
        .getCommonChallengeDto()
        .getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(200L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(600L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(600L);

    deleteCtfChallenge(challenge2
        .getChallengeDto()
        .getCommonChallengeDto()
        .getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(0L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(400L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(400L);

    CtfChallengeAdminDto challenge4 = createStandardChallenge(800L, categories);

    assertThat(solveChallenge(challenge4, user1)).isTrue();
    assertThat(solveChallenge(challenge4, user2)).isTrue();

    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(800L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(1200L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(400L);

    em.flush();
    em.clear();

    deleteCtfChallenge(challenge3
        .getChallengeDto()
        .getCommonChallengeDto()
        .getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(800L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(800L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(0L);

    deleteCtfChallenge(challenge4
        .getChallengeDto()
        .getCommonChallengeDto()
        .getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(0L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(0L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(0L);
  }

  @Test
  @DisplayName("[시나리오2] DYNAMIC 문제 삭제 시 점수 반영 제대로 되는지 테스트")
  void deleteProblem_scenario2() {
    CtfChallengeAdminDto challenge1 = createDynamicChallenge(getDynamicScore(0L, 1000L));
    CtfChallengeAdminDto challenge2 = createDynamicChallenge(getDynamicScore(0L, 2000L));
    CtfChallengeAdminDto challenge3 = createDynamicChallenge(getDynamicScore(0L, 4000L));

    MemberEntity user1 = generateMemberEntity(회원, 정회원, 일반회원);
    CtfTeamDetailDto team1 = createCtfTeam(user1); // 1번, 2번 문제 해결
    MemberEntity user2 = generateMemberEntity(회원, 정회원, 일반회원);
    CtfTeamDetailDto team2 = createCtfTeam(user2); // 2번, 3번 문제 해결
    MemberEntity user3 = generateMemberEntity(회원, 정회원, 일반회원);
    CtfTeamDetailDto team3 = createCtfTeam(user3); // 1번, 2번, 3번 모두 해결
    MemberEntity user4 = generateMemberEntity(회원, 정회원, 일반회원);
    CtfTeamDetailDto team4 = createCtfTeam(user4); // 문제 해결 안함

    assertThat(solveChallenge(challenge1, user1)).isTrue();
    assertThat(solveChallenge(challenge2, user1)).isTrue();
    assertThat(solveChallenge(challenge2, user2)).isTrue();
    assertThat(solveChallenge(challenge3, user2)).isTrue();
    assertThat(solveChallenge(challenge1, user3)).isTrue();
    assertThat(solveChallenge(challenge2, user3)).isTrue();
    assertThat(solveChallenge(challenge3, user3)).isTrue();

    assertThat(getChallengeScore(challenge1)).isEqualTo(750L);
    assertThat(getChallengeScore(challenge2)).isEqualTo(875L);
    assertThat(getChallengeScore(challenge3)).isEqualTo(3000L);
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(1625L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(3875L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(4625L);
    assertThat(ctfTeamRepository.getById(team4.getId()).getScore()).isEqualTo(0L);

    em.flush();
    em.clear();

    System.out.println(team1.getSolvedChallengeList());
    System.out.println(team2.getSolvedChallengeList());
    System.out.println(team3.getSolvedChallengeList());
    System.out.println(team4.getSolvedChallengeList());
    System.out.println();

    deleteCtfChallenge(challenge1
        .getChallengeDto()
        .getCommonChallengeDto()
        .getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(875L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(3875L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(3875L);

    System.out.println(team1.getSolvedChallengeList());
    System.out.println(team2.getSolvedChallengeList());
    System.out.println(team3.getSolvedChallengeList());
    System.out.println(team4.getSolvedChallengeList());
    System.out.println();

    deleteCtfChallenge(challenge2
        .getChallengeDto()
        .getCommonChallengeDto()
        .getChallengeId());
    assertThat(ctfTeamRepository.getById(team1.getId()).getScore()).isEqualTo(0L);
    assertThat(ctfTeamRepository.getById(team2.getId()).getScore()).isEqualTo(3000L);
    assertThat(ctfTeamRepository.getById(team3.getId()).getScore()).isEqualTo(3000L);

    System.out.println(team1.getSolvedChallengeList());
    System.out.println(team2.getSolvedChallengeList());
    System.out.println(team3.getSolvedChallengeList());
    System.out.println(team4.getSolvedChallengeList());
    System.out.println();
  }

  private Long getChallengeScore(CtfChallengeAdminDto challenge1) {
    return ctfChallengeRepository.getById(challenge1
        .getChallengeDto()
        .getCommonChallengeDto()
        .getChallengeId()).getScore();
  }

  private static CtfDynamicChallengeInfoDto getDynamicScore(long minScore, long maxScore) {
    return CtfDynamicChallengeInfoDto.builder()
        .minScore(minScore)
        .maxScore(maxScore)
        .build();
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
    CtfFlagDto result = ctfChallengeService.checkFlag(challengeInfo
            .getChallengeDto()
            .getCommonChallengeDto()
            .getChallengeId(),
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