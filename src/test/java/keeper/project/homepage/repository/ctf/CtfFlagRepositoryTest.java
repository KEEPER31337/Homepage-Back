package keeper.project.homepage.repository.ctf;

import static org.assertj.core.api.Assertions.assertThat;

import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CtfFlagRepositoryTest extends CtfTestHelper {

  @Test
  @DisplayName("CTF Flag 테스트")
  void testContest() {
    // given
    final long epochTime = System.nanoTime();
    String content = "content_" + epochTime;
    boolean isCorrect = true;
    MemberEntity member = memberRepository.getById(1L);
    CtfContestEntity contest = generateCtfContest(member);
    CtfTeamEntity ctfTeam = generateCtfTeam(contest, member, 0L);
    CtfChallengeEntity ctfChallenge = generateCtfChallenge(contest, CtfChallengeType.STANDARD,
        CtfChallengeCategory.Misc, 1000L);

    // when
    CtfFlagEntity flag = CtfFlagEntity.builder()
        .content(content)
        .ctfTeamEntity(ctfTeam)
        .ctfChallengeEntity(ctfChallenge)
        .isCorrect(isCorrect)
        .build();
    ctfFlagRepository.save(flag);
    CtfFlagEntity findFlag = ctfFlagRepository.getById(flag.getId());

    // then
    assertThat(findFlag.getContent()).isEqualTo(content);
    assertThat(findFlag.getCtfTeamEntity().getId()).isEqualTo(ctfTeam.getId());
    assertThat(findFlag.getCtfChallengeEntity().getId()).isEqualTo(ctfChallenge.getId());
    assertThat(findFlag.getIsCorrect()).isEqualTo(isCorrect);
  }
}