package keeper.project.homepage.util.service;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity.CRYPTO;
import static keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity.FORENSIC;
import static keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity.MISC;
import static keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity.SYSTEM;
import static keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity.WEB;
import static keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity.DYNAMIC;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import keeper.project.homepage.controller.ctf.CtfSpringTestHelper;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class CtfUtilServiceTest extends CtfSpringTestHelper {

  private static final int VALID_TEAM_COUNT = 5;
  private static final int VALID_CHALLENGE_COUNT = 3;

  CtfFlagEntity[][] flagEntities = new CtfFlagEntity[VALID_TEAM_COUNT][VALID_CHALLENGE_COUNT];
  List<CtfTeamEntity> validTeamList = new ArrayList<>();
  List<CtfChallengeEntity> validChallengeList = new ArrayList<>();

  MemberEntity creator, member1, member2;
  CtfContestEntity invalidCtf;
  CtfContestEntity validCtf;

  @BeforeEach
  void setUp() {
    creator = generateMemberEntity(회원, 정회원, 일반회원);
    invalidCtf = generateCtfContest(creator, false);
    validCtf = generateCtfContest(creator, true);

    // 참여하지 않는 팀 생성
    member1 = generateMemberEntity(회원, 정회원, 일반회원);
    generateCtfTeam(invalidCtf, member1, 0L);
    member2 = generateMemberEntity(회원, 정회원, 일반회원);
    generateCtfTeam(invalidCtf, member2, 0L);

    // 참여 팀 생성
    validTeamList = new ArrayList<>();
    IntStream.range(0, VALID_TEAM_COUNT).forEach(n ->
        validTeamList.add(generateCtfTeam(validCtf, generateMemberEntity(회원, 정회원, 일반회원), 0L)));

    validChallengeList = new ArrayList<>();
    IntStream.range(0, VALID_CHALLENGE_COUNT).forEach(n -> {
      CtfChallengeEntity challenge = generateCtfChallenge(validCtf, DYNAMIC, FORENSIC, 0L);
      generateDynamicChallengeInfo(challenge, 1000L, 100L);
      validChallengeList.add(challenge);
    });

    IntStream.range(0, validTeamList.size()).forEach(teamIndex ->
        IntStream.range(0, validChallengeList.size()).forEach(challengeIndex -> {
          flagEntities[teamIndex][challengeIndex] = generateCtfFlag(
              validTeamList.get(teamIndex), validChallengeList.get(challengeIndex), false);
        })
    );

    ctfUtilService.setAllDynamicScore();
    validChallengeList.forEach(challenge -> {
      Assertions.assertThat(ctfChallengeRepository.getById(challenge.getId()).getScore())
          .isEqualTo(1000L);
    });
  }

  @Test
  void setAllDynamicScore() {
    // 0 ~ 5번째 팀이 0번째 문제를 맞췄을 때
    int correctTeam = 0;
    int correctChallenge = 0;
    for (; correctTeam < 5; correctTeam++) {
      takeAnswer(flagEntities, correctTeam, correctChallenge, validTeamList, validChallengeList);
      ctfUtilService.setDynamicScore(validChallengeList.get(correctChallenge));
    }

    // 0 ~ 3번째 팀이 1번째 문제를 맞췄을 때
    correctTeam = 0;
    correctChallenge = 1;
    for (; correctTeam < 3; correctTeam++) {
      takeAnswer(flagEntities, correctTeam, correctChallenge, validTeamList, validChallengeList);
      ctfUtilService.setDynamicScore(validChallengeList.get(correctChallenge));
    }

    // 0번째 팀이 2번째 문제를 맞췄을 때
    correctTeam = 0;
    correctChallenge = 2;
    for (; correctTeam < 1; correctTeam++) {
      takeAnswer(flagEntities, correctTeam, correctChallenge, validTeamList, validChallengeList);
      ctfUtilService.setDynamicScore(validChallengeList.get(correctChallenge));
    }

    long[] expectedTeamScore = {100L + 676L + 964L, 100L + 676L, 100L + 676L, 100L, 100L};
    IntStream.range(0, VALID_TEAM_COUNT).forEach(
        n -> Assertions.assertThat(validTeamList.get(n).getScore())
            .isEqualTo(expectedTeamScore[n]));

    long[] expectedChallengeScore = {100L, 676L, 964L};
    IntStream.range(0, VALID_CHALLENGE_COUNT)
        .forEach(n -> Assertions.assertThat(validChallengeList.get(n).getScore())
            .isEqualTo(expectedChallengeScore[n]));
  }

  @Test
  @DisplayName("Dynamic 문제 Score 재정산 테스트")
  void setDynamicScore() {
    // 0 ~ 5번째 팀이 0번째 문제를 맞췄을 때
    int prevCorrectTeam = 4;
    int correctTeam = 0;
    int correctChallenge = 0;
    long[] prevCorrectTeamScore = {0L, 856L, 676L, 424L, 100L};
    long[] correctTeamScore = {964L, 856L, 676L, 424L, 100L};
    long[] challengeScore = {964L, 856L, 676L, 424L, 100L};
    for (; correctTeam < VALID_TEAM_COUNT; correctTeam++) {
      takeAnswer(flagEntities, correctTeam, correctChallenge, validTeamList, validChallengeList);
      ctfUtilService.setDynamicScore(validChallengeList.get(correctChallenge));

      Assertions.assertThat(
              ctfTeamRepository.getById(validTeamList.get(prevCorrectTeam).getId()).getScore())
          .isEqualTo(prevCorrectTeamScore[correctTeam]);
      Assertions.assertThat(
              ctfTeamRepository.getById(validTeamList.get(correctTeam).getId()).getScore())
          .isEqualTo(correctTeamScore[correctTeam]);
      Assertions.assertThat(
              ctfChallengeRepository.getById(validChallengeList.get(correctChallenge).getId())
                  .getScore())
          .isEqualTo(challengeScore[correctTeam]);

      prevCorrectTeam = correctTeam;
    }

    // 0 ~ 3번째 팀이 1번째 문제를 맞췄을 때
    int noAnswerTeam = 4; // 맞추지 못한 팀 점수는 그대로
    correctTeam = 0;
    correctChallenge = 1;
    long[] noAnswerTeamScore2 = {100L, 100L, 100L};
    long[] correctTeamScore2 = {964L + 100L, 856L + 100L, 676L + 100L};
    long[] challengeScore2 = {964L, 856L, 676L};
    for (; correctTeam < 3; correctTeam++) {
      takeAnswer(flagEntities, correctTeam, correctChallenge, validTeamList, validChallengeList);
      ctfUtilService.setDynamicScore(validChallengeList.get(correctChallenge));

      Assertions.assertThat(
              ctfTeamRepository.getById(validTeamList.get(noAnswerTeam).getId()).getScore())
          .isEqualTo(noAnswerTeamScore2[correctTeam]);
      Assertions.assertThat(
              ctfTeamRepository.getById(validTeamList.get(correctTeam).getId()).getScore())
          .isEqualTo(correctTeamScore2[correctTeam]);
      Assertions.assertThat(
              ctfChallengeRepository.getById(validChallengeList.get(correctChallenge).getId())
                  .getScore())
          .isEqualTo(challengeScore2[correctTeam]);
    }
  }

  private void takeAnswer(CtfFlagEntity[][] flagEntities, int correctTeam, int correctChallenge,
      List<CtfTeamEntity> validTeamList, List<CtfChallengeEntity> validChallengeList) {
    flagEntities[correctTeam][correctChallenge].setIsCorrect(true);
    ctfFlagRepository.saveAndFlush(flagEntities[correctTeam][correctChallenge]);
    validTeamList.get(correctTeam).setScore(validTeamList.get(correctTeam).getScore() +
        validChallengeList.get(correctChallenge).getScore());
    ctfTeamRepository.saveAndFlush(validTeamList.get(correctTeam));
  }
}