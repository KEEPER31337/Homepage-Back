package keeper.project.homepage.util.service;

import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.election.exception.CustomElectionNotFoundException;
import keeper.project.homepage.repository.election.ElectionRepository;
import keeper.project.homepage.repository.election.ElectionVoterRepository;
import keeper.project.homepage.user.dto.election.response.ElectionVoteStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ElectionUtilService {

  public static final Long VIRTUAL_ELECTION_ID = 1L;

  private final ElectionRepository electionRepository;
  private final ElectionVoterRepository electionVoterRepository;

  public ElectionEntity getElectionById(Long electionId) {
    return electionRepository.findById(electionId)
        .orElseThrow(CustomElectionNotFoundException::new);
  }

  public ElectionVoteStatus getVoteStatus(Long electionId) {
    ElectionEntity election = getElectionById(electionId);
    Integer total = election.getVoters().size();
    Integer voted = electionVoterRepository.countAllByElectionVoterPK_ElectionAndIsVotedIsTrue(
        election);
    return ElectionVoteStatus.createStatus(total, voted, election.getIsAvailable());
  }

}
