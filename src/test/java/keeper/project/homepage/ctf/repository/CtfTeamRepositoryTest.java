package keeper.project.homepage.repository.ctf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

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

  @Test
  @DisplayName("CTF Team 중복 테스트")
  void testTeamDuplicate() {
    // given
    final long epochTime = System.nanoTime();
    String name = "name_" + epochTime;
    String desc = "desc_" + epochTime;
    long score = 1000L;
    MemberEntity member = memberRepository.getById(1L);
    CtfContestEntity contest = generateCtfContest(member);

    // when
    ctfTeamRepository.save(CtfTeamEntity.builder()
        .name(name)
        .description(desc)
        .registerTime(LocalDateTime.now())
        .creator(member)
        .score(score)
        .ctfContestEntity(contest)
        .build());

    // then
    assertThatThrownBy(() -> ctfTeamRepository.save(CtfTeamEntity.builder()
        .name(name)
        .description(desc)
        .registerTime(LocalDateTime.now())
        .creator(member)
        .score(score)
        .ctfContestEntity(contest)
        .build()))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("CTF Team 중복이 아닐 경우 테스트")
  void testTeamNotDuplicate() {
    // given
    final long epochTime = System.nanoTime();
    String name1 = "name_" + epochTime;
    String desc1 = "desc_" + epochTime;
    final long epochTime2 = System.nanoTime();
    String name2 = "name_" + epochTime2;
    String desc2 = "desc_" + epochTime2;
    long score = 1000L;
    MemberEntity member = memberRepository.getById(1L);
    CtfContestEntity contest = generateCtfContest(member);
    CtfContestEntity contest2 = generateCtfContest(member);

    // when
    ctfTeamRepository.save(CtfTeamEntity.builder()
        .name(name1)
        .description(desc1)
        .registerTime(LocalDateTime.now())
        .creator(member)
        .score(score)
        .ctfContestEntity(contest)
        .build());

    ctfTeamRepository.save(CtfTeamEntity.builder()
        .name(name2)
        .description(desc2)
        .registerTime(LocalDateTime.now())
        .creator(member)
        .score(score)
        .ctfContestEntity(contest)
        .build());

    ctfTeamRepository.save(CtfTeamEntity.builder()
        .name(name1)
        .description(desc1)
        .registerTime(LocalDateTime.now())
        .creator(member)
        .score(score)
        .ctfContestEntity(contest2)
        .build());

    ctfTeamRepository.save(CtfTeamEntity.builder()
        .name(name2)
        .description(desc2)
        .registerTime(LocalDateTime.now())
        .creator(member)
        .score(score)
        .ctfContestEntity(contest2)
        .build());
  }
}