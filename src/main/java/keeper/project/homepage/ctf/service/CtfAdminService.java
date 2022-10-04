package keeper.project.homepage.ctf.service;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_CONTEST_ID;
import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_PROBLEM_ID;
import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_TEAM_ID;

import java.nio.file.AccessDeniedException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.ctf.dto.CtfChallengeAdminDto;
import keeper.project.homepage.ctf.dto.CtfContestAdminDto;
import keeper.project.homepage.ctf.dto.CtfDynamicChallengeInfoDto;
import keeper.project.homepage.ctf.dto.CtfProbMakerDto;
import keeper.project.homepage.ctf.dto.CtfSubmitLogDto;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfDynamicChallengeInfoEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.ctf.exception.CustomContestNotFoundException;
import keeper.project.homepage.ctf.exception.CustomCtfCategoryNotFoundException;
import keeper.project.homepage.ctf.exception.CustomCtfChallengeNotFoundException;
import keeper.project.homepage.ctf.exception.CustomCtfTypeNotFoundException;
import keeper.project.homepage.ctf.repository.CtfChallengeCategoryRepository;
import keeper.project.homepage.ctf.repository.CtfChallengeRepository;
import keeper.project.homepage.ctf.repository.CtfChallengeTypeRepository;
import keeper.project.homepage.ctf.repository.CtfContestRepository;
import keeper.project.homepage.ctf.repository.CtfDynamicChallengeInfoRepository;
import keeper.project.homepage.ctf.repository.CtfFlagRepository;
import keeper.project.homepage.ctf.repository.CtfSubmitLogRepository;
import keeper.project.homepage.ctf.repository.CtfTeamRepository;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.member.repository.MemberHasMemberJobRepository;
import keeper.project.homepage.member.repository.MemberJobRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.util.dto.FileDto;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.util.service.CtfUtilService;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@RequiredArgsConstructor
public class CtfAdminService {

  private final AuthService authService;
  private final FileService fileService;
  private final CtfUtilService ctfUtilService;
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
  public CtfContestAdminDto createContest(CtfContestAdminDto contestDto) {
    contestDto.setJoinable(false);
    MemberEntity creator = authService.getMemberEntityWithJWT();
    return CtfContestAdminDto.toDto(ctfContestRepository.save(contestDto.toEntity(creator)));
  }

  public CtfContestAdminDto openContest(Long ctfId) {
    ctfUtilService.checkVirtualContest(ctfId);
    CtfContestEntity contestEntity = getCtfContestEntity(ctfId);
    contestEntity.setIsJoinable(true);
    return CtfContestAdminDto.toDto(ctfContestRepository.save(contestEntity));
  }

  public CtfContestAdminDto closeContest(Long ctfId) {
    ctfUtilService.checkVirtualContest(ctfId);
    CtfContestEntity contestEntity = getCtfContestEntity(ctfId);
    contestEntity.setIsJoinable(false);
    return CtfContestAdminDto.toDto(ctfContestRepository.save(contestEntity));
  }

  public Page<CtfContestAdminDto> getContests(Pageable pageable) {
    Page<CtfContestEntity> contestEntities = getAllContests(pageable);
    return contestEntities.map(CtfContestAdminDto::toDto);
  }

  @Transactional
  public CtfProbMakerDto designateProbMaker(CtfProbMakerDto probMakerDto) {
    MemberEntity probMaker = getProbMaker(probMakerDto);
    MemberJobEntity probMakerJob = getProbMakerJob();
    designateMemberAJob(probMaker, probMakerJob);
    return CtfProbMakerDto.toDto(probMaker);
  }

  @Transactional
  public CtfChallengeAdminDto createChallenge(CtfChallengeAdminDto challengeAdminDto) {
    CtfChallengeEntity newChallenge = createChallengeEntity(challengeAdminDto);
    if (ctfUtilService.isTypeDynamic(newChallenge)) {
      trySetDynamicInfoInChallenge(newChallenge, challengeAdminDto);
    }
    setFlagAllTeam(challengeAdminDto.getFlag(), newChallenge);
    return CtfChallengeAdminDto.toDto(newChallenge);
  }

  public FileDto saveFileAndRegisterInChallenge(Long challengeId, HttpServletRequest request,
      MultipartFile file) {
    FileEntity saveFile = saveFileAndGetEntity(request, file);
    tryFileRegisterInChallenge(challengeId, saveFile);
    return FileDto.toDto(saveFile);
  }

  public CtfChallengeAdminDto openProblem(Long problemId) {
    ctfUtilService.checkVirtualProblem(problemId);
    CtfChallengeEntity challenge = getChallengeById(problemId);
    challenge.setIsSolvable(true);
    challengeRepository.save(challenge);
    return CtfChallengeAdminDto.toDto(challenge);
  }

