package keeper.project.homepage.repository.attendance;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

  Optional<AttendanceEntity> findTopByMemberOrderByIdDesc(MemberEntity memberEntity);

  List<AttendanceEntity> findAllByMember(MemberEntity memberEntity);

  List<AttendanceEntity> findAllByTimeBetween(LocalDateTime time, LocalDateTime time2);

  List<AttendanceEntity> findAllByTimeBetweenAndMemberNotLike(LocalDateTime time,
      LocalDateTime time2, MemberEntity member);

  List<AttendanceEntity> findByMemberAndTimeBetween(
      MemberEntity member, LocalDateTime time, LocalDateTime time2);

}
