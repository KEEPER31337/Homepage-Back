package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfFlagRepository extends
    JpaRepository<CtfFlagEntity, Long> {

}
