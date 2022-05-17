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
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
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
import keeper.project.homepage.repository.ctf.CtfFlagRepository;
import keeper.project.homepage.repository.ctf.CtfSubmitLogRepository;
import keeper.project.homepage.repository.ctf.CtfTeamRepository;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.util.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

  private final AuthService authService;
  private final FileService fileService;
  private final CtfContestRepository ctfContestRepository;
  private final CtfTeamRepository ctfTeamRepository;
  private final CtfChallengeCategoryRepository ctfChallengeCategoryRepository;
  private final CtfSubmitLogRepository ctfSubmitLogRepository;
  private final CtfChallengeTypeRepository ctfChallengeTypeRepository;
  private final CtfChallengeRepository challengeRepository;
  private final CtfFlagRepository ctfFlagRepository;
  private final MemberRepository memberRepository;
  private final MemberHasMemberJobRepository memberHasMemberJobRepository;
  private final MemberJobRepository memberJobRepository;

  @Transactional
  public CtfContestDto createContest(CtfContestDto contestDto) {
    contestDto.setJoinable(false);
    MemberEntity creator = authService.getMemberEntityWithJWT();
    return ctfContestRepository.save(contestDto.toEntity(creator)).toDto();
  }

  public CtfContestDto openContest(Long ctfId) {
    CtfContestEntity contestEntity = getCtfContestEntity(ctfId);
    contestEntity.setIsJoinable(true);
    return contestEntity.toDto();
  }

  public CtfContestDto closeContest(Long ctfId) {
    CtfContestEntity contestEntity = getCtfContestEntity(ctfId);
    contestEntity.setIsJoinable(false);
    return contestEntity.toDto();
  }

  public List<CtfContestDto> getContests() {
    List<CtfContestEntity> contestEntities = ctfContestRepository.findAll();
    return contestEntities.stream().map(CtfContestEntity::toDto).collect(Collectors.toList());
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
  public CtfChallengeAdminDto createProblem(HttpServletRequest request,
      CtfChallengeAdminDto challengeAdminDto, @Nullable MultipartFile file) {
    String ipAddress = request.getHeader("X-FORWARDED-FOR");
    FileEntity saveFile = fileService.saveFile(file, ipAddress);

    CtfChallengeAdminDto returnDto = null;
    try {
      CtfChallengeEntity challenge = getChallengeEntity(challengeAdminDto, saveFile);
      challengeRepository.save(challenge);

      setFlagAllTeam(challengeAdminDto.getFlag(), challenge);

      returnDto = CtfChallengeAdminDto.toDto(challenge);

    } catch (Exception e) {
      log.info(e.getMessage());
      if (saveFile != null) {
        fileService.deleteFileById(saveFile.getId());
      }
      throw new RuntimeException("문제 생성 실패!");
    }
    return returnDto;
  }

  private void setFlagAllTeam(String flag, CtfChallengeEntity challenge) {
    List<CtfTeamEntity> ctfTeamEntities = ctfTeamRepository.findAll();
    for (var ctfTeam : ctfTeamEntities) {
      ctfFlagRepository.save(
          CtfFlagEntity.builder()
              .content(flag)
              .ctfTeamEntity(ctfTeam)
              .ctfChallengeEntity(challenge)
              .isCorrect(false)
              .build());
    }
  }

  private CtfChallengeEntity getChallengeEntity(
      CtfChallengeAdminDto challengeAdminDto, FileEntity fileEntity) {
    CtfContestEntity contest = getCtfContestEntity(challengeAdminDto.getContest().getCtfId());
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
    Long requestMemberId = authService.getMemberIdByJWT();
    CtfChallengeEntity challenge = challengeRepository.findById(problemId)
        .orElseThrow(CustomCtfChallengeNotFoundException::new);
    if (!challenge.getCreator().getId().equals(requestMemberId)) {
      throw new AccessDeniedException("문제 생성자나 회장만 삭제할 수 있습니다.");
    }
    challengeRepository.delete(challenge);

    fileService.deleteFileById(challenge.getFileEntity().getId());

    return CtfChallengeAdminDto.toDto(challenge);
  }
}
