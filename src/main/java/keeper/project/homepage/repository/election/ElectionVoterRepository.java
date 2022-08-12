package keeper.project.homepage.repository.election;

import java.util.List;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.election.ElectionVoterPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionVoterRepository extends JpaRepository<ElectionVoterEntity, ElectionVoterPK> {

  List<ElectionVoterEntity> findAllByElectionVoterPK_Election(ElectionEntity election);

  Integer countAllByElectionVoterPK_ElectionAndIsVotedIsTrue(ElectionEntity election);

}
