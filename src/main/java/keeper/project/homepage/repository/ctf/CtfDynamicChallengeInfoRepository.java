package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfDynamicChallengeInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfDynamicChallengeInfoRepository extends
    JpaRepository<CtfDynamicChallengeInfoEntity, CtfChallengeEntity> {

}
