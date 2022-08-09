package keeper.project.homepage.repository.election;

import java.util.List;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.election.ElectionVoterPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionVoterRepository extends JpaRepository<ElectionVoterEntity, ElectionVoterPK> {

}
