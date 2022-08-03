package keeper.project.homepage.controller.election;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.election.ElectionVoterPK;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.repository.election.ElectionCandidateRepository;
import keeper.project.homepage.repository.election.ElectionRepository;
import keeper.project.homepage.repository.election.ElectionVoterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.FieldDescriptor;

public class ElectionSpringTestHelper extends ApiControllerTestHelper {

  @Autowired
  protected ElectionRepository electionRepository;

  @Autowired
  protected ElectionCandidateRepository electionCandidateRepository;

  @Autowired
  protected ElectionVoterRepository electionVoterRepository;

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
    return electionCandidateRepository.save(
        ElectionCandidateEntity.builder()
            .candidate(candidate)
            .election(election)
            .description("description_" + epochTime)
            .registerTime(LocalDateTime.now())
            .voteCount(0)
            .memberJob(memberJob)
            .build()
    );
  }

  protected ElectionVoterEntity generateElectionVoter(MemberEntity voter, ElectionEntity election) {
    ElectionVoterPK pk = new ElectionVoterPK(voter, election);
    return electionVoterRepository.save(
        ElectionVoterEntity.builder()
            .electionVoterPK(pk)
            .isVoted(false)
            .build()
    );
  }

}
