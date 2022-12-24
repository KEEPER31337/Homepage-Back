package keeper.project.homepage.ctf.repository;

import keeper.project.homepage.ctf.entity.CtfChallengeHasCtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeHasCtfChallengeCategoryEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeHasCtfChallengeCategoryRepository extends
    JpaRepository<CtfChallengeHasCtfChallengeCategoryEntity, CtfChallengeHasCtfChallengeCategoryEntityPK> {

}
