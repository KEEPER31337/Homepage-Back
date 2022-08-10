package keeper.project.homepage.repository.election;

import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.election.ElectionVoterPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionVoterRepository extends JpaRepository<ElectionVoterEntity, ElectionVoterPK> {

  Integer countAllByElectionVoterPK_ElectionAndIsVotedIsTrue(ElectionEntity election);

}
