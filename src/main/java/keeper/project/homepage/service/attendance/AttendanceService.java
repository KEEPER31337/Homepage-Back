package keeper.project.homepage.service.attendance;

import static keeper.project.homepage.service.attendance.DateUtils.clearTime;
import static keeper.project.homepage.service.attendance.DateUtils.isBeforeDay;
import static keeper.project.homepage.service.attendance.DateUtils.isToday;

import java.util.Date;
import java.util.List;
import java.util.Random;
import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.attendance.AttendanceRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final MemberRepository memberRepository;

  public boolean save(AttendanceDto attendanceDto) {

    MemberEntity memberEntity = memberRepository.findById(attendanceDto.getMemberId()).get();
    List<AttendanceEntity> attendanceEntities = attendanceRepository.findAllByMemberId(
        memberEntity);

    boolean checked = false;
    int continousDay = 0;
    Date now = java.sql.Timestamp.valueOf(attendanceDto.getTime());
    for (AttendanceEntity attendanceEntity : attendanceEntities) {
      if (isToday(attendanceEntity.getTime())) {
        checked = true;
      } else if (isBeforeDay(attendanceEntity.getTime(), now)) {
        continousDay = attendanceEntity.getContinousDay() + 1;
      }
    }

    if (checked) {
      return false;
    }

    int point = 0;
    List<AttendanceEntity> attendanceEntitiesByDate = attendanceRepository.findAllByTimeBetween(
        clearTime(now), now);
    int size = attendanceEntitiesByDate.size();
    if (size == 0) {
      point += 500;
    } else if (size == 1) {
      point += 300;
    } else if (size == 2) {
      point += 100;
    }

    if (continousDay == 6) {
      point += 3000;
    } else if (continousDay == 27) {
      point += 10000;
    } else if (continousDay == 364) {
      point += 100000;
    }

    Random random = new Random();

    attendanceRepository.save(
        AttendanceEntity.builder().point(point).continousDay(continousDay).greetings(
                attendanceDto.getGreetings()).ipAddress(attendanceDto.getIpAddress()).time(now)
            .memberId(memberEntity).randomPoint(random.nextInt(100, 1001)).build());

    return true;
  }
}
