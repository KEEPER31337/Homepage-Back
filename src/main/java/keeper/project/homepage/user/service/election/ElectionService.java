package keeper.project.homepage.user.service.election;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionChartLogEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.election.ElectionVoterPK;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.exception.election.CustomCloseElectionVoteException;
import keeper.project.homepage.exception.election.CustomElectionAlreadyVotedException;
import keeper.project.homepage.exception.election.CustomElectionCandidateNotFoundException;
import keeper.project.homepage.exception.election.CustomElectionIsNotClosedException;
import keeper.project.homepage.exception.election.CustomElectionNotMatchCandidateException;
import keeper.project.homepage.exception.election.CustomElectionVoteCountNotMatchException;
import keeper.project.homepage.exception.election.CustomElectionVoteDuplicationJobException;
import keeper.project.homepage.exception.election.CustomElectionVoterNotFoundException;
import keeper.project.homepage.repository.election.ElectionCandidateRepository;
import keeper.project.homepage.repository.election.ElectionChartLogRepository;
import keeper.project.homepage.repository.election.ElectionRepository;
import keeper.project.homepage.repository.election.ElectionVoterRepository;
import keeper.project.homepage.user.dto.election.request.ElectionVoteRequestDto;
import keeper.project.homepage.user.dto.election.response.ElectionCandidatesResponseDto;
import keeper.project.homepage.user.dto.election.response.ElectionResponseDto;
import keeper.project.homepage.user.dto.election.response.ElectionResultResponseDto;
import keeper.project.homepage.user.service.member.MemberUtilService;
import keeper.project.homepage.util.service.ElectionUtilService;
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
    Long electionJobCount = electionCandidateRepository.countDistinctMemberJobByElection(election);
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
