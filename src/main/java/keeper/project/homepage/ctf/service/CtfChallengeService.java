package keeper.project.homepage.ctf.service;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_PROBLEM_ID;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.ctf.dto.CtfChallengeDto;
import keeper.project.homepage.ctf.dto.CtfCommonChallengeDto;
import keeper.project.homepage.ctf.dto.CtfFlagDto;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfSubmitLogEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.ctf.exception.CustomContestNotFoundException;
import keeper.project.homepage.ctf.exception.CustomCtfChallengeNotFoundException;
import keeper.project.homepage.ctf.exception.CustomSubmitCountNotEnoughException;
import keeper.project.homepage.ctf.repository.CtfChallengeRepository;
import keeper.project.homepage.ctf.repository.CtfContestRepository;
import keeper.project.homepage.ctf.repository.CtfFlagRepository;
import keeper.project.homepage.ctf.repository.CtfSubmitLogRepository;
import keeper.project.homepage.ctf.repository.CtfTeamHasMemberRepository;
import keeper.project.homepage.ctf.repository.CtfTeamRepository;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.util.service.CtfUtilService;
import keeper.project.homepage.util.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    checkCtfIdIsValid(ctfId);
    return getChallengeListSetMyTeamSolved(ctfId);
  }

  private void checkCtfIdIsValid(Long ctfId) {
    ctfUtilService.checkVirtualContest(ctfId);
    ctfUtilService.checkJoinable(ctfId);
  }

  private List<CtfCommonChallengeDto> getChallengeListSetMyTeamSolved(Long ctfId) {
    List<CtfChallengeEntity> solvableChallengeList = getSolvableChallengeList(ctfId);
    CtfTeamEntity myTeam = getMyTeamByCtfId(ctfId);
    return solvableChallengeList.stream()
        .map(solvableChallenge -> {
          CtfFlagEntity ctfFlagEntity = getCtfFlagEntity(solvableChallenge, myTeam);
          Boolean isMyTeamSolved = isAlreadySolved(ctfFlagEntity);
          Long remainedSubmitCount = ctfFlagEntity.getRemainedSubmitCount();
          return CtfCommonChallengeDto.toDto(solvableChallenge, isMyTeamSolved,
              remainedSubmitCount);
        }).toList();
  }

  private CtfTeamEntity getMyTeamByCtfId(Long ctfId) {
    Long requestMemberId = authService.getMemberIdByJWT();
    CtfTeamEntity myTeam = getTeamEntity(ctfId, requestMemberId);
    return myTeam;
  }

  @Transactional
  public CtfFlagDto checkFlag(Long probId, CtfFlagDto submitFlag) {
    checkSubmitChallengeIsValidById(probId);
    return SetIsCorrectAndGetCtfFlagDto(probId, submitFlag);
  }

  private CtfFlagDto SetIsCorrectAndGetCtfFlagDto(Long probId, CtfFlagDto submitFlag) {
    CtfChallengeEntity submitChallenge = getChallengeEntity(probId);
    Long submitterId = authService.getMemberIdByJWT();
    CtfTeamEntity submitTeam = getTeamEntity(getCtfIdByChallenge(submitChallenge), submitterId);
    CtfFlagEntity flagEntity = getFlagEntity(probId, submitTeam);
    if (flagEntity.getRemainedSubmitCount() <= 0) {
      throw new CustomSubmitCountNotEnoughException();
    }
    flagEntity.decreaseSubmitCount();
    // 이미 맞췄으면 제출한 flag 정답 유무만 체크하고 DB 갱신 안함.
    if (isAlreadySolved(flagEntity)) {
      setSubmitFlagIsCorrect(submitFlag, flagEntity);
      return CtfFlagDto.toDto(flagEntity);
    }
    if (isFlagCorrect(submitFlag, flagEntity)) {
      LocalDateTime solvedTime = LocalDateTime.now();
      setCorrect(flagEntity, submitTeam, solvedTime);
      updateTeamScore(submitChallenge, submitTeam);
      if (ctfUtilService.isTypeDynamic(submitChallenge)) {
        ctfUtilService.setDynamicScore(submitChallenge);
      }
    }
    return CtfFlagDto.toDto(flagEntity);
  }

  private void setSubmitFlagIsCorrect(CtfFlagDto submitFlag, CtfFlagEntity flagEntity) {
    submitFlag.setIsCorrect(false);
    if (isFlagCorrect(submitFlag, flagEntity)) {
      submitFlag.setIsCorrect(true);
    }
  }

  private void checkSubmitChallengeIsValidById(Long probId) {
    checkSubmitChallengeIdIsValid(probId);
    checkSubmitChallengeIsValid(probId);
  }

  private void checkSubmitChallengeIsValid(Long probId) {
    CtfChallengeEntity submitChallenge = getChallengeEntity(probId);
    ctfUtilService.checkJoinable(getCtfIdByChallenge(submitChallenge));
    if (!isSolvableChallenge(submitChallenge)) {
      throw new AccessDeniedException("풀 수 없는 문제입니다.");
    }
  }

  private void checkSubmitChallengeIdIsValid(Long probId) {
    ctfUtilService.checkVirtualProblem(probId);
  }

  public CtfChallengeDto getProblemDetail(Long probId) {
    checkSubmitChallengeIsValidById(probId);
    return getChallengeDto(probId);
  }

  private CtfChallengeDto getChallengeDto(Long probId) {
    CtfChallengeEntity challengeEntity = getSolvableChallenge(probId);
    Long solvedTeamCount = getSolvedTeamCount(probId);
    CtfTeamEntity myTeam = getTeamEntity(getCtfIdByChallenge(challengeEntity),
        authService.getMemberIdByJWT());
    CtfFlagEntity ctfFlagEntity = getCtfFlagEntity(challengeEntity, myTeam);
    Boolean isAlreadySolved = isAlreadySolved(ctfFlagEntity);
    Long remainedSubmitCount = ctfFlagEntity.getRemainedSubmitCount();
    return CtfChallengeDto.toDto(challengeEntity, solvedTeamCount, isAlreadySolved,
        remainedSubmitCount);
  }

  @Transactional
  public CtfSubmitLogEntity setLog(Long probId, CtfFlagDto submitFlag) {
    MemberEntity submitter = authService.getMemberEntityWithJWT();
    CtfChallengeEntity submitChallenge = getChallenge(probId);
    CtfTeamEntity submitTeam = getTeamEntity(getCtfIdByChallenge(submitChallenge),
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

  private CtfChallengeEntity getChallenge(Long probId) {
    return challengeRepository.findById(probId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
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

  private List<CtfChallengeEntity> getSolvableChallengeList(Long ctfId) {
    CtfContestEntity contestEntity = getContestEntity(ctfId);
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

  private void setCorrect(CtfFlagEntity flagEntity, CtfTeamEntity submitTeam,
      LocalDateTime solvedTime) {
    flagEntity.setIsCorrect(true);
    flagEntity.setSolvedTime(solvedTime);
    submitTeam.changeLastSolveTime(solvedTime);
  }

  private Boolean isAlreadySolved(CtfFlagEntity flagEntity) {
    return flagEntity.getIsCorrect();
  }

  private Boolean isSolvableChallenge(CtfChallengeEntity submitChallenge) {
    return submitChallenge.getIsSolvable();
  }

  private Long getCtfIdByChallenge(CtfChallengeEntity submitChallenge) {
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
