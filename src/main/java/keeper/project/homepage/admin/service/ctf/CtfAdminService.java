package keeper.project.homepage.admin.service.ctf;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.admin.dto.ctf.CtfChallengeAdminDto;
import keeper.project.homepage.admin.dto.ctf.CtfContestDto;
import keeper.project.homepage.admin.dto.ctf.CtfProbMakerDto;
import keeper.project.homepage.admin.dto.ctf.CtfSubmitLogDto;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfDynamicChallengeInfoEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.ctf.CtfSubmitLogEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.exception.ctf.CustomContestNotFoundException;
import keeper.project.homepage.exception.ctf.CustomCtfCategoryNotFoundException;
import keeper.project.homepage.exception.ctf.CustomCtfChallengeNotFoundException;
import keeper.project.homepage.exception.ctf.CustomCtfTypeNotFoundException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.ctf.CtfChallengeCategoryRepository;
import keeper.project.homepage.repository.ctf.CtfChallengeRepository;
import keeper.project.homepage.repository.ctf.CtfChallengeTypeRepository;
import keeper.project.homepage.repository.ctf.CtfContestRepository;
import keeper.project.homepage.repository.ctf.CtfDynamicChallengeInfoRepository;
import keeper.project.homepage.repository.ctf.CtfFlagRepository;
import keeper.project.homepage.repository.ctf.CtfSubmitLogRepository;
import keeper.project.homepage.repository.ctf.CtfTeamRepository;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.user.dto.ctf.CtfDynamicChallengeInfoDto;
import keeper.project.homepage.util.dto.FileDto;
import keeper.project.homepage.util.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@RequiredArgsConstructor
public class CtfAdminService {

  private static final String PROBLEM_MAKER_JOB = "ROLE_출제자";
  private static final Long VIRTUAL_SUBMIT_LOG_ID = 1L;

  private final AuthService authService;
  private final FileService fileService;
  private final CtfContestRepository ctfContestRepository;
  private final CtfTeamRepository ctfTeamRepository;
  private final CtfChallengeCategoryRepository ctfChallengeCategoryRepository;
  private final CtfSubmitLogRepository ctfSubmitLogRepository;
  private final CtfChallengeTypeRepository ctfChallengeTypeRepository;
  private final CtfChallengeRepository challengeRepository;
  private final CtfDynamicChallengeInfoRepository ctfDynamicChallengeInfoRepository;
  private final CtfFlagRepository ctfFlagRepository;
  private final MemberRepository memberRepository;
  private final MemberHasMemberJobRepository memberHasMemberJobRepository;
  private final MemberJobRepository memberJobRepository;

  @Transactional
  public CtfContestDto createContest(CtfContestDto contestDto) {
    contestDto.setJoinable(false);
    MemberEntity creator = authService.getMemberEntityWithJWT();
    return CtfContestDto.toDto(ctfContestRepository.save(contestDto.toEntity(creator)));
  }

  public CtfContestDto openContest(Long ctfId) {
    CtfContestEntity contestEntity = getCtfContestEntity(ctfId);
    contestEntity.setIsJoinable(true);
    return CtfContestDto.toDto(contestEntity);
  }

  public CtfContestDto closeContest(Long ctfId) {
    CtfContestEntity contestEntity = getCtfContestEntity(ctfId);
    contestEntity.setIsJoinable(false);
    return CtfContestDto.toDto(contestEntity);
  }

  public List<CtfContestDto> getContests() {
    List<CtfContestEntity> contestEntities = ctfContestRepository.findAll();
    return contestEntities.stream().map(CtfContestDto::toDto).collect(Collectors.toList());
  }

  @Transactional
  public CtfProbMakerDto designateProbMaker(CtfProbMakerDto probMakerDto) {
    MemberEntity probMaker = memberRepository.findById(probMakerDto.getMemberId())
        .orElseThrow(CustomMemberNotFoundException::new);
    MemberJobEntity probMakerJob = memberJobRepository.findByName(PROBLEM_MAKER_JOB)
        .orElseThrow(() -> new RuntimeException("'ROLE_출제자'가 존재하지 않습니다. DB를 확인해주세요."));
    memberHasMemberJobRepository.save(MemberHasMemberJobEntity.builder()
        .memberEntity(probMaker)
        .memberJobEntity(probMakerJob)
        .build());

    return CtfProbMakerDto.toDto(probMaker);
  }

  @Transactional
  public CtfChallengeAdminDto createProblem(CtfChallengeAdminDto challengeAdminDto) {

    CtfChallengeEntity challenge = createChallengeEntity(challengeAdminDto, null);
    challenge = challengeRepository.save(challenge);
    if (challengeAdminDto.getType().getId().equals(CtfChallengeTypeEntity.DYNAMIC.getId())) {
      if (challengeAdminDto.getDynamicInfo() == null) {
        // TODO: DynamicInfo Exception
        throw new RuntimeException("");
      }
      CtfDynamicChallengeInfoEntity dynamicInfoEntity = createDynamicInfoEntity(
          challengeAdminDto.getDynamicInfo(), challenge);
//      ctfDynamicChallengeInfoRepository.save(dynamicInfoEntity);
      challenge.setDynamicChallengeInfoEntity(dynamicInfoEntity);
    }
    challenge = challengeRepository.save(challenge);

    setFlagAllTeam(challengeAdminDto.getFlag(), challenge);

    return CtfChallengeAdminDto.toDto(challenge);

  }

