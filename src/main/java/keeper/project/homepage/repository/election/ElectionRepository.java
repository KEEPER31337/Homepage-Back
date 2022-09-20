package keeper.project.homepage.repository.election;

import keeper.project.homepage.election.entity.ElectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<ElectionEntity, Long> {

  Page<ElectionEntity> findAllByIdIsNot(Long electionId, Pageable pageable);

  Page<ElectionEntity> findAllByIdIsNotAndIsAvailableIsTrue(Long electionId, Pageable pageable);

  Page<ElectionEntity> findAllByIdIsNotAndIsAvailableIsFalse(Long electionId, Pageable pageable);

}
