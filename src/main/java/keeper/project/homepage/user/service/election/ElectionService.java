package keeper.project.homepage.user.service.election;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.election.entity.ElectionCandidateEntity;
import keeper.project.homepage.election.entity.ElectionChartLogEntity;
import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.election.entity.ElectionVoterEntity;
import keeper.project.homepage.election.entity.ElectionVoterPK;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.election.exception.CustomCloseElectionVoteException;
import keeper.project.homepage.election.exception.CustomElectionAlreadyVotedException;
import keeper.project.homepage.election.exception.CustomElectionCandidateNotFoundException;
import keeper.project.homepage.election.exception.CustomElectionIsNotClosedException;
import keeper.project.homepage.election.exception.CustomElectionNotMatchCandidateException;
import keeper.project.homepage.election.exception.CustomElectionVoteCountNotMatchException;
import keeper.project.homepage.election.exception.CustomElectionVoteDuplicationJobException;
import keeper.project.homepage.election.exception.CustomElectionVoterNotFoundException;
import keeper.project.homepage.election.repository.ElectionCandidateRepository;
import keeper.project.homepage.election.repository.ElectionChartLogRepository;
import keeper.project.homepage.election.repository.ElectionRepository;
import keeper.project.homepage.election.repository.ElectionVoterRepository;
import keeper.project.homepage.user.dto.election.request.ElectionVoteRequestDto;
import keeper.project.homepage.user.dto.election.response.ElectionCandidatesResponseDto;
import keeper.project.homepage.user.dto.election.response.ElectionResponseDto;
import keeper.project.homepage.user.dto.election.response.ElectionResultResponseDto;
import keeper.project.homepage.user.dto.election.response.ElectionVoteStatus;
import keeper.project.homepage.user.service.member.MemberUtilService;
import keeper.project.homepage.util.service.ElectionUtilService;
import keeper.project.homepage.util.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ElectionService {

  private final AuthService authService;
  private final WebSocketService webSocketService;
  private final ElectionUtilService electionUtilService;
  private final MemberUtilService memberUtilService;
  private final ElectionRepository electionRepository;
  private final ElectionCandidateRepository electionCandidateRepository;
  private final ElectionVoterRepository electionVoterRepository;
  private final ElectionChartLogRepository electionChartLogRepository;

  public Page<ElectionResponseDto> getElections(Pageable pageable) {
    return electionRepository.findAllByIdIsNot(ElectionUtilService.VIRTUAL_ELECTION_ID, pageable)
        .map(ElectionResponseDto::from);
  }

  public Page<ElectionResponseDto> getOpenElections(Pageable pageable) {
    return electionRepository.findAllByIdIsNotAndIsAvailableIsTrue(
            ElectionUtilService.VIRTUAL_ELECTION_ID, pageable)
        .map(ElectionResponseDto::from);
  }

  public Page<ElectionResponseDto> getCloseElections(Pageable pageable) {
    return electionRepository.findAllByIdIsNotAndIsAvailableIsFalse(
            ElectionUtilService.VIRTUAL_ELECTION_ID, pageable)
        .map(ElectionResponseDto::from);
  }

  public Boolean joinElection(Long electionId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    MemberEntity member = authService.getMemberEntityWithJWT();
    ElectionVoterPK pk = new ElectionVoterPK(member, election);
    boolean voterExist = electionVoterRepository.existsById(pk);
    return voterExist ? election.getIsAvailable() : false;
  }

  public List<ElectionCandidatesResponseDto> getCandidates(Long electionId, Long jobId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    MemberJobEntity memberJob = memberUtilService.getJobById(jobId);
    return electionCandidateRepository.findAllByElectionAndMemberJob(election, memberJob)
        .stream().map(ElectionCandidatesResponseDto::from).toList();
  }

  public ElectionVoteStatus getVoteStatus(Long electionId) {
    return electionUtilService.getVoteStatus(electionId);
  }

  @Transactional
  public Boolean voteElection(ElectionVoteRequestDto requestDto) {
    ElectionEntity election = electionUtilService.getElectionById(requestDto.getElectionId());
    validateOpenElection(election);
    MemberEntity member = memberUtilService.getById(requestDto.getVoterId());
    ElectionVoterPK pk = new ElectionVoterPK(member, election);
    ElectionVoterEntity voter = electionVoterRepository.findById(pk)
        .orElseThrow(CustomElectionVoterNotFoundException::new);
    validateVoted(voter);
    validateCandidatesCount(election, requestDto.getCandidateIds());
    voteCandidates(election, requestDto.getCandidateIds());
    voter.vote();
    return voter.getIsVoted();
  }

  private void validateVoted(ElectionVoterEntity voter) {
    if (voter.getIsVoted()) {
      throw new CustomElectionAlreadyVotedException();
    }
  }

  private void validateOpenElection(ElectionEntity election) {
    if (!election.getIsAvailable()) {
      throw new CustomCloseElectionVoteException();
    }
  }

  private void validateCandidatesCount(ElectionEntity election, List<Long> candidateIds) {
    Long electionJobCount = electionCandidateRepository.getDistinctCountMemberJobByElection(
        election);
    if (candidateIds.size() != electionJobCount) {
      throw new CustomElectionVoteCountNotMatchException();
    }
  }

  private void voteCandidates(ElectionEntity election, List<Long> candidateIds) {
    Set<MemberJobEntity> candidateJobs = new HashSet<>();
    for (Long candidateId : candidateIds) {
      ElectionCandidateEntity candidate = electionCandidateRepository.findById(candidateId)
          .orElseThrow(CustomElectionCandidateNotFoundException::new);
      if (election != candidate.getElection()) {
        throw new CustomElectionNotMatchCandidateException();
      }
      if (candidateJobs.contains(candidate.getMemberJob())) {
        throw new CustomElectionVoteDuplicationJobException();
      }
      electionChartLogRepository.save(ElectionChartLogEntity.createChartLog(candidate));
      candidate.gainVote();
      candidateJobs.add(candidate.getMemberJob());
    }
  }

  public void sendVoteStatus(Long electionId) {
    ElectionVoteStatus status = electionUtilService.getVoteStatus(electionId);
    webSocketService.sendVoteStatusMessage("/topics/votes/result", status);
  }

  public Boolean isVoted(Long electionId, Long voterId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    MemberEntity member = memberUtilService.getById(voterId);
    ElectionVoterPK pk = new ElectionVoterPK(member, election);
    ElectionVoterEntity voter = electionVoterRepository.findById(pk)
        .orElseThrow(CustomElectionVoterNotFoundException::new);
    return voter.getIsVoted();
  }

  public List<ElectionResultResponseDto> countVotes(Long electionId, Long jobId) {
    ElectionEntity election = electionUtilService.getElectionById(electionId);
    MemberJobEntity memberJob = memberUtilService.getJobById(jobId);
    validateCloseElection(election);
    List<ElectionChartLogEntity> electionChartLog = electionChartLogRepository.findAllByElectionCandidate_ElectionAndElectionCandidate_MemberJobOrderById(
        election, memberJob);
    return electionChartLog.stream().map(ElectionResultResponseDto::from).toList();
  }

  private void validateCloseElection(ElectionEntity election) {
    if (election.getIsAvailable()) {
      throw new CustomElectionIsNotClosedException();
    }
  }

}
