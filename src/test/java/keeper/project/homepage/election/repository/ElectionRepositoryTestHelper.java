package keeper.project.homepage.election.repository;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import keeper.project.homepage.election.entity.ElectionCandidateEntity;
import keeper.project.homepage.election.entity.ElectionChartLogEntity;
import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.election.entity.ElectionVoterEntity;
import keeper.project.homepage.election.entity.ElectionVoterPK;
import keeper.project.homepage.election.repository.ElectionCandidateRepository;
import keeper.project.homepage.election.repository.ElectionChartLogRepository;
import keeper.project.homepage.election.repository.ElectionRepository;
import keeper.project.homepage.election.repository.ElectionVoterRepository;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
