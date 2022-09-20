package keeper.project.homepage.repository.ctf;

import keeper.project.homepage.ctf.entity.CtfSubmitLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfSubmitLogRepository extends
    JpaRepository<CtfSubmitLogEntity, Long> {

  Page<CtfSubmitLogEntity> findAllByIdIsNotAndContestId(Long id, Pageable pageable, Long contestId);
}
