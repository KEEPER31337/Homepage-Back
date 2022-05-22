package keeper.project.homepage.util.service;

import static keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity.DYNAMIC;

import java.util.List;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfDynamicChallengeInfoEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.exception.ctf.CustomContestNotFoundException;
import keeper.project.homepage.exception.ctf.CustomCtfChallengeNotFoundException;
import keeper.project.homepage.repository.ctf.CtfChallengeRepository;
import keeper.project.homepage.repository.ctf.CtfFlagRepository;
import keeper.project.homepage.repository.ctf.CtfTeamHasMemberRepository;
import keeper.project.homepage.repository.ctf.CtfTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CtfUtilService {

  public static final String PROBLEM_MAKER_JOB = "ROLE_출제자";
  public static final Long VIRTUAL_CONTEST_ID = 1L;
  public static final Long VIRTUAL_PROBLEM_ID = 1L;
  public static final Long VIRTUAL_SUBMIT_LOG_ID = 1L;
  public static final Long VIRTUAL_TEAM_ID = 1L;

  private final CtfChallengeRepository challengeRepository;
  private final CtfTeamRepository teamRepository;
  private final CtfFlagRepository flagRepository;

  public void checkVirtualContest(Long ctfId) {
    if (ctfId.equals(VIRTUAL_CONTEST_ID)) {
      throw new CustomContestNotFoundException();
    }
  }

  public void checkVirtualProblem(Long probId) {
    if (probId.equals(VIRTUAL_PROBLEM_ID)) {
      throw new CustomCtfChallengeNotFoundException();
    }
  }

  public boolean isTypeDynamic(CtfChallengeEntity challenge) {
    return challenge.getCtfChallengeTypeEntity().getId().equals(
        DYNAMIC.getId());
  }

  public void setDynamicScore(CtfChallengeEntity challenge) {
    CtfDynamicChallengeInfoEntity dynamicInfo = challenge.getDynamicChallengeInfoEntity();
    if ((!isTypeDynamic(challenge)) || (dynamicInfo == null)) {
      return;
    }

    // 문제 점수 조정
    List<CtfFlagEntity> ctfSolvedList = flagRepository.
        findAllByCtfChallengeEntityIdAndIsCorrect(challenge.getId(), true);
    Long originalScore = challenge.getScore();
    Long allTeamCount = teamRepository.countByIdIsNot(VIRTUAL_TEAM_ID);
    Long solvedTeamCount = (long) ctfSolvedList.size();
    Long maxScore = dynamicInfo.getMaxScore();
    Long minScore = dynamicInfo.getMinScore();
    long dynamicScore = getDynamicScore(allTeamCount, solvedTeamCount, maxScore, minScore);
    challenge.setScore(dynamicScore);
    challengeRepository.save(challenge);

    // 해당 문제를 맞춘 팀 별로 점수 조정
    setTeamDynamicScore(ctfSolvedList, originalScore, dynamicScore);
  }

  private long getDynamicScore(Long allTeamCount, Long solvedTeamCount, Long maxScore,
      Long minScore) {
    return (minScore - maxScore) *
        (solvedTeamCount / allTeamCount) * (solvedTeamCount / allTeamCount) + maxScore;
  }

  private void setTeamDynamicScore(List<CtfFlagEntity> ctfSolvedList, Long originalScore,
      long dynamicScore) {
    List<CtfTeamEntity> solvedTeamList = ctfSolvedList.stream()
        .map(CtfFlagEntity::getCtfTeamEntity).toList();
    for (CtfTeamEntity solvedTeam : solvedTeamList) {
      solvedTeam.setScore(solvedTeam.getScore() - originalScore + dynamicScore);
    }
    teamRepository.saveAll(solvedTeamList);
  }
}
