package keeper.project.homepage.repository.ctf;

import java.util.List;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfContestRepository extends
    JpaRepository<CtfContestEntity, Long> {

  List<CtfContestEntity> findAllByIdIsNot(Long id);
}
