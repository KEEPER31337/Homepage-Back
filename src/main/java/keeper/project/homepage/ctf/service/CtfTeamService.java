package keeper.project.homepage.ctf.service;

import static java.time.LocalDateTime.now;
import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_CONTEST_ID;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.ctf.entity.CtfTeamHasMemberEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.ctf.exception.CustomContestNotFoundException;
import keeper.project.homepage.ctf.exception.CustomCtfTeamNotFoundException;
import keeper.project.homepage.ctf.repository.CtfChallengeRepository;
import keeper.project.homepage.ctf.repository.CtfContestRepository;
import keeper.project.homepage.ctf.repository.CtfFlagRepository;
import keeper.project.homepage.ctf.repository.CtfTeamHasMemberRepository;
import keeper.project.homepage.ctf.repository.CtfTeamRepository;
import keeper.project.homepage.ctf.dto.CtfJoinTeamRequestDto;
import keeper.project.homepage.ctf.dto.CtfTeamDetailDto;
import keeper.project.homepage.ctf.dto.CtfTeamDto;
import keeper.project.homepage.ctf.dto.CtfTeamHasMemberDto;
import keeper.project.homepage.util.service.CtfUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CtfTeamService {

  private final CtfTeamRepository teamRepository;
  private final CtfTeamHasMemberRepository teamHasMemberRepository;
  private final CtfFlagRepository flagRepository;
  private final CtfContestRepository contestRepository;
  private final CtfChallengeRepository challengeRepository;
  private final AuthService authService;
  private final CtfUtilService ctfUtilService;

  @Transactional
  public CtfTeamDetailDto createTeam(CtfTeamDetailDto ctfTeamDetailDto) {
    checkContestIdIsValid(ctfTeamDetailDto.getContestId());
    checkCreatorIsAlreadyHasTeam(ctfTeamDetailDto);
    CtfTeamEntity newTeamEntity = createNewTeamEntity(ctfTeamDetailDto);
    registerCreatorToTeam(newTeamEntity);
    ctfUtilService.setAllDynamicScore(); // 팀이 생성될 때 마다 Dynamic score 변경해 줘야 함.
    createTeamFlag(ctfTeamDetailDto, newTeamEntity); // 팀이 생성될 때 마다 모든 문제에 해당하는 flag를 매핑해 줘야 함.
    return getCtfTeamDetailDto(newTeamEntity, Collections.emptyList());
  }

  private CtfTeamEntity createNewTeamEntity(CtfTeamDetailDto ctfTeamDetailDto) {
    Long contestId = ctfTeamDetailDto.getContestId();
    MemberEntity creator = getMemberEntityByJWT();
    CtfContestEntity contest = getContestEntity(contestId);
    ctfTeamDetailDto.setRegisterTime(now());
    CtfTeamEntity newTeamEntity = saveTeam(ctfTeamDetailDto.toEntity(contest, creator));
    return newTeamEntity;
  }

  private void checkCreatorIsAlreadyHasTeam(CtfTeamDetailDto ctfTeamDetailDto) {
    Long contestId = ctfTeamDetailDto.getContestId();
    MemberEntity creator = getMemberEntityByJWT();
    if (isAlreadyHasTeam(contestId, creator)) {
      throw new RuntimeException("이미 해당 CTF에서 가입한 팀이 존재합니다.");
    }
  }

  private void checkContestIdIsValid(Long contestId) {
    ctfUtilService.checkVirtualContest(contestId);
    ctfUtilService.checkJoinable(contestId);
  }

  @Transactional
  public CtfTeamDetailDto modifyTeam(Long teamId, CtfTeamDto ctfTeamDto) {
    checkTeamAndCtfIsValid(teamId);
    checkTeamIsMine(teamId);
    CtfTeamEntity modifiedTeamEntity = modifyTeamEntity(teamId, ctfTeamDto);
    return getCtfTeamDetailDto(teamId, modifiedTeamEntity);
  }

  private CtfTeamDetailDto getCtfTeamDetailDto(CtfTeamEntity teamEntity,
      List<CtfChallengeEntity> solvedChallengeList) {
    return CtfTeamDetailDto.toDto(teamEntity, solvedChallengeList);
  }

  private CtfTeamDetailDto getCtfTeamDetailDto(Long teamId, CtfTeamEntity teamEntity) {
    List<CtfChallengeEntity> solvedChallengeList = getSolvedChallengeListByTeamId(teamId);
    return getCtfTeamDetailDto(teamEntity, solvedChallengeList);
  }

  private CtfTeamEntity modifyTeamEntity(Long teamId, CtfTeamDto ctfTeamDto) {
    CtfTeamEntity teamEntity = getTeamEntity(teamId);
    teamEntity.setName(ctfTeamDto.getName());
    teamEntity.setDescription(ctfTeamDto.getDescription());
    return teamEntity;
  }

  private void checkTeamIsMine(Long teamId) {
    CtfTeamEntity teamEntity = getTeamEntity(teamId);
    Long modifyMemberId = authService.getMemberIdByJWT();
    if (!amICreator(teamEntity, modifyMemberId)) {
      throw new RuntimeException("본인이 생성한 팀이 아니면 수정할 수 없습니다.");
    }
  }

  private void checkTeamAndCtfIsValid(Long teamId) {
    checkTeamIsValid(teamId);
    CtfTeamEntity teamEntity = getTeamEntity(teamId);
    checkContestIdIsValid(getCtfId(teamEntity));
  }

  private void checkTeamIsValid(Long teamId) {
    ctfUtilService.checkVirtualTeam(teamId);
  }

  @Transactional
  public CtfTeamHasMemberDto tryJoinTeam(CtfJoinTeamRequestDto joinTeamRequestDto) {
    checkJoinTeamRequestIsValid(joinTeamRequestDto);
    return joinTeamAndGetJoinTeamDto(joinTeamRequestDto);
  }

  private void checkJoinTeamRequestIsValid(CtfJoinTeamRequestDto joinTeamRequestDto) {
    checkTeamNameIsValid(joinTeamRequestDto);
    checkContestIdIsValid(joinTeamRequestDto.getContestId());
    checkIsAlreadyHasTeam(joinTeamRequestDto);
  }

  private CtfTeamHasMemberDto joinTeamAndGetJoinTeamDto(CtfJoinTeamRequestDto joinTeamRequestDto) {
    CtfTeamHasMemberEntity teamHasMember = joinTeam(joinTeamRequestDto);
    return CtfTeamHasMemberDto.toDto(teamHasMember);
  }

  private CtfTeamHasMemberEntity joinTeam(
      CtfJoinTeamRequestDto joinTeamRequestDto) {
    String teamName = joinTeamRequestDto.getTeamName();
    CtfContestEntity joinTeamContest = getContestEntity(joinTeamRequestDto.getContestId());
    CtfTeamEntity joinTeam = getJoinTeam(teamName, joinTeamContest);
    MemberEntity joinMember = getMemberEntityByJWT();
    CtfTeamHasMemberEntity teamHasMember = teamHasMemberRepository
        .save(CtfTeamHasMemberEntity.builder()
            .team(joinTeam)
            .member(joinMember)
            .build());
    return teamHasMember;
  }

  private void checkIsAlreadyHasTeam(CtfJoinTeamRequestDto joinTeamRequestDto) {
    String teamName = joinTeamRequestDto.getTeamName();
    CtfContestEntity joinTeamContest = getContestEntity(joinTeamRequestDto.getContestId());
    CtfTeamEntity joinTeam = getJoinTeam(teamName, joinTeamContest);
    MemberEntity joinMember = getMemberEntityByJWT();
    if (isAlreadyHasTeam(getCtfId(joinTeam), joinMember)) {
      throw new RuntimeException("이미 해당 CTF에서 가입한 팀이 존재합니다.");
    }
  }

  private Long getCtfId(CtfTeamEntity joinTeam) {
    return joinTeam.getCtfContestEntity().getId();
  }

  private void checkTeamNameIsValid(CtfJoinTeamRequestDto joinTeamRequestDto) {
    ctfUtilService.checkVirtualTeamByName(joinTeamRequestDto.getTeamName());
  }

  @Transactional
  public CtfTeamDetailDto tryLeaveTeam(Long ctfId) {
    checkContestIdIsValid(ctfId);
    CtfTeamEntity leftTeam = leaveTeam(ctfId);
    MemberEntity leaveMember = getMemberEntityByJWT();
    if (isTeamCreator(leaveMember, leftTeam)) {
      removeTeam(leftTeam);
      // 팀이 삭제될 때 마다 Dynamic score 변경
      ctfUtilService.setAllDynamicScore();
    }
    return getCtfTeamDetailDto(leftTeam);
  }

  private CtfTeamDetailDto getCtfTeamDetailDto(CtfTeamEntity leftTeam) {
    return getCtfTeamDetailDto(leftTeam, Collections.emptyList());
  }

  private CtfTeamEntity leaveTeam(Long ctfId) {
    MemberEntity leaveMember = getMemberEntityByJWT();
    CtfTeamHasMemberEntity leaveTeamHasMemberEntity = ctfUtilService
        .getTeamHasMemberEntity(ctfId, leaveMember.getId());
    CtfTeamEntity leftTeam = leaveTeamHasMemberEntity.getTeam();
    teamHasMemberRepository.delete(leaveTeamHasMemberEntity);
    return leftTeam;
  }

  public CtfTeamDetailDto getTeamDetail(Long teamId) {
    checkTeamIsValid(teamId);
    CtfTeamEntity teamEntity = getTeam(teamId);
    return getCtfTeamDetailDto(teamId, teamEntity);
  }

  public Page<CtfTeamDto> getTeamList(Pageable pageable, Long ctfId) {
    ctfUtilService.checkVirtualContest(ctfId);
    Page<CtfTeamEntity> teamList = teamRepository
        .findAllByIdIsNotAndCtfContestEntity_Id(VIRTUAL_CONTEST_ID, ctfId, pageable);
    return teamList.map(CtfTeamDto::toDto);
  }

  public CtfTeamDetailDto getMyTeam(Long ctfId) {
    CtfTeamEntity myTeam = ctfUtilService
        .getTeamHasMemberEntity(ctfId, authService.getMemberIdByJWT())
        .getTeam();
    return getCtfTeamDetailDto(myTeam.getId(), myTeam);
  }

  private CtfTeamEntity getTeam(Long teamId) {
    return teamRepository.findById(teamId)
        .orElseThrow(CustomCtfTeamNotFoundException::new);
  }

  private CtfTeamEntity getJoinTeam(String teamName, CtfContestEntity joinTeamContest) {
    return teamRepository.findByNameAndCtfContestEntity(teamName, joinTeamContest)
        .orElseThrow(CustomCtfTeamNotFoundException::new);
  }

  private boolean amICreator(CtfTeamEntity teamEntity, Long modifyMemberId) {
    return modifyMemberId.equals(teamEntity.getCreator().getId());
  }

  private CtfTeamEntity getTeamEntity(Long teamId) {
    return teamRepository.findById(teamId)
        .orElseThrow(CustomContestNotFoundException::new);
  }

  private void createTeamFlag(CtfTeamDetailDto ctfTeamDetailDto, CtfTeamEntity newTeamEntity) {
    Long contestId = ctfTeamDetailDto.getContestId();
    CtfContestEntity contest = getContestEntity(contestId);
    List<CtfChallengeEntity> challengeEntityList = getAllChallengeEntity(contest);
    challengeEntityList.forEach(challenge -> {
      CtfFlagEntity flagEntity = CtfFlagEntity.builder()
          .content(challenge.getCtfFlagEntity().get(0).getContent())
          .ctfTeamEntity(newTeamEntity)
          .ctfChallengeEntity(challenge)
          .isCorrect(false)
          .lastTryTime(now())
          .build();
      flagRepository.save(flagEntity);
    });
  }

  private List<CtfChallengeEntity> getAllChallengeEntity(CtfContestEntity contest) {
    return challengeRepository.findAllByIdIsNotAndCtfContestEntity(
        VIRTUAL_CONTEST_ID, contest);
  }

  private void registerCreatorToTeam(CtfTeamEntity team) {
    MemberEntity creator = getMemberEntityByJWT();
    CtfTeamHasMemberEntity teamHasMemberEntity = new CtfTeamHasMemberEntity(team, creator);
    teamHasMemberRepository.save(teamHasMemberEntity);
  }

  private CtfTeamEntity saveTeam(CtfTeamEntity ctfTeamEntity) {
    return teamRepository.save(ctfTeamEntity);
  }

  private CtfContestEntity getContestEntity(Long ctfTeamDetailDto) {
    return contestRepository.findById(ctfTeamDetailDto)
        .orElseThrow(CustomContestNotFoundException::new);
  }

  private MemberEntity getMemberEntityByJWT() {
    return authService.getMemberEntityWithJWT();
  }

  private boolean isAlreadyHasTeam(Long ctfId, MemberEntity creator) {
    return teamHasMemberRepository.findAllByMember(creator).stream().anyMatch(teamHasMemberEntity ->
        ctfId.equals(getCtfId(teamHasMemberEntity.getTeam()))
    );
  }

  private List<CtfChallengeEntity> getSolvedChallengeListByTeamId(Long teamId) {
    return flagRepository.findAllByCtfTeamEntityIdAndIsCorrectTrue(
        teamId).stream().map(CtfFlagEntity::getCtfChallengeEntity).toList();
  }

  private void removeTeam(CtfTeamEntity leftTeam) {
    teamHasMemberRepository.deleteAllByTeamId(leftTeam.getId());
    flagRepository.deleteAllByCtfTeamEntityId(leftTeam.getId());
    teamRepository.delete(leftTeam);
  }

  private boolean isTeamCreator(MemberEntity leaveMember, CtfTeamEntity leftTeam) {
    return leftTeam.getCreator().getId().equals(leaveMember.getId());
  }
}
