package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeTypeRepository extends
    JpaRepository<CtfChallengeTypeEntity, Long> {

}
