package keeper.project.homepage.repository.ctf;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfSubmitLogEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CtfSubmitLogRepositoryTest extends CtfTestHelper {

  @Test
  @DisplayName("CTF Submit Log 테스트")
  void testContest() {
    // given
    final long epochTime = System.nanoTime();
    boolean isCorrect = true;
    String submitFlag = "submitFlag_" + epochTime;
    MemberEntity member = memberRepository.getById(1L);
    CtfContestEntity contest = generateCtfContest(member);
    CtfTeamEntity ctfTeam = generateCtfTeam(contest, member, 0L);
    CtfChallengeEntity ctfChallenge = generateCtfChallenge(contest, CtfChallengeType.STANDARD,
        CtfChallengeCategory.Misc, 1000L);

    // when
    CtfSubmitLogEntity submitLog = CtfSubmitLogEntity.builder()
        .submitTime(LocalDateTime.now())
        .flagSubmitted(submitFlag)
        .isCorrect(isCorrect)
        .teamName(ctfTeam.getName())
        .submitterLoginId(member.getLoginId())
        .submitterRealname(member.getRealName())
        .challengeName(ctfChallenge.getName())
        .contestName(contest.getName())
        .contest(contest)
        .build();
    ctfSubmitLogRepository.save(submitLog);
    CtfSubmitLogEntity findSubmitLog = ctfSubmitLogRepository.getById(submitLog.getId());

    // then

    assertThat(findSubmitLog.getTeamName()).isEqualTo(ctfTeam.getName());
    assertThat(findSubmitLog.getSubmitterLoginId()).isEqualTo(member.getLoginId());
    assertThat(findSubmitLog.getSubmitterRealname()).isEqualTo(member.getRealName());
    assertThat(findSubmitLog.getChallengeName()).isEqualTo(ctfChallenge.getName());
    assertThat(findSubmitLog.getContestName()).isEqualTo(contest.getName());
    assertThat(findSubmitLog.getFlagSubmitted()).isEqualTo(submitFlag);
    assertThat(findSubmitLog.getIsCorrect()).isEqualTo(isCorrect);
  }
}