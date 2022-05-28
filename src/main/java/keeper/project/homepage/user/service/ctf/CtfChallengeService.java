package keeper.project.homepage.user.service.ctf;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_PROBLEM_ID;

import java.util.List;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.exception.ctf.CustomContestNotFoundException;
import keeper.project.homepage.exception.ctf.CustomCtfChallengeNotFoundException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.ctf.CtfChallengeRepository;
import keeper.project.homepage.repository.ctf.CtfContestRepository;
import keeper.project.homepage.repository.ctf.CtfFlagRepository;
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
  private final CtfUtilService ctfUtilService;
  private final AuthService authService;

  public List<CtfCommonChallengeDto> getProblemList(Long ctfId) {

    ctfUtilService.checkVirtualContest(ctfId);

    CtfContestEntity contestEntity = contestRepository.findById(ctfId)
        .orElseThrow(CustomContestNotFoundException::new);
    List<CtfChallengeEntity> challengeEntities = challengeRepository.findAllByIdIsNotAndCtfContestEntityAndIsSolvable(
        VIRTUAL_PROBLEM_ID, contestEntity, true);
    return challengeEntities.stream().map(CtfCommonChallengeDto::toDto).toList();
  }

  @Transactional
  public CtfFlagDto checkFlag(Long probId, CtfFlagDto submitFlag) {

    ctfUtilService.checkVirtualProblem(probId);

    Long submitterId = authService.getMemberIdByJWT();
    CtfChallengeEntity submitChallenge = challengeRepository.findById(probId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
    CtfTeamEntity submitTeam = teamHasMemberRepository.findByMember_Id(submitterId)
        .orElseThrow(CustomMemberNotFoundException::new).getTeam();
    CtfFlagEntity flagEntity = flagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(probId,
        submitTeam.getId()).orElseThrow(CustomCtfChallengeNotFoundException::new);

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

    return CtfChallengeDto.toDto(challengeRepository.findById(probId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new), solvedTeamCount);
  }
}