package keeper.project.homepage.repository.ctf;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CtfContestRepositoryTest extends CtfTestHelper {

  @Test
  @DisplayName("CTF contest 테스트")
  void testContest() {
    // given
    final long epochTime = System.nanoTime();
    String name = "name_" + epochTime;
    String desc = "desc_" + epochTime;
    MemberEntity member = memberRepository.getById(1L);
    boolean isJoinabe = true;

    // when
    CtfContestEntity contest = CtfContestEntity.builder()
        .name(name)
        .description(desc)
        .registerTime(LocalDateTime.now())
        .creator(member) // Virtual Member
        .isJoinable(isJoinabe)
        .build();
    ctfContestRepository.save(contest);
    CtfContestEntity findContest = ctfContestRepository.getById(contest.getId());

    // then
    assertThat(findContest.getName()).isEqualTo(name);
    assertThat(findContest.getDescription()).isEqualTo(desc);
    assertThat(findContest.getCreator()).isEqualTo(member);
    assertThat(findContest.getIsJoinable()).isEqualTo(isJoinabe);
  }
}
