package keeper.project.homepage.repository.election;

import java.util.List;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionCandidateRepository extends JpaRepository<ElectionCandidateEntity, Long> {

  List<ElectionCandidateEntity> findByCandidateAndElectionAndMemberJob(MemberEntity candidate,
      ElectionEntity election, MemberJobEntity memberJob);

  List<ElectionCandidateEntity> findAllByElectionAndMemberJob(ElectionEntity election,
      MemberJobEntity memberJob);

  Long countDistinctMemberJobByElection(ElectionEntity election);
}
