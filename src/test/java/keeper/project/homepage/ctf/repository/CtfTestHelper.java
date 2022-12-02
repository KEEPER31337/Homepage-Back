package keeper.project.homepage.ctf.repository;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfSubmitLogEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.repository.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CtfTestHelper {

  @Autowired
  protected CtfChallengeCategoryRepository ctfChallengeCategoryRepository;

  @Autowired
  protected CtfChallengeTypeRepository ctfChallengeTypeRepository;

  @Autowired
  protected CtfChallengeRepository ctfChallengeRepository;

  @Autowired
  protected CtfContestRepository ctfContestRepository;

  @Autowired
  protected CtfFlagRepository ctfFlagRepository;

  @Autowired
  protected CtfSubmitLogRepository ctfSubmitLogRepository;

  @Autowired
  protected CtfTeamRepository ctfTeamRepository;

  @Autowired
  protected MemberRepository memberRepository;

  protected CtfContestEntity generateCtfContest(MemberEntity creator) {
    final long epochTime = System.nanoTime();
    CtfContestEntity entity = CtfContestEntity.builder()
        .name("name_" + epochTime)
        .description("desc_" + epochTime)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .isJoinable(false)
        .build();
    ctfContestRepository.save(entity);
    return entity;

  }

  protected CtfFlagEntity generateCtfFlag(CtfTeamEntity ctfTeam, CtfChallengeEntity ctfChallenge) {
    final long epochTime = System.nanoTime();
    CtfFlagEntity entity = CtfFlagEntity.builder()
        .content("content_" + epochTime)
        .ctfTeamEntity(ctfTeam)
        .ctfChallengeEntity(ctfChallenge)
        .isCorrect(false)
        .lastTryTime(now())
        .build();
    ctfFlagRepository.save(entity);
    return entity;
  }

  protected CtfSubmitLogEntity generateCtfSubmitLog(CtfTeamEntity ctfTeam, MemberEntity submitter,
      CtfChallengeEntity ctfChallengeEntity, String submitFlag) {
    CtfSubmitLogEntity entity = CtfSubmitLogEntity.builder()
        .submitTime(LocalDateTime.now())
        .teamName(ctfTeam.getName())
        .submitterLoginId(submitter.getLoginId())
        .submitterRealname(submitter.getRealName())
        .challengeName(ctfChallengeEntity.getName())
        .contestName(ctfTeam.getCtfContestEntity().getName())
        .flagSubmitted(submitFlag)
        .isCorrect(false)
        .build();
    ctfSubmitLogRepository.save(entity);
    return entity;
  }

  protected CtfTeamEntity generateCtfTeam(CtfContestEntity ctfContestEntity, MemberEntity creator,
      Long score) {
    final long epochTime = System.nanoTime();
    CtfTeamEntity entity = CtfTeamEntity.builder()
        .name("name_" + epochTime)
        .description("desc_" + epochTime)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .score(score)
        .ctfContestEntity(ctfContestEntity)
        .build();
    ctfTeamRepository.save(entity);
    return entity;
  }

  protected CtfChallengeEntity generateCtfChallenge(
      CtfContestEntity ctfContestEntity,
      CtfChallengeType ctfChallengeType,
      CtfChallengeCategory ctfChallengeCategory,
      Long score) {
    final long epochTime = System.nanoTime();
    CtfChallengeTypeEntity ctfChallengeTypeEntity = ctfChallengeTypeRepository.getById(
        ctfChallengeType.getId());
    CtfChallengeCategoryEntity ctfChallengeCategoryEntity = ctfChallengeCategoryRepository.getById(
        ctfChallengeCategory.getId());
    CtfChallengeEntity entity = CtfChallengeEntity.builder()
        .name("name_" + epochTime)
        .description("desc_" + epochTime)
        .registerTime(LocalDateTime.now())
        .creator(memberRepository.getById(1L)) // Virtual Member
        .isSolvable(false)
        .ctfChallengeTypeEntity(ctfChallengeTypeEntity)
        .ctfChallengeCategoryEntity(ctfChallengeCategoryEntity)
        .score(score)
        .ctfContestEntity(ctfContestEntity)
        .maxSubmitCount(123L)
        .build();
    ctfChallengeRepository.save(entity);
    return entity;
  }

}
