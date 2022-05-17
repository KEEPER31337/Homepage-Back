package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfDynamicChallengeInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfDynamicChallengeInfoRepository extends
    JpaRepository<CtfDynamicChallengeInfoEntity, Long> {

}
