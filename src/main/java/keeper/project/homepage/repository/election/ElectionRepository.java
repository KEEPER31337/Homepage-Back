package keeper.project.homepage.repository.election;

import keeper.project.homepage.entity.election.ElectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<ElectionEntity, Long> {

  Page<ElectionEntity> findAllByIdIsNot(Long electionId, Pageable pageable);

}
