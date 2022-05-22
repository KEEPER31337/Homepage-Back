package keeper.project.homepage.repository.ctf;

import java.util.Optional;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfTeamRepository extends
    JpaRepository<CtfTeamEntity, Long> {

  Optional<CtfTeamEntity> findByCreatorId(Long creator_id);

  Long countByIdIsNot(Long id);
}
