package keeper.project.homepage.user.service.ctf;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_PROBLEM_ID;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.ctf.CtfSubmitLogEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.ctf.CustomContestNotFoundException;
import keeper.project.homepage.exception.ctf.CustomCtfChallengeNotFoundException;
import keeper.project.homepage.repository.ctf.CtfChallengeRepository;
import keeper.project.homepage.repository.ctf.CtfContestRepository;
import keeper.project.homepage.repository.ctf.CtfFlagRepository;
import keeper.project.homepage.repository.ctf.CtfSubmitLogRepository;
import keeper.project.homepage.repository.ctf.CtfTeamHasMemberRepository;
import keeper.project.homepage.repository.ctf.CtfTeamRepository;
import keeper.project.homepage.user.dto.ctf.CtfChallengeDto;
import keeper.project.homepage.user.dto.ctf.CtfCommonChallengeDto;
import keeper.project.homepage.user.dto.ctf.CtfFlagDto;
import keeper.project.homepage.util.service.CtfUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CtfChallengeService {

  private final CtfChallengeRepository challengeRepository;
  private final CtfTeamHasMemberRepository teamHasMemberRepository;
  private final CtfTeamRepository teamRepository;
  private final CtfFlagRepository flagRepository;
  private final CtfContestRepository contestRepository;
  private final CtfSubmitLogRepository submitLogRepository;
  private final CtfUtilService ctfUtilService;
  private final AuthService authService;

  public List<CtfCommonChallengeDto> getProblemList(Long ctfId) {

    ctfUtilService.checkVirtualContest(ctfId);
    ctfUtilService.checkJoinable(ctfId);

    CtfTeamEntity myTeam = getTeamEntity(ctfId, authService.getMemberIdByJWT());
    CtfContestEntity contestEntity = getContestEntity(ctfId);

    List<CtfChallengeEntity> solvableChallengeList = getSolvableChallengeList(contestEntity);

    return solvableChallengeList.stream()
        .map(solvableChallenge -> {
          Boolean isMyTeamSolved = isCorrect(getCtfFlagEntity(solvableChallenge, myTeam));
          return CtfCommonChallengeDto.toDto(solvableChallenge, isMyTeamSolved);
        }).toList();
  }

  @Transactional
  public CtfFlagDto checkFlag(Long probId, CtfFlagDto submitFlag) {

    ctfUtilService.checkVirtualProblem(probId);

    Long submitterId = authService.getMemberIdByJWT();
    CtfChallengeEntity submitChallenge = getChallengeEntity(probId);
    CtfTeamEntity submitTeam = getTeamEntity(getIdByChallenge(submitChallenge), submitterId);
    CtfFlagEntity flagEntity = getFlagEntity(probId, submitTeam);

    // 참가 불가능 CTF면 Flag check 안함.
    ctfUtilService.checkJoinable(getIdByChallenge(submitChallenge));

    // 풀 수 없는 문제면 조치 안함.
    if (!isSolvableChallenge(submitChallenge)) {
      throw new AccessDeniedException("풀 수 없는 문제입니다.");
    }

    // 이미 맞췄으면 제출한 flag 정답 유무만 체크하고 DB 갱신 안함.
    if (isCorrect(flagEntity)) {
      submitFlag.setIsCorrect(false);
      if (isFlagCorrect(submitFlag, flagEntity)) {
        submitFlag.setIsCorrect(true);
      }
      return submitFlag;
    }

    submitFlag.setIsCorrect(false);
    if (isFlagCorrect(submitFlag, flagEntity)) {
      submitFlag.setIsCorrect(true);

      setCorrect(flagEntity);

      updateTeamScore(submitChallenge, submitTeam);

      if (ctfUtilService.isTypeDynamic(submitChallenge)) {
        ctfUtilService.setDynamicScore(submitChallenge);
      }
    }
    return submitFlag;
  }

  public CtfChallengeDto getProblemDetail(Long probId) {

    ctfUtilService.checkVirtualProblem(probId);

    Long solvedTeamCount = getSolvedTeamCount(probId);
    CtfChallengeEntity challengeEntity = getSolvableChallenge(probId);

    ctfUtilService.checkJoinable(getIdByChallenge(challengeEntity));

    CtfTeamEntity myTeam = getTeamEntity(getIdByChallenge(challengeEntity),
        authService.getMemberIdByJWT());
    Boolean isSolved = isCorrect(getCtfFlagEntity(challengeEntity, myTeam));

    return CtfChallengeDto.toDto(challengeEntity, solvedTeamCount, isSolved);
  }

  public CtfSubmitLogEntity setLog(Long probId, CtfFlagDto submitFlag) {
    MemberEntity submitter = authService.getMemberEntityWithJWT();
    CtfChallengeEntity submitChallenge = challengeRepository.findById(probId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
    CtfTeamEntity submitTeam = getTeamEntity(getIdByChallenge(submitChallenge),
        submitter.getId());
    CtfFlagEntity flagEntity = getFlagEntity(probId, submitTeam);

    return submitLogRepository.save(CtfSubmitLogEntity.builder()
        .submitTime(LocalDateTime.now())
        .flagSubmitted(submitFlag.getContent())
        .isCorrect(isFlagCorrect(submitFlag, flagEntity))
        .teamName(submitTeam.getName())
        .submitterLoginId(submitter.getLoginId())
        .submitterRealname(submitter.getRealName())
        .challengeName(submitChallenge.getName())
        .contestName(submitChallenge.getCtfContestEntity().getName())
        .contest(submitChallenge.getCtfContestEntity())
        .build());
  }

  private CtfChallengeEntity getSolvableChallenge(Long probId) {
    return challengeRepository.findByIdAndIsSolvableTrue(probId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
  }

  private Long getSolvedTeamCount(Long probId) {
    return flagRepository.countByCtfChallengeEntityIdAndIsCorrect(probId, true);
  }

  private CtfFlagEntity getCtfFlagEntity(CtfChallengeEntity solvableChallenge,
      CtfTeamEntity myTeam) {
    return flagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
        solvableChallenge.getId(), myTeam.getId()).get();
  }

  private List<CtfChallengeEntity> getSolvableChallengeList(
      CtfContestEntity contestEntity) {
    return challengeRepository.findAllByIdIsNotAndCtfContestEntityAndIsSolvable(
        VIRTUAL_PROBLEM_ID, contestEntity, true);
  }

  private CtfContestEntity getContestEntity(Long ctfId) {
    return contestRepository.findById(ctfId)
        .orElseThrow(CustomContestNotFoundException::new);
  }

  private CtfTeamEntity getTeamEntity(Long ctfId, Long memberId) {
    return ctfUtilService.getTeamHasMemberEntity(ctfId, memberId).getTeam();
  }

  private void updateTeamScore(CtfChallengeEntity submitChallenge, CtfTeamEntity submitTeam) {
    submitTeam.setScore(submitTeam.getScore() + submitChallenge.getScore());
    teamRepository.save(submitTeam);
  }

  private void setCorrect(CtfFlagEntity flagEntity) {
    flagEntity.setIsCorrect(true);
    flagRepository.save(flagEntity);
  }

  private Boolean isCorrect(CtfFlagEntity flagEntity) {
    return flagEntity.getIsCorrect();
  }

  private Boolean isSolvableChallenge(CtfChallengeEntity submitChallenge) {
    return submitChallenge.getIsSolvable();
  }

  private Long getIdByChallenge(CtfChallengeEntity submitChallenge) {
    return submitChallenge.getCtfContestEntity().getId();
  }

  private CtfFlagEntity getFlagEntity(Long probId, CtfTeamEntity submitTeam) {
    return flagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(probId,
        submitTeam.getId()).orElseThrow(CustomCtfChallengeNotFoundException::new);
  }

  private CtfChallengeEntity getChallengeEntity(Long probId) {
    return challengeRepository.findById(probId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
  }

  private boolean isFlagCorrect(CtfFlagDto submitFlag, CtfFlagEntity flagEntity) {
    return flagEntity.getContent().equals(submitFlag.getContent());
  }

}