  public CtfChallengeAdminDto closeProblem(Long problemId) {
    ctfUtilService.checkVirtualProblem(problemId);
    CtfChallengeEntity challenge = getChallengeById(problemId);
    challenge.setIsSolvable(false);
    challengeRepository.save(challenge);
    return CtfChallengeAdminDto.toDto(challenge);
  }

  @Transactional
  public CtfChallengeAdminDto deleteProblem(Long problemId) throws AccessDeniedException {
    ctfUtilService.checkVirtualProblem(problemId);
    MemberEntity requestMember = authService.getMemberEntityWithJWT();
    CtfChallengeEntity challenge = getChallengeById(problemId);
    if (isNotClubPresident(requestMember) &&
        isNotChallengeCreator(challenge.getCreator(), requestMember)) {
      throw new AccessDeniedException("문제 생성자나 회장만 삭제할 수 있습니다.");
    }
    ctfUtilService.setChallengeScore(challenge, 0);
    if (hasFileEntity(challenge)) {
      fileService.deleteFile(challenge.getFileEntity());
    }
    challengeRepository.delete(challenge);
    return CtfChallengeAdminDto.toDto(challenge);
  }

  private boolean hasFileEntity(CtfChallengeEntity challenge) {
    return challenge.getFileEntity() != null;
  }

  private boolean isNotClubPresident(MemberEntity requestMember) {
    return !requestMember.getJobs().contains("ROLE_회장");
  }

  public Page<CtfChallengeAdminDto> getProblemList(Pageable pageable, Long ctfId) {
    ctfUtilService.checkVirtualContest(ctfId);
    CtfContestEntity contest = getContest(ctfId);
    return challengeRepository
        .findAllByIdIsNotAndCtfContestEntity(VIRTUAL_PROBLEM_ID, contest, pageable)
        .map(CtfChallengeAdminDto::toDto);
  }

  private CtfContestEntity getContest(Long ctfId) {
    return ctfContestRepository.findById(ctfId)
        .orElseThrow(CustomContestNotFoundException::new);
  }

  public Page<CtfSubmitLogDto> getSubmitLogList(Pageable pageable, Long ctfId) {
    ctfUtilService.checkVirtualContest(ctfId);
    return ctfSubmitLogRepository
        .findAllByIdIsNotAndContestId(CtfUtilService.VIRTUAL_SUBMIT_LOG_ID, pageable, ctfId)
        .map(CtfSubmitLogDto::toDto);
  }

  public void disqualifyProbMaker(CtfProbMakerDto probMakerDto) {
    MemberEntity probMaker = getProbMaker(probMakerDto);
    MemberJobEntity probMakerJob = getProbMakerJob();
    disqualifyMemberAJob(probMaker, probMakerJob);
  }

  private void disqualifyMemberAJob(MemberEntity probMaker, MemberJobEntity probMakerJob) {
    memberHasMemberJobRepository.deleteAllByMemberEntityAndMemberJobEntity(probMaker, probMakerJob);
  }

  private boolean isNotChallengeCreator(MemberEntity creator, MemberEntity requestMember) {
    return !creator.getId().equals(requestMember.getId());
  }

  private void trySetDynamicInfoInChallenge(CtfChallengeEntity challenge,
      CtfChallengeAdminDto challengeAdminDto) {
    checkDynamicInfoValid(challengeAdminDto);
    setDynamicInfoInChallenge(challenge, challengeAdminDto);
  }

  private void setDynamicInfoInChallenge(CtfChallengeEntity challenge,
      CtfChallengeAdminDto challengeAdminDto) {
    CtfDynamicChallengeInfoEntity dynamicInfoEntity = createDynamicInfoEntity(
        challengeAdminDto.getDynamicInfo(), challenge);
    challenge.setDynamicChallengeInfoEntity(dynamicInfoEntity);
    challenge.setScore(dynamicInfoEntity.getMaxScore());
    challengeRepository.save(challenge);
  }

  private void checkDynamicInfoValid(CtfChallengeAdminDto challengeAdminDto) {
    if (challengeAdminDto.getDynamicInfo() == null) {
      // TODO: DynamicInfo Exception
      throw new CustomCtfChallengeNotFoundException("Dynamic 관련 필드가 존재하지 않습니다.");
    }
    if (isMaxScoreLessThanMinScore(challengeAdminDto)) {
      // TODO: DynamicInfo Exception
      throw new CustomCtfChallengeNotFoundException(
          "DYNAMIC 문제는 max score보다 min score가 더 클 수 없습니다.");
    }
  }

  private boolean isMaxScoreLessThanMinScore(CtfChallengeAdminDto challengeAdminDto) {
    Long maxScore = challengeAdminDto.getDynamicInfo().getMaxScore();
    Long minScore = challengeAdminDto.getDynamicInfo().getMinScore();
    return maxScore < minScore;
  }

