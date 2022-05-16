package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeRepository extends
    JpaRepository<CtfChallengeEntity, Long> {

}
