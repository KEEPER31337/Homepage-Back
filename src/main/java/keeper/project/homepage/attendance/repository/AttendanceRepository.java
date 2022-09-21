package keeper.project.homepage.attendance.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.attendance.entity.AttendanceEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

  Optional<AttendanceEntity> findTopByMemberOrderByIdDesc(MemberEntity memberEntity);

  List<AttendanceEntity> findAllByMember(MemberEntity memberEntity);

  List<AttendanceEntity> findAllByTimeBetween(LocalDateTime time, LocalDateTime time2);

  List<AttendanceEntity> findAllByTimeBetweenAndMemberNotLike(LocalDateTime time,
      LocalDateTime time2, MemberEntity member);

  List<AttendanceEntity> findByMemberAndTimeBetween(
      MemberEntity member, LocalDateTime time, LocalDateTime time2);

  Long countByMember(MemberEntity memberEntity);
}
