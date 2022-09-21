package keeper.project.homepage.ctf.service;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_PROBLEM_ID;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfSubmitLogEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.ctf.exception.CustomContestNotFoundException;
import keeper.project.homepage.ctf.exception.CustomCtfChallengeNotFoundException;
import keeper.project.homepage.ctf.repository.CtfChallengeRepository;
import keeper.project.homepage.ctf.repository.CtfContestRepository;
import keeper.project.homepage.ctf.repository.CtfFlagRepository;
import keeper.project.homepage.ctf.repository.CtfSubmitLogRepository;
import keeper.project.homepage.ctf.repository.CtfTeamHasMemberRepository;
import keeper.project.homepage.ctf.repository.CtfTeamRepository;
import keeper.project.homepage.ctf.dto.CtfChallengeDto;
import keeper.project.homepage.ctf.dto.CtfCommonChallengeDto;
import keeper.project.homepage.ctf.dto.CtfFlagDto;
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

    CtfTeamEntity myTeam = ctfUtilService.getTeamHasMemberEntity(ctfId,
        authService.getMemberIdByJWT()).getTeam();
    CtfContestEntity contestEntity = contestRepository.findById(ctfId)
        .orElseThrow(CustomContestNotFoundException::new);
    List<CtfChallengeEntity> challengeEntities = challengeRepository.findAllByIdIsNotAndCtfContestEntityAndIsSolvable(
        VIRTUAL_PROBLEM_ID, contestEntity, true);
    return challengeEntities.stream().map(challenge -> {
      Boolean isSolved = flagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
          challenge.getId(), myTeam.getId()).get().getIsCorrect();
      return CtfCommonChallengeDto.toDto(challenge, isSolved);
    }).toList();
  }

  @Transactional
  public CtfFlagDto checkFlag(Long probId, CtfFlagDto submitFlag) {

    ctfUtilService.checkVirtualProblem(probId);

    Long submitterId = authService.getMemberIdByJWT();
    CtfChallengeEntity submitChallenge = challengeRepository.findById(probId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
    CtfTeamEntity submitTeam = ctfUtilService.getTeamHasMemberEntity(
        submitChallenge.getCtfContestEntity().getId(), submitterId).getTeam();
    CtfFlagEntity flagEntity = flagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(probId,
        submitTeam.getId()).orElseThrow(CustomCtfChallengeNotFoundException::new);

    // 참가 불가능 CTF면 Flag check 안함.
    ctfUtilService.checkJoinable(submitChallenge.getCtfContestEntity().getId());

    // 풀 수 없는 문제면 조치 안함.
    if (!submitChallenge.getIsSolvable()) {
      throw new AccessDeniedException("풀 수 없는 문제입니다.");
    }

    // 이미 맞췄으면 조치 안함.
    if (flagEntity.getIsCorrect()) {
      submitFlag.setIsCorrect(false);
      if (isFlagCorrect(submitFlag, flagEntity)) {
        submitFlag.setIsCorrect(true);
      }
      return submitFlag;
    }

    submitFlag.setIsCorrect(false);
    if (isFlagCorrect(submitFlag, flagEntity)) {
      submitFlag.setIsCorrect(true);

      flagEntity.setIsCorrect(true);
      flagRepository.save(flagEntity);

      submitTeam.setScore(submitTeam.getScore() + submitChallenge.getScore());
      teamRepository.save(submitTeam);

      if (ctfUtilService.isTypeDynamic(submitChallenge)) {
        ctfUtilService.setDynamicScore(submitChallenge);
      }
    }
    return submitFlag;
  }

  private boolean isFlagCorrect(CtfFlagDto submitFlag, CtfFlagEntity flagEntity) {
    return flagEntity.getContent().equals(submitFlag.getContent());
  }


  public CtfChallengeDto getProblemDetail(Long probId) {

    ctfUtilService.checkVirtualProblem(probId);

    Long solvedTeamCount = flagRepository.countByCtfChallengeEntityIdAndIsCorrect(probId, true);
    CtfChallengeEntity challengeEntity = challengeRepository.findByIdAndIsSolvableTrue(probId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);

    ctfUtilService.checkJoinable(challengeEntity.getCtfContestEntity().getId());

    CtfTeamEntity myTeam = ctfUtilService.getTeamHasMemberEntity(
        challengeEntity.getCtfContestEntity().getId(),
        authService.getMemberIdByJWT()).getTeam();
    Boolean isSolved = flagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
        challengeEntity.getId(), myTeam.getId()).get().getIsCorrect();

    return CtfChallengeDto.toDto(challengeEntity, solvedTeamCount, isSolved);
  }

  public CtfSubmitLogEntity setLog(Long probId, CtfFlagDto submitFlag) {
    MemberEntity submitter = authService.getMemberEntityWithJWT();
    CtfChallengeEntity submitChallenge = challengeRepository.findById(probId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
    CtfTeamEntity submitTeam = ctfUtilService.getTeamHasMemberEntity(
        submitChallenge.getCtfContestEntity().getId(), submitter.getId()).getTeam();
    CtfFlagEntity flagEntity = flagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(probId,
        submitTeam.getId()).orElseThrow(CustomCtfChallengeNotFoundException::new);

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
}
