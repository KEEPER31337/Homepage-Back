package keeper.project.homepage.admin.service.election;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.admin.dto.election.request.ElectionCandidateCreateRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCreateRequestDto;
import keeper.project.homepage.admin.dto.election.response.ElectionCandidateDeleteResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionDeleteResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionUpdateResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionVoterCreateResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionVoterResponseDto;
import keeper.project.homepage.admin.service.member.AdminMemberUtilService;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.election.ElectionVoterPK;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.exception.election.CustomElectionCandidateExistException;
import keeper.project.homepage.exception.election.CustomElectionCandidateNotFoundException;
import keeper.project.homepage.exception.election.CustomElectionIsNotClosedException;
import keeper.project.homepage.exception.election.CustomElectionVoterExistException;
import keeper.project.homepage.exception.election.CustomElectionVoterNotFoundException;
import keeper.project.homepage.repository.election.ElectionCandidateRepository;
import keeper.project.homepage.repository.election.ElectionRepository;
import keeper.project.homepage.repository.election.ElectionVoterRepository;
import keeper.project.homepage.user.dto.election.response.ElectionVoteStatus;
import keeper.project.homepage.util.service.ElectionUtilService;
import keeper.project.homepage.util.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminElectionService {

  private final AuthService authService;
  private final WebSocketService webSocketService;
  private final AdminMemberUtilService adminMemberUtilService;
  private final ElectionUtilService electionUtilService;
  private final ElectionRepository electionRepository;
  private final ElectionCandidateRepository electionCandidateRepository;
  private final ElectionVoterRepository electionVoterRepository;

  private boolean isExistElectionCandidate(MemberEntity candidate, ElectionEntity election,
      MemberJobEntity memberJob) {
    return electionCandidateRepository.existsByCandidateAndElectionAndMemberJob(candidate, election,
        memberJob);
  }

  @Transactional
  public Long createElection(ElectionCreateRequestDto electionCreateRequestDto) {
    MemberEntity creator = authService.getMemberEntityWithJWT();
    electionCreateRequestDto.setRegisterTime(LocalDateTime.now());
    return electionRepository.save(electionCreateRequestDto.toEntity(creator)).getId();
  }

  @Transactional
  public ElectionDeleteResponseDto deleteElection(Long electionId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    if (election.getIsAvailable()) {
      throw new CustomElectionIsNotClosedException();
    }
    electionVoterRepository.deleteAllInBatch(election.getVoters());
    electionRepository.delete(election);
    return ElectionDeleteResponseDto.from(election);
  }

  @Transactional
  public ElectionUpdateResponseDto openElection(Long electionId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    election.openElection();
    return ElectionUpdateResponseDto.from(election);
  }

  @Transactional
  public ElectionUpdateResponseDto closeElection(Long electionId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    election.closeElection();
    return ElectionUpdateResponseDto.from(election);
  }

  public void sendVoteEnd() {
    ElectionVoteStatus status = ElectionVoteStatus.createStatus(0, 0, false);
    webSocketService.sendVoteStatusMessage("/topics/votes/end", status);
  }

  @Transactional
  public Long registerCandidate(ElectionCandidateCreateRequestDto requestDto) {
    ElectionEntity election = electionUtilService.getElectionById(requestDto.getElectionId());
    MemberEntity member = adminMemberUtilService.getMemberById(requestDto.getMemberId());
    MemberJobEntity memberJob = adminMemberUtilService.getJobById(requestDto.getMemberJobId());
    if (isExistElectionCandidate(member, election, memberJob)) {
      throw new CustomElectionCandidateExistException();
    }
    return electionCandidateRepository.save(requestDto.toEntity(member, election, memberJob))
        .getId();
  }

  @Transactional
  public ElectionCandidateDeleteResponseDto deleteCandidate(Long candidateId) {
    ElectionCandidateEntity candidate = electionCandidateRepository.findById(candidateId)
        .orElseThrow(CustomElectionCandidateNotFoundException::new);
    electionCandidateRepository.delete(candidate);
    return ElectionCandidateDeleteResponseDto.from(candidate);
  }

  public List<ElectionVoterResponseDto> getVoters(Long electionId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    List<ElectionVoterEntity> voters = electionVoterRepository.findAllByElectionVoterPK_Election(
        election);
    return voters.stream().map(ElectionVoterResponseDto::from).collect(Collectors.toList());
  }

  @Transactional
  public ElectionVoterCreateResponseDto registerVoter(Long electionId, Long memberId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    MemberEntity member = adminMemberUtilService.getMemberById(memberId);
    ElectionVoterPK pk = new ElectionVoterPK(member, election);
    if (electionVoterRepository.findById(pk).isPresent()) {
      throw new CustomElectionVoterExistException();
    }
    ElectionVoterEntity voter = electionVoterRepository.save(ElectionVoterEntity.createVoter(pk));
    return ElectionVoterCreateResponseDto.from(voter);
  }

  @Transactional
  public Long deleteVoter(Long electionId, Long voterId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    MemberEntity member = adminMemberUtilService.getMemberById(voterId);
    ElectionVoterPK pk = new ElectionVoterPK(member, election);
    ElectionVoterEntity voter = electionVoterRepository.findById(pk).orElseThrow(
        CustomElectionVoterNotFoundException::new);
    electionVoterRepository.delete(voter);
    return member.getId();
  }

}
