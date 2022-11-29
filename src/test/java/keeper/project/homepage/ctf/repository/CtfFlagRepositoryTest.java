package keeper.project.homepage.ctf.repository;

import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.MISC;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.STANDARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CtfFlagRepositoryTest extends CtfTestHelper {

  private CtfFlagEntity generateFlag() {
    return generateFlag(100L);
  }

  private CtfFlagEntity generateFlag(long remainingSubmitCount) {
    final long epochTime = System.nanoTime();
    String content = "content_" + epochTime;
    boolean isCorrect = true;
    LocalDateTime now = LocalDateTime.now();
    MemberEntity member = memberRepository.getById(1L);
    CtfContestEntity contest = generateCtfContest(member);
    CtfTeamEntity ctfTeam = generateCtfTeam(contest, member, 0L);
    CtfChallengeEntity ctfChallenge = generateCtfChallenge(contest, STANDARD, MISC, 1000L);
    return CtfFlagEntity.builder()
        .content(content)
        .ctfTeamEntity(ctfTeam)
        .ctfChallengeEntity(ctfChallenge)
        .isCorrect(isCorrect)
        .lastSubmitTime(now)
        .remainingSubmitCount(remainingSubmitCount)
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
    assertThat(findFlag.getLastSubmitTime()).isEqualTo(flag.getLastSubmitTime());
    assertThat(findFlag.getRemainingSubmitCount()).isEqualTo(flag.getRemainingSubmitCount());
  }

  @Test
  @DisplayName("남은 제출 횟수가 100일 때 정상적으로 줄어드는지 테스트")
  void decreaseRemainingCount100Test() {
    CtfFlagEntity flag = generateFlag(100L);
    flag.decreaseSubmitCount();
    assertThat(flag.getRemainingSubmitCount()).isEqualTo(99);
  }

  @Test
  @DisplayName("남은 제출 횟수가 2일 때 정상적으로 줄어드는지 테스트")
  void decreaseRemainingCount2Test() {
    CtfFlagEntity flag = generateFlag(2L);
    flag.decreaseSubmitCount();
    assertThat(flag.getRemainingSubmitCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("남은 제출 횟수가 1일 때 정상적으로 줄어드는지 테스트")
  void decreaseRemainingCount1Test() {
    CtfFlagEntity flag = generateFlag(1);
    flag.decreaseSubmitCount();
    assertThat(flag.getRemainingSubmitCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("남은 제출 횟수가 0일 때 오류를 발생시키는지 테스트")
  void decreaseRemainingCount0Test() {
    CtfFlagEntity flag = generateFlag(0);
    assertThatThrownBy(flag::decreaseSubmitCount)
        .isInstanceOf(IllegalStateException.class);
  }

}