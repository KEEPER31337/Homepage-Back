package keeper.project.homepage.util.service;

import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.exception.election.CustomElectionNotFoundException;
import keeper.project.homepage.repository.election.ElectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ElectionUtilService {

  public static final Long VIRTUAL_ELECTION_ID = 1L;

  private final ElectionRepository electionRepository;

  public ElectionEntity getElectionById(Long electionId) {
    return electionRepository.findById(electionId)
        .orElseThrow(CustomElectionNotFoundException::new);
  }

}
