package keeper.project.homepage.user.service.ctf;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_CONTEST_ID;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    ctfUtilService.checkVirtualContest(ctfTeamDetailDto.getContestId());
    ctfUtilService.checkJoinable(ctfTeamDetailDto.getContestId());

    ctfTeamDetailDto.setRegisterTime(LocalDateTime.now());
    MemberEntity creator = authService.getMemberEntityWithJWT();
    CtfContestEntity contest = contestRepository.findById(ctfTeamDetailDto.getContestId())
        .orElseThrow(CustomContestNotFoundException::new);

    if (isAlreadyHasTeam(ctfTeamDetailDto.getContestId(), creator)) {
      throw new RuntimeException("이미 해당 CTF에서 가입한 팀이 존재합니다.");
    }

    CtfTeamEntity newTeamEntity = teamRepository.save(ctfTeamDetailDto.toEntity(contest, creator));

    CtfTeamHasMemberEntity teamHasMemberEntity = new CtfTeamHasMemberEntity(newTeamEntity, creator);
    teamHasMemberRepository.save(teamHasMemberEntity);

    ctfUtilService.setAllDynamicScore();

    List<CtfChallengeEntity> challengeEntities = challengeRepository.findAllByIdIsNotAndCtfContestEntity(
        VIRTUAL_CONTEST_ID, contest);
    challengeEntities.forEach(challenge -> {
      CtfFlagEntity flagEntity = CtfFlagEntity.builder()
          .content(challenge.getCtfFlagEntity().get(0).getContent())
          .ctfTeamEntity(newTeamEntity)
          .ctfChallengeEntity(challenge)
          .isCorrect(false)
          .build();
      flagRepository.save(flagEntity);
    });
    return CtfTeamDetailDto.toDto(newTeamEntity, new ArrayList<>());
  }

  private boolean isAlreadyHasTeam(Long ctfId, MemberEntity creator) {
    return teamHasMemberRepository.findAllByMember(creator).stream().anyMatch(teamHasMemberEntity ->
        ctfId.equals(teamHasMemberEntity.getTeam().getCtfContestEntity().getId())
    );
  }

  public CtfTeamDetailDto modifyTeam(Long teamId, CtfTeamDto ctfTeamDto) {

    ctfUtilService.checkVirtualTeam(teamId);

    CtfTeamEntity teamEntity = teamRepository.findById(teamId)
        .orElseThrow(CustomContestNotFoundException::new);

    ctfUtilService.checkJoinable(teamEntity.getCtfContestEntity().getId());

    Long modifyMemberId = authService.getMemberIdByJWT();
    if (!modifyMemberId.equals(teamEntity.getCreator().getId())) {
      throw new RuntimeException("본인이 생성한 팀이 아니면 수정할 수 없습니다.");
    }

    teamEntity.setName(ctfTeamDto.getName());
    teamEntity.setDescription(ctfTeamDto.getDescription());

    List<CtfChallengeEntity> solvedChallengeList = getSolvedChallengeListByTeamId(teamId);

    return CtfTeamDetailDto.toDto(teamRepository.save(teamEntity), solvedChallengeList);
  }

  private List<CtfChallengeEntity> getSolvedChallengeListByTeamId(Long teamId) {
    return flagRepository.findAllByCtfTeamEntityIdAndIsCorrectTrue(
        teamId).stream().map(CtfFlagEntity::getCtfChallengeEntity).toList();
  }

  public CtfTeamHasMemberDto joinTeam(CtfJoinTeamRequestDto joinTeamRequestDto) {

    String teamName = joinTeamRequestDto.getTeamName();
    CtfContestEntity joinTeamContest = contestRepository.findById(joinTeamRequestDto.getContestId())
        .orElseThrow(CustomContestNotFoundException::new);

    ctfUtilService.checkVirtualTeamByName(teamName);
    ctfUtilService.checkVirtualContest(joinTeamRequestDto.getContestId());
    ctfUtilService.checkJoinable(joinTeamRequestDto.getContestId());

    CtfTeamEntity joinTeam = teamRepository.findByNameAndCtfContestEntity(teamName, joinTeamContest)
        .orElseThrow(CustomCtfTeamNotFoundException::new);
    MemberEntity joinMember = authService.getMemberEntityWithJWT();

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

    MemberEntity leaveMember = authService.getMemberEntityWithJWT();
    CtfTeamHasMemberEntity leaveTeamHasMemberEntity = ctfUtilService
        .getTeamHasMemberEntity(ctfId, leaveMember.getId());
    CtfTeamEntity leftTeam = leaveTeamHasMemberEntity.getTeam();

    ctfUtilService.checkJoinable(leftTeam.getCtfContestEntity().getId());

    teamHasMemberRepository.delete(leaveTeamHasMemberEntity);
    if (isTeamCreator(leaveMember, leftTeam)) {
      removeTeam(leftTeam);
      ctfUtilService.setAllDynamicScore();
    }

    return CtfTeamDetailDto.toDto(leftTeam, new ArrayList<>());
  }

  private void removeTeam(CtfTeamEntity leftTeam) {
    teamHasMemberRepository.deleteAllByTeamId(leftTeam.getId());
    flagRepository.deleteAllByCtfTeamEntityId(leftTeam.getId());
    teamRepository.delete(leftTeam);
  }

  private boolean isTeamCreator(MemberEntity leaveMember, CtfTeamEntity leftTeam) {
    return leftTeam.getCreator().getId().equals(leaveMember.getId());
  }

  public CtfTeamDetailDto getTeamDetail(Long teamId) {
    ctfUtilService.checkVirtualTeam(teamId);

    CtfTeamEntity teamEntity = teamRepository.findById(teamId)
        .orElseThrow(CustomCtfTeamNotFoundException::new);

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
}
