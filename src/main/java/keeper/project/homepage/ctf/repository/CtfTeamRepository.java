package keeper.project.homepage.ctf.repository;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
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

  Optional<CtfTeamEntity> findByNameAndCtfContestEntity(String name,
      CtfContestEntity ctfContestEntity);
}
