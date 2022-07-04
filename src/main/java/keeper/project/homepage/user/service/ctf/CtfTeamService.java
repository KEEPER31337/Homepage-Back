package keeper.project.homepage.user.service.ctf;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_CONTEST_ID;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.ctf.CtfTeamHasMemberEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.ctf.CustomContestNotFoundException;
import keeper.project.homepage.exception.ctf.CustomCtfTeamNotFoundException;
import keeper.project.homepage.repository.ctf.CtfChallengeRepository;
import keeper.project.homepage.repository.ctf.CtfContestRepository;
import keeper.project.homepage.repository.ctf.CtfFlagRepository;
import keeper.project.homepage.repository.ctf.CtfTeamHasMemberRepository;
import keeper.project.homepage.repository.ctf.CtfTeamRepository;
import keeper.project.homepage.user.dto.ctf.CtfJoinTeamRequestDto;
import keeper.project.homepage.user.dto.ctf.CtfTeamDetailDto;
import keeper.project.homepage.user.dto.ctf.CtfTeamDto;
import keeper.project.homepage.user.dto.ctf.CtfTeamHasMemberDto;
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

    Long contestId = ctfTeamDetailDto.getContestId();

    ctfUtilService.checkVirtualContest(contestId);
    ctfUtilService.checkJoinable(contestId);

    ctfTeamDetailDto.setRegisterTime(LocalDateTime.now());
    MemberEntity creator = getMemberEntityByJWT();
    CtfContestEntity contest = getContestEntity(contestId);

    if (isAlreadyHasTeam(contestId, creator)) {
      throw new RuntimeException("이미 해당 CTF에서 가입한 팀이 존재합니다.");
    }

    CtfTeamEntity newTeamEntity = saveTeam(ctfTeamDetailDto.toEntity(contest, creator));

    registerMemberToTeam(creator, newTeamEntity);

    // 팀이 생성될 때 마다 Dynamic score 변경
    ctfUtilService.setAllDynamicScore();

    // 팀이 생성될 때 마다 모든 문제에 해당하는 flag 생성
    createTeamFlag(contest, newTeamEntity);

    return CtfTeamDetailDto.toDto(newTeamEntity, new ArrayList<>());
  }

  public CtfTeamDetailDto modifyTeam(Long teamId, CtfTeamDto ctfTeamDto) {

    ctfUtilService.checkVirtualTeam(teamId);

    CtfTeamEntity teamEntity = getTeamEntity(teamId);

    ctfUtilService.checkJoinable(teamEntity.getCtfContestEntity().getId());

    Long modifyMemberId = authService.getMemberIdByJWT();
    if (!amICreator(teamEntity, modifyMemberId)) {
      throw new RuntimeException("본인이 생성한 팀이 아니면 수정할 수 없습니다.");
    }

    teamEntity.setName(ctfTeamDto.getName());
    teamEntity.setDescription(ctfTeamDto.getDescription());

    List<CtfChallengeEntity> solvedChallengeList = getSolvedChallengeListByTeamId(teamId);

    return CtfTeamDetailDto.toDto(saveTeam(teamEntity), solvedChallengeList);
  }

  public CtfTeamHasMemberDto joinTeam(CtfJoinTeamRequestDto joinTeamRequestDto) {

    String teamName = joinTeamRequestDto.getTeamName();
    CtfContestEntity joinTeamContest = getContestEntity(joinTeamRequestDto.getContestId());

    ctfUtilService.checkVirtualTeamByName(teamName);
    ctfUtilService.checkVirtualContest(joinTeamRequestDto.getContestId());
    ctfUtilService.checkJoinable(joinTeamRequestDto.getContestId());

    CtfTeamEntity joinTeam = getJoinTeam(teamName, joinTeamContest);
    MemberEntity joinMember = getMemberEntityByJWT();

    if (isAlreadyHasTeam(joinTeam.getCtfContestEntity().getId(), joinMember)) {
      throw new RuntimeException("이미 해당 CTF에서 가입한 팀이 존재합니다.");
    }

    return CtfTeamHasMemberDto.toDto(teamHasMemberRepository
        .save(CtfTeamHasMemberEntity.builder()
            .team(joinTeam)
            .member(joinMember)
            .build()));
  }

  @Transactional
  public CtfTeamDetailDto leaveTeam(Long ctfId) {

    MemberEntity leaveMember = getMemberEntityByJWT();
    CtfTeamHasMemberEntity leaveTeamHasMemberEntity = ctfUtilService
        .getTeamHasMemberEntity(ctfId, leaveMember.getId());
    CtfTeamEntity leftTeam = leaveTeamHasMemberEntity.getTeam();

    ctfUtilService.checkJoinable(leftTeam.getCtfContestEntity().getId());

    teamHasMemberRepository.delete(leaveTeamHasMemberEntity);
    if (isTeamCreator(leaveMember, leftTeam)) {
      removeTeam(leftTeam);

      // 팀이 삭제될 때 마다 Dynamic score 변경
      ctfUtilService.setAllDynamicScore();
    }

    return CtfTeamDetailDto.toDto(leftTeam, new ArrayList<>());
  }

  public CtfTeamDetailDto getTeamDetail(Long teamId) {
    ctfUtilService.checkVirtualTeam(teamId);

    CtfTeamEntity teamEntity = getTeam(teamId);

    List<CtfChallengeEntity> solvedChallengeList = getSolvedChallengeListByTeamId(teamId);

    return CtfTeamDetailDto.toDto(teamEntity, solvedChallengeList);
  }

  public Page<CtfTeamDto> getTeamList(Pageable pageable, Long ctfId) {
    ctfUtilService.checkVirtualContest(ctfId);

    Page<CtfTeamEntity> teamList = teamRepository.findAllByIdIsNotAndCtfContestEntity_Id(
        VIRTUAL_CONTEST_ID, ctfId, pageable);

    return teamList.map(CtfTeamDto::toDto);
  }

  public CtfTeamDetailDto getMyTeam(Long ctfId) {
    CtfTeamEntity myTeam = ctfUtilService.getTeamHasMemberEntity(ctfId,
        authService.getMemberIdByJWT()).getTeam();

    List<CtfChallengeEntity> solvedChallengeList = getSolvedChallengeListByTeamId(myTeam.getId());

    return CtfTeamDetailDto.toDto(myTeam, solvedChallengeList);
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

  private void createTeamFlag(CtfContestEntity contest, CtfTeamEntity newTeamEntity) {
    List<CtfChallengeEntity> challengeEntityList = getAllChallengeEntity(contest);
    challengeEntityList.forEach(challenge -> {
      CtfFlagEntity flagEntity = CtfFlagEntity.builder()
          .content(challenge.getCtfFlagEntity().get(0).getContent())
          .ctfTeamEntity(newTeamEntity)
          .ctfChallengeEntity(challenge)
          .isCorrect(false)
          .build();
      flagRepository.save(flagEntity);
    });
  }

  private List<CtfChallengeEntity> getAllChallengeEntity(CtfContestEntity contest) {
    return challengeRepository.findAllByIdIsNotAndCtfContestEntity(
        VIRTUAL_CONTEST_ID, contest);
  }

  private void registerMemberToTeam(MemberEntity member, CtfTeamEntity team) {
    CtfTeamHasMemberEntity teamHasMemberEntity = new CtfTeamHasMemberEntity(team, member);
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
        ctfId.equals(teamHasMemberEntity.getTeam().getCtfContestEntity().getId())
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
