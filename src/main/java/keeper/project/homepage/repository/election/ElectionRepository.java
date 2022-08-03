package keeper.project.homepage.repository.election;

import keeper.project.homepage.entity.election.ElectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<ElectionEntity, Long> {

}
