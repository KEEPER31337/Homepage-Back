package keeper.project.homepage.util.service;

import static keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity.DYNAMIC;

import java.util.List;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfDynamicChallengeInfoEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.ctf.CtfTeamHasMemberEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.ctf.CustomContestNotFoundException;
import keeper.project.homepage.exception.ctf.CustomCtfChallengeNotFoundException;
import keeper.project.homepage.exception.ctf.CustomCtfTeamNotFoundException;
import keeper.project.homepage.repository.ctf.CtfChallengeRepository;
import keeper.project.homepage.repository.ctf.CtfContestRepository;
import keeper.project.homepage.repository.ctf.CtfFlagRepository;
import keeper.project.homepage.repository.ctf.CtfTeamHasMemberRepository;
import keeper.project.homepage.repository.ctf.CtfTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
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
  public static final String VIRTUAL_TEAM_NAME = "virtual_ctf_team";

  private final CtfContestRepository contestRepository;
  private final CtfChallengeRepository challengeRepository;
  private final CtfTeamRepository teamRepository;
  private final CtfTeamHasMemberRepository teamHasMemberRepository;
  private final CtfFlagRepository flagRepository;

  public void checkVirtualContest(Long ctfId) {
    if (VIRTUAL_CONTEST_ID.equals(ctfId)) {
      throw new CustomContestNotFoundException();
    }
  }

  public void checkVirtualProblem(Long probId) {
    if (VIRTUAL_PROBLEM_ID.equals(probId)) {
      throw new CustomCtfChallengeNotFoundException();
    }
  }

  public void checkVirtualTeam(Long teamId) {
    if (VIRTUAL_TEAM_ID.equals(teamId)) {
      throw new CustomCtfTeamNotFoundException();
    }
  }

  public void checkVirtualTeamByName(String teamName) {
    if (VIRTUAL_TEAM_NAME.equals(teamName)) {
      throw new CustomCtfTeamNotFoundException();
    }
  }

  public boolean isTypeDynamic(CtfChallengeEntity challenge) {
    return DYNAMIC.getId().equals(challenge.getCtfChallengeTypeEntity().getId());
  }

  public void checkJoinable(Long ctfId) {
    if (!isJoinable(ctfId)) {
      throw new AccessDeniedException("해당 CTF는 종료되었거나 현재 접근이 불가합니다.");
    }
  }

  public boolean isJoinable(Long ctfId) {
    return contestRepository.findById(ctfId)
        .orElseThrow(CustomContestNotFoundException::new)
        .getIsJoinable();
  }

  public void setAllDynamicScore() {
    List<CtfChallengeEntity> challengeEntityList = challengeRepository
        .findAllByCtfChallengeTypeEntityId(DYNAMIC.getId());

    challengeEntityList.forEach(this::setDynamicScore);
  }

  public CtfTeamHasMemberEntity getTeamHasMemberEntity(Long ctfId, Long memberId) {
    List<CtfTeamHasMemberEntity> teamHasMemberEntityList = teamHasMemberRepository
        .findAllByMemberId(memberId);
    CtfTeamHasMemberEntity teamHasMemberEntity = teamHasMemberEntityList.stream()
        .filter(teamHasMember -> ctfId.equals(
            teamHasMember.getTeam().getCtfContestEntity().getId()))
        .findFirst().orElseThrow(() -> new CustomCtfTeamNotFoundException("가입한 팀을 찾을 수 없습니다."));
    return teamHasMemberEntity;
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
    Long allTeamCount = teamRepository.countByIdIsNotAndCtfContestEntity(VIRTUAL_TEAM_ID,
        challenge.getCtfContestEntity());
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
    return (long) ((minScore - maxScore) * (solvedTeamCount / (double) allTeamCount) *
        (solvedTeamCount / (double) allTeamCount) + maxScore);
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
