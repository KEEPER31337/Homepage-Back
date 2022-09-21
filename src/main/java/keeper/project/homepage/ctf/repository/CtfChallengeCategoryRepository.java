package keeper.project.homepage.ctf.repository;

import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeCategoryRepository extends
    JpaRepository<CtfChallengeCategoryEntity, Long> {

}
