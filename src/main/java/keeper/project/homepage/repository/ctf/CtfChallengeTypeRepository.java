package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeTypeRepository extends
    JpaRepository<CtfChallengeTypeEntity, Long> {

}
