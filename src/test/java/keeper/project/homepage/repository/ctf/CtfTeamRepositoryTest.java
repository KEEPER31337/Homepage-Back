package keeper.project.homepage.repository.ctf;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CtfTeamRepositoryTest extends CtfTestHelper {

  @Test
  @DisplayName("CTF Team 테스트")
  void testTeam() {
    // given
    final long epochTime = System.nanoTime();
    String name = "name_" + epochTime;
    String desc = "desc_" + epochTime;
    long score = 1000L;
    MemberEntity member = memberRepository.getById(1L);
    CtfContestEntity contest = generateCtfContest(member);

    // when
    CtfTeamEntity team = CtfTeamEntity.builder()
        .name(name)
        .description(desc)
        .registerTime(LocalDateTime.now())
        .creator(member)
        .score(score)
        .ctfContestEntity(contest)
        .build();
    ctfTeamRepository.save(team);
    CtfTeamEntity findTeam = ctfTeamRepository.getById(team.getId());

    // then
    assertThat(findTeam.getName()).isEqualTo(name);
    assertThat(findTeam.getDescription()).isEqualTo(desc);
    assertThat(findTeam.getCreator().getId()).isEqualTo(member.getId());
    assertThat(findTeam.getScore()).isEqualTo(score);
    assertThat(findTeam.getCtfContestEntity().getId()).isEqualTo(contest.getId());
  }
}