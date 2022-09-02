package keeper.project.homepage.repository.clerk;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.clerk.MeritLogEntity;
import keeper.project.homepage.entity.clerk.MeritTypeEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeritLogRepository extends JpaRepository<MeritLogEntity, Long> {

  @Query("SELECT m FROM MeritLogEntity m WHERE YEAR(m.date) = :year")
  List<MeritLogEntity> findAllByYear(@Param("year") Integer year);

  Optional<MeritLogEntity> findFirstByOrderByDate();

  // 결석일 때만 사용해야 함
  List<MeritLogEntity> findByAwarderAndMeritTypeAndDate(MemberEntity awarder, MeritTypeEntity meritType,
      LocalDate date);

}