  private Long getChallengeTypeId(CtfChallengeAdminDto challengeAdminDto) {
    return challengeAdminDto.getType().getId();
  }

  private Page<CtfContestEntity> getAllContests(Pageable pageable) {
    return ctfContestRepository
        .findAllByIdIsNotOrderByIdDesc(VIRTUAL_CONTEST_ID, pageable);
  }

  private FileEntity saveFileAndGetEntity(HttpServletRequest request, MultipartFile file) {
    String ipAddress = getIpAddress(request);
    return saveFile(file, ipAddress);
  }

  private void tryFileRegisterInChallenge(Long challengeId, FileEntity saveFile) {
    try {
      fileRegisterInChallenge(challengeId, saveFile);
    } catch (Exception e) {
      log.info(e.getMessage());
      if (saveFile != null) {
        fileService.deleteFile(saveFile.getId());
      }
      throw new RuntimeException("문제 생성 실패!");
    }
  }

  private void fileRegisterInChallenge(Long challengeId, FileEntity saveFile) {
    CtfChallengeEntity challenge = getChallengeById(challengeId);
    challenge.setFileEntity(saveFile);
    challengeRepository.save(challenge);
  }

  private CtfChallengeEntity getChallengeById(Long challengeId) {
    return challengeRepository
        .findById(challengeId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
  }

  private FileEntity saveFile(MultipartFile file, String ipAddress) {
    return fileService.saveFile(file, ipAddress, null);
  }

  private String getIpAddress(HttpServletRequest request) {
    return request.getHeader("X-FORWARDED-FOR") == null ?
        request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR");
  }

  private CtfDynamicChallengeInfoEntity createDynamicInfoEntity(
      CtfDynamicChallengeInfoDto dynamicInfo, CtfChallengeEntity challenge) {
    return dynamicInfo.toEntity(challenge);
  }

  private void setFlagAllTeam(String flag, CtfChallengeEntity challenge) {
    // team이 하나도 없을 때 flag가 유실되는 것을 방지하기 위해 VIRTUAL TEAM을 이용해 flag를 저장합니다.
    List<CtfTeamEntity> allCtfTeamList = ctfTeamRepository
        .findAllByIdOrCtfContestEntityId(VIRTUAL_TEAM_ID, getCtfId(challenge));
    for (CtfTeamEntity ctfTeam : allCtfTeamList) {
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

  private Long getCtfId(CtfChallengeEntity challenge) {
    return challenge.getCtfContestEntity().getId();
  }

  private CtfChallengeEntity createChallengeEntityWithFileEntity(
      CtfChallengeAdminDto challengeAdminDto, FileEntity fileEntity) {
    CtfContestEntity contest = getCtfContestEntity(challengeAdminDto.getContestId());
    CtfChallengeCategoryEntity category = getCategoryEntity(challengeAdminDto);
    CtfChallengeTypeEntity type = getTypeEntity(challengeAdminDto);
    MemberEntity creator = authService.getMemberEntityWithJWT();
    CtfChallengeEntity challenge = challengeAdminDto
        .toEntity(contest, type, category, fileEntity, creator);
    return challengeRepository.save(challenge);
  }

  private CtfChallengeEntity createChallengeEntity(CtfChallengeAdminDto challengeAdminDto) {
    return createChallengeEntityWithFileEntity(challengeAdminDto, null);
  }

  private CtfChallengeTypeEntity getTypeEntity(CtfChallengeAdminDto challengeAdminDto) {
    return ctfChallengeTypeRepository
        .findById(getChallengeTypeId(challengeAdminDto))
        .orElseThrow(CustomCtfTypeNotFoundException::new);
  }

  private CtfChallengeCategoryEntity getCategoryEntity(CtfChallengeAdminDto challengeAdminDto) {
    return ctfChallengeCategoryRepository
        .findById(challengeAdminDto.getCategory().getId())
        .orElseThrow(CustomCtfCategoryNotFoundException::new);
  }

  private CtfContestEntity getCtfContestEntity(Long challengeAdminDto) {
    return getContest(challengeAdminDto);
  }

  private void designateMemberAJob(MemberEntity probMaker, MemberJobEntity probMakerJob) {
    memberHasMemberJobRepository.save(MemberHasMemberJobEntity.builder()
        .memberEntity(probMaker)
        .memberJobEntity(probMakerJob)
        .build());
  }

  private MemberJobEntity getProbMakerJob() {
    return memberJobRepository
        .findByName(CtfUtilService.PROBLEM_MAKER_JOB)
        .orElseThrow(() -> new RuntimeException("'ROLE_출제자'가 존재하지 않습니다. DB를 확인해주세요."));
  }

  private MemberEntity getProbMaker(CtfProbMakerDto probMakerDto) {
    return memberRepository
        .findById(probMakerDto.getMemberId())
        .orElseThrow(() -> new CustomMemberNotFoundException(probMakerDto.getMemberId()));
  }
}
