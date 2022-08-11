package keeper.project.homepage.repository.election;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionChartLogEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.election.ElectionVoterPK;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ElectionRepositoryTestHelper {

  @Autowired
  protected EntityManager em;

  @Autowired
  protected MemberRepository memberRepository;

  @Autowired
  protected ElectionRepository electionRepository;

  @Autowired
  protected ElectionCandidateRepository electionCandidateRepository;

  @Autowired
  protected ElectionChartLogRepository electionChartLogRepository;

  @Autowired
  protected ElectionVoterRepository electionVoterRepository;

  protected ElectionEntity generateElection(MemberEntity creator, Boolean isAvailable) {
    final long epochTime = System.nanoTime();
    return electionRepository.save(
        ElectionEntity.builder()
            .name("name_" + epochTime)
            .description("description_" + epochTime)
            .registerTime(LocalDateTime.now())
            .creator(creator)
            .isAvailable(isAvailable)
            .build()
    );
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

  protected ElectionChartLogEntity generateElectionChartLog(ElectionCandidateEntity candidate) {
    return electionChartLogRepository.save(
        ElectionChartLogEntity.builder()
            .electionCandidate(candidate)
            .voteTime(LocalDateTime.now())
            .build()
    );
  }

  protected ElectionVoterEntity generateElectionVoter(MemberEntity voter, ElectionEntity election,
      Boolean isVoted) {
    ElectionVoterPK pk = new ElectionVoterPK(voter, election);
    return electionVoterRepository.save(
        ElectionVoterEntity.builder()
            .electionVoterPK(pk)
            .isVoted(isVoted)
            .build()
    );
  }

}
