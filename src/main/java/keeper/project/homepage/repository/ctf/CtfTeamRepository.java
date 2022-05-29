package keeper.project.homepage.repository.ctf;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfTeamRepository extends
    JpaRepository<CtfTeamEntity, Long> {

  Page<CtfTeamEntity> findAllByIdIsNotAndCtfContestEntity_Id(Long id, Long ctfContestEntity_id,
      Pageable pageable);

  List<CtfTeamEntity> findAllByIdOrCtfContestEntityId(Long id, Long ctfContestEntity_id);

  Optional<CtfTeamEntity> findByCreatorId(Long creator_id);

  Long countByIdIsNotAndCtfContestEntity(Long id, CtfContestEntity ctfContestEntity);

  Optional<CtfTeamEntity> findByName(String name);
}
