package keeper.project.homepage.repository.ctf;

import java.util.List;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfContestRepository extends
    JpaRepository<CtfContestEntity, Long> {

  Page<CtfContestEntity> findAllByIdIsNotOrderByIdDesc(Long id, Pageable pageable);

  List<CtfContestEntity> findAllByIsJoinableTrueOrderByIdDesc();
}
