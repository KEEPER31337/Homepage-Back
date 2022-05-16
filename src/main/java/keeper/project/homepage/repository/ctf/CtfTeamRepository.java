package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfTeamRepository extends
    JpaRepository<CtfTeamEntity, Long> {

}
