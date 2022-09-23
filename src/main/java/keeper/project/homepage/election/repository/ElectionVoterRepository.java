package keeper.project.homepage.election.repository;

import java.util.List;
import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.election.entity.ElectionVoterEntity;
import keeper.project.homepage.election.entity.ElectionVoterPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionVoterRepository extends JpaRepository<ElectionVoterEntity, ElectionVoterPK> {

  List<ElectionVoterEntity> findAllByElectionVoterPK_Election(ElectionEntity election);

  Integer countAllByElectionVoterPK_ElectionAndIsVotedIsTrue(ElectionEntity election);

}
