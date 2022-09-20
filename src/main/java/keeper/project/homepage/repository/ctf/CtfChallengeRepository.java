package keeper.project.homepage.repository.ctf;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeRepository extends
    JpaRepository<CtfChallengeEntity, Long> {

  Optional<CtfChallengeEntity> findByIdAndIsSolvableTrue(Long id);

  List<CtfChallengeEntity> findAllByIdIsNotAndCtfContestEntity(Long id,
      CtfContestEntity ctfContestEntity);

  Page<CtfChallengeEntity> findAllByIdIsNotAndCtfContestEntity(Long id,
      CtfContestEntity ctfContestEntity, Pageable pageable);

  List<CtfChallengeEntity> findAllByIdIsNotAndCtfContestEntityAndIsSolvable(Long id,
      CtfContestEntity ctfContestEntity, Boolean isSolvable);

  List<CtfChallengeEntity> findAllByCtfChallengeTypeEntityId(Long ctfChallengeTypeEntity_id);
}
