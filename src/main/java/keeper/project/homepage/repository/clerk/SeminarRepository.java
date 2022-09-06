package keeper.project.homepage.repository.clerk;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarRepository extends JpaRepository<SeminarEntity, Long> {

  List<SeminarEntity> findAllByOrderByOpenTimeDesc();

  Page<SeminarEntity> findAllByOpenTimeBetweenOrderByOpenTime(Pageable pageable,
      LocalDateTime startDate, LocalDateTime endDate);

  Boolean existsByName(String name);
}
