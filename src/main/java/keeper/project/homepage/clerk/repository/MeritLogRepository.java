package keeper.project.homepage.clerk.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.clerk.entity.MeritLogEntity;
import keeper.project.homepage.clerk.entity.MeritTypeEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeritLogRepository extends JpaRepository<MeritLogEntity, Long> {

  @Query("SELECT m FROM MeritLogEntity m WHERE YEAR(m.date) = :year")
  List<MeritLogEntity> findAllByYear(@Param("year") Integer year);

  Optional<MeritLogEntity> findFirstByOrderByDate();
  Optional<MeritLogEntity> findFirstByOrderByDateDesc();

  // 결석일 때만 사용해야 함
  List<MeritLogEntity> findByAwarderAndMeritTypeAndDate(MemberEntity awarder, MeritTypeEntity meritType,
      LocalDate date);

  boolean existsByAwarderAndMeritTypeAndDate(MemberEntity awarder, MeritTypeEntity meritType,
      LocalDate date);

  boolean existsByMeritType(MeritTypeEntity meritType);
}
