package keeper.project.homepage.repository.clerk;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeminarRepository extends JpaRepository<SeminarEntity, Long> {

  List<SeminarEntity> findAllByOrderByOpenTimeDesc();

  Page<SeminarEntity> findAllByOpenTimeBetweenOrderByOpenTimeDesc(Pageable pageable,
      LocalDateTime startDate, LocalDateTime endDate);

  Boolean existsByName(String name);

  Optional<SeminarEntity> findByName(String name);

  @Query("select s from SeminarEntity s"
      + " where s.name = :name"
      + " and s.attendanceCode is not null"
      + " and s.latenessCloseTime >= :now")
  Optional<SeminarEntity> findSeminarOngoingAttendance(@Param("name") String name,
      @Param("now") LocalDateTime now);
}
