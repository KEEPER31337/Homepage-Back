package keeper.project.homepage.repository.ctf;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfFlagRepository extends
    JpaRepository<CtfFlagEntity, Long> {

  Optional<CtfFlagEntity> findByCtfChallengeEntityIdAndCtfTeamEntityId(Long ctfChallengeEntity_id,
      Long ctfTeamEntity_id);

  List<CtfFlagEntity> findAllByCtfChallengeEntityIdAndIsCorrect(Long ctfChallengeEntity_id,
      Boolean isCorrect);

  List<CtfFlagEntity> findAllByCtfTeamEntityId(Long ctfTeamEntity_id);

  List<CtfFlagEntity> findAllByCtfTeamEntityIdAndIsCorrectTrue(Long ctfTeamEntity_id);

  Long countByCtfChallengeEntityIdAndIsCorrect(Long ctfChallengeEntity_id, Boolean isCorrect);

  List<CtfFlagEntity> findAllByCtfChallengeEntityId(Long id);

  void deleteAllByCtfTeamEntityId(Long ctfTeamEntity_id);
}
