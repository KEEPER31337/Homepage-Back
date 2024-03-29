package keeper.project.homepage.ctf.repository;

import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.SYSTEM;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.STANDARD;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CtfChallengeRepositoryTest extends CtfTestHelper {

  @Test
  @DisplayName("CTF challenge 테스트")
  public void saveChallengeList() {
    // given
    final long epochTime = System.nanoTime();
    String name = "name_" + epochTime;
    String desc = "desc_" + epochTime;
    MemberEntity member = memberRepository.getById(1L);
    boolean isSolvable = true;
    CtfChallengeTypeEntity ctfChallengeTypeEntity = ctfChallengeTypeRepository.getById(
        STANDARD.getId());
    Long score = 1000L;
    CtfContestEntity contest = generateCtfContest(member);

    // when
    CtfChallengeEntity challenge = CtfChallengeEntity.builder()
        .name(name)
        .description(desc)
        .registerTime(LocalDateTime.now())
        .creator(member) // Virtual Member
        .isSolvable(isSolvable)
        .ctfChallengeTypeEntity(ctfChallengeTypeEntity)
        .ctfChallengeHasCtfChallengeCategoryList(new ArrayList<>())
        .score(score)
        .ctfContestEntity(contest)
        .ctfFlagEntity(new ArrayList<>())
        .maxSubmitCount(123L)
        .build();
    ctfChallengeRepository.save(challenge);
    CtfChallengeEntity findChallenge = ctfChallengeRepository.getById(challenge.getId());

    // then
    assertThat(findChallenge.getName()).isEqualTo(name);
    assertThat(findChallenge.getDescription()).isEqualTo(desc);
    assertThat(findChallenge.getCreator()).isEqualTo(member);
    assertThat(findChallenge.getIsSolvable()).isEqualTo(isSolvable);
    assertThat(findChallenge.getCtfChallengeTypeEntity()).isEqualTo(ctfChallengeTypeEntity);
    assertThat(findChallenge.getScore()).isEqualTo(score);
    assertThat(findChallenge.getCtfContestEntity()).isEqualTo(contest);
  }

  @Test
  @DisplayName("CTF challenge 매우 긴 description 테스트")
  public void saveLargeDescriptionChallengeList() {
    // given
    final long epochTime = System.nanoTime();
    String name = "name_" + epochTime;
    String desc = generateDummyString(20000);
    MemberEntity member = memberRepository.getById(1L);
    boolean isSolvable = true;
    CtfChallengeTypeEntity ctfChallengeTypeEntity = ctfChallengeTypeRepository.getById(
        STANDARD.getId());
    Long score = 1000L;
    CtfContestEntity contest = generateCtfContest(member);

    // when
    CtfChallengeEntity challenge = CtfChallengeEntity.builder()
        .name(name)
        .description(desc)
        .registerTime(LocalDateTime.now())
        .creator(member) // Virtual Member
        .isSolvable(isSolvable)
        .ctfChallengeTypeEntity(ctfChallengeTypeEntity)
        .ctfChallengeHasCtfChallengeCategoryList(new ArrayList<>())
        .score(score)
        .ctfContestEntity(contest)
        .ctfFlagEntity(new ArrayList<>())
        .maxSubmitCount(123L)
        .build();
    ctfChallengeRepository.save(challenge);
    CtfChallengeEntity findChallenge = ctfChallengeRepository.getById(challenge.getId());

    // then
    assertThat(findChallenge.getName()).isEqualTo(name);
    assertThat(findChallenge.getDescription()).isEqualTo(desc);
    assertThat(findChallenge.getCreator()).isEqualTo(member);
    assertThat(findChallenge.getIsSolvable()).isEqualTo(isSolvable);
    assertThat(findChallenge.getCtfChallengeTypeEntity()).isEqualTo(ctfChallengeTypeEntity);
    assertThat(findChallenge.getScore()).isEqualTo(score);
    assertThat(findChallenge.getCtfContestEntity()).isEqualTo(contest);
  }

  private String generateDummyString(int stringSize) {
    return "0".repeat(stringSize);
  }
}