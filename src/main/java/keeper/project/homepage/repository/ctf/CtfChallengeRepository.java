package keeper.project.homepage.repository.ctf;

import java.util.List;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeRepository extends
    JpaRepository<CtfChallengeEntity, Long> {

  List<CtfChallengeEntity> findAllByIdIsNotAndCtfContestEntity(Long id,
      CtfContestEntity ctfContestEntity);
}