  public FileDto fileRegistrationInProblem(Long challengeId,
      HttpServletRequest request, MultipartFile file) {
    String ipAddress = request.getHeader("X-FORWARDED-FOR") == null ?
        request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR");
    FileEntity saveFile = fileService.saveFile(file, ipAddress);

    try {
      CtfChallengeEntity challenge = challengeRepository.findById(challengeId)
          .orElseThrow(CustomCtfChallengeNotFoundException::new);
      challenge.setFileEntity(saveFile);
      challengeRepository.save(challenge);
    } catch (Exception e) {
      log.info(e.getMessage());
      if (saveFile != null) {
        fileService.deleteFileById(saveFile.getId());
      }
      throw new RuntimeException("문제 생성 실패!");
    }

    return FileDto.toDto(saveFile);
  }

  private CtfDynamicChallengeInfoEntity createDynamicInfoEntity(
      CtfDynamicChallengeInfoDto dynamicInfo, CtfChallengeEntity challenge) {
    return dynamicInfo.toEntity(challenge);
  }

  private void setFlagAllTeam(String flag, CtfChallengeEntity challenge) {
    List<CtfTeamEntity> ctfTeamEntities = ctfTeamRepository.findAll();
    for (var ctfTeam : ctfTeamEntities) {
      CtfFlagEntity flagEntity = CtfFlagEntity.builder()
          .content(flag)
          .ctfTeamEntity(ctfTeam)
          .ctfChallengeEntity(challenge)
          .isCorrect(false)
          .build();
      ctfFlagRepository.save(flagEntity);
      challenge.getCtfFlagEntity().add(flagEntity);
    }
  }

  private CtfChallengeEntity createChallengeEntity(
      CtfChallengeAdminDto challengeAdminDto, FileEntity fileEntity) {
    CtfContestEntity contest = getCtfContestEntity(challengeAdminDto.getContestId());
    CtfChallengeCategoryEntity category = getCategoryEntity(challengeAdminDto);
    CtfChallengeTypeEntity type = getTypeEntity(challengeAdminDto);

    MemberEntity creator = authService.getMemberEntityWithJWT();

    CtfChallengeEntity challenge = challengeAdminDto
        .toEntity(contest, type, category, fileEntity, creator);
    return challenge;
  }

  private CtfChallengeTypeEntity getTypeEntity(CtfChallengeAdminDto challengeAdminDto) {
    return ctfChallengeTypeRepository
        .findById(challengeAdminDto.getType().getId())
        .orElseThrow(CustomCtfTypeNotFoundException::new);
  }

  private CtfChallengeCategoryEntity getCategoryEntity(CtfChallengeAdminDto challengeAdminDto) {
    return ctfChallengeCategoryRepository
        .findById(challengeAdminDto.getCategory().getId())
        .orElseThrow(CustomCtfCategoryNotFoundException::new);
  }

  private CtfContestEntity getCtfContestEntity(Long challengeAdminDto) {
    return ctfContestRepository
        .findById(challengeAdminDto)
        .orElseThrow(CustomContestNotFoundException::new);
  }

  public CtfChallengeAdminDto openProblem(Long problemId) {
    CtfChallengeEntity challenge = challengeRepository.findById(problemId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
    challenge.setIsSolvable(true);
    challengeRepository.save(challenge);

    return CtfChallengeAdminDto.toDto(challenge);
  }

  public CtfChallengeAdminDto closeProblem(Long problemId) {
    CtfChallengeEntity challenge = challengeRepository.findById(problemId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
    challenge.setIsSolvable(false);
    challengeRepository.save(challenge);

    return CtfChallengeAdminDto.toDto(challenge);
  }

  @Transactional
  public CtfChallengeAdminDto deleteProblem(Long problemId) throws AccessDeniedException {
    MemberEntity requestMember = authService.getMemberEntityWithJWT();
    CtfChallengeEntity challenge = challengeRepository.findById(problemId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
    if (!requestMember.getJobs().contains("ROLE_회장")) {
      if (!challenge.getCreator().getId().equals(requestMember.getId())) {
        throw new AccessDeniedException("문제 생성자나 회장만 삭제할 수 있습니다.");
      }
    }

    if (challenge.getFileEntity() != null) {
      fileService.deleteFile(challenge.getFileEntity());
    }
    challengeRepository.delete(challenge);

    return CtfChallengeAdminDto.toDto(challenge);
  }

  public List<CtfChallengeAdminDto> getProblemList(Long ctfId) {
    CtfContestEntity contest = ctfContestRepository.findById(ctfId)
        .orElseThrow(CustomContestNotFoundException::new);
    return challengeRepository.findAllByCtfContestEntity(contest).stream()
        .map(CtfChallengeAdminDto::toDto)
        .toList();
  }

  public Page<CtfSubmitLogDto> getSubmitLogList(Pageable pageable) {
    return ctfSubmitLogRepository.findAllByIdIsNot(VIRTUAL_SUBMIT_LOG_ID, pageable)
        .map(CtfSubmitLogDto::toDto);
  }
}
