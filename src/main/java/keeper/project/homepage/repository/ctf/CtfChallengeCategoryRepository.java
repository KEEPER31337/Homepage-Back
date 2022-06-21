package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeCategoryRepository extends
    JpaRepository<CtfChallengeCategoryEntity, Long> {

}
