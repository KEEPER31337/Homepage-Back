package keeper.project.homepage.repository.clerk;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.entity.clerk.MeritLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeritLogRepository extends JpaRepository<MeritLogEntity, Long> {

  @Query("SELECT m FROM MeritLogEntity m WHERE YEAR(m.time) = :year")
  List<MeritLogEntity> findAllByYear(@Param("year") Integer year);

  @Query("SELECT YEAR(m.time) from MeritLogEntity m GROUP BY YEAR(m.time) ORDER BY YEAR(m.time)")
  List<Integer> findYears();
}
