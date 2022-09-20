package keeper.project.homepage.controller.election;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.election.entity.ElectionCandidateEntity;
import keeper.project.homepage.election.entity.ElectionChartLogEntity;
import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.election.entity.ElectionVoterEntity;
import keeper.project.homepage.election.entity.ElectionVoterPK;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.repository.election.ElectionCandidateRepository;
import keeper.project.homepage.repository.election.ElectionChartLogRepository;
import keeper.project.homepage.repository.election.ElectionRepository;
import keeper.project.homepage.repository.election.ElectionVoterRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ElectionSpringTestHelper extends ApiControllerTestHelper {

  @Autowired
  protected ElectionRepository electionRepository;

  @Autowired
  protected ElectionCandidateRepository electionCandidateRepository;

  @Autowired
  protected ElectionVoterRepository electionVoterRepository;

  @Autowired
  protected ElectionChartLogRepository electionChartLogRepository;

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected ElectionEntity generateElection(MemberEntity creator, Boolean isAvailable) {
    final long epochTime = System.nanoTime();
    return electionRepository.save(
        ElectionEntity.builder()
            .name("name_" + epochTime)
            .description("description_" + epochTime)
            .registerTime(LocalDateTime.now())
            .creator(creator)
            .isAvailable(isAvailable)
            .build());
  }

  protected ElectionCandidateEntity generateElectionCandidate(MemberEntity candidate,
      ElectionEntity election, MemberJobEntity memberJob) {
    final long epochTime = System.nanoTime();
    ElectionCandidateEntity newCandidate = ElectionCandidateEntity.builder()
        .candidate(candidate)
        .election(election)
        .description("description_" + epochTime)
        .registerTime(LocalDateTime.now())
        .voteCount(0)
        .memberJob(memberJob)
        .build();
    electionCandidateRepository.save(newCandidate);
    election.getCandidates().add(newCandidate);
    return newCandidate;
  }

  protected ElectionVoterEntity generateElectionVoter(MemberEntity voter, ElectionEntity election,
      Boolean isVoted) {
    ElectionVoterPK pk = new ElectionVoterPK(voter, election);
    ElectionVoterEntity newVoter = ElectionVoterEntity.builder()
        .electionVoterPK(pk)
        .isVoted(isVoted)
        .build();
    electionVoterRepository.save(newVoter);
    election.getVoters().add(newVoter);
    return newVoter;
  }

  protected ElectionChartLogEntity generateElectionChartLog(ElectionCandidateEntity candidate) {
    ElectionChartLogEntity newChartLog = ElectionChartLogEntity.builder()
        .electionCandidate(candidate)
        .voteTime(LocalDateTime.now())
        .build();
    electionChartLogRepository.save(newChartLog);
    candidate.getChartLogs().add(newChartLog);
    return newChartLog;
  }

}
