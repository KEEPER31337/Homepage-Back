package keeper.project.homepage.ctf.repository;

import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfDynamicChallengeInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfDynamicChallengeInfoRepository extends
    JpaRepository<CtfDynamicChallengeInfoEntity, CtfChallengeEntity> {

}
