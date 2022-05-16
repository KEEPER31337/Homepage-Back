package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfContestRepository extends
    JpaRepository<CtfContestEntity, Long> {

}
