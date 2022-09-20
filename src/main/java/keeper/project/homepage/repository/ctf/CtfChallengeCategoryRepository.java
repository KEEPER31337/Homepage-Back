package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeCategoryRepository extends
    JpaRepository<CtfChallengeCategoryEntity, Long> {

}
