package keeper.project.homepage.ctf.repository;

import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.MISC;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.STANDARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeHasCtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CtfFlagRepositoryTest extends CtfTestHelper {

  private CtfFlagEntity generateFlag() {
    return generateFlag(100L);
  }

  private CtfFlagEntity generateFlag(long remainedSubmitCount) {
    final long epochTime = System.nanoTime();
    String content = "content_" + epochTime;
    boolean isCorrect = true;
    LocalDateTime now = LocalDateTime.now();
    MemberEntity member = memberRepository.getById(1L);
    CtfContestEntity contest = generateCtfContest(member);
    CtfTeamEntity ctfTeam = generateCtfTeam(contest, member, 0L);

    CtfChallengeEntity ctfChallenge = generateCtfChallenge(contest, STANDARD, 1000L);
    return CtfFlagEntity.builder()
        .content(content)
        .ctfTeamEntity(ctfTeam)
        .ctfChallengeEntity(ctfChallenge)
        .isCorrect(isCorrect)
        .lastTryTime(now)
        .remainedSubmitCount(remainedSubmitCount)
        .build();
  }

  @Test
  @DisplayName("CTF Flag 정상 저장 테스트")
  void flagSaveCorrectlyTest() {
    // given
    CtfFlagEntity flag = generateFlag();

    // when
    ctfFlagRepository.save(flag);
    CtfFlagEntity findFlag = ctfFlagRepository.getById(flag.getId());

    // then
    assertThat(findFlag.getContent()).isEqualTo(flag.getContent());
    assertThat(findFlag.getCtfTeamEntity().getId()).isEqualTo(flag.getCtfTeamEntity().getId());
    assertThat(findFlag.getCtfChallengeEntity().getId()).isEqualTo(
        flag.getCtfChallengeEntity().getId());
    assertThat(findFlag.getIsCorrect()).isEqualTo(flag.getIsCorrect());
    assertThat(findFlag.getLastTryTime()).isEqualTo(flag.getLastTryTime());
    assertThat(findFlag.getRemainedSubmitCount()).isEqualTo(flag.getRemainedSubmitCount());
  }

  @ParameterizedTest
  @ValueSource(ints = {100, 2, 1})
  @DisplayName("남은 제출 횟수가 정상적으로 줄어드는지 테스트")
  void decreaseRemainingCount(int count) {
    CtfFlagEntity flag = generateFlag(count);
    flag.decreaseSubmitCount();
    assertThat(flag.getRemainedSubmitCount()).isEqualTo(count - 1);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -1234})
  @DisplayName("남은 제출 횟수가 0 이하일 때 오류를 발생시키는지 테스트")
  void decreaseRemainingCount0Test(int count) {
    CtfFlagEntity flag = generateFlag(count);
    assertThatThrownBy(flag::decreaseSubmitCount)
        .isInstanceOf(IllegalStateException.class);
  }

}