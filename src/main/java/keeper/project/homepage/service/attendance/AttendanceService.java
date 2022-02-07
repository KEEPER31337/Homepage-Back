package keeper.project.homepage.service.attendance;

import static keeper.project.homepage.service.attendance.DateUtils.clearTime;
import static keeper.project.homepage.service.attendance.DateUtils.isBeforeDay;
import static keeper.project.homepage.service.attendance.DateUtils.isToday;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomAttendanceException;
import keeper.project.homepage.repository.attendance.AttendanceRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final MemberRepository memberRepository;
  private final AuthService authService;

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

  public void updateGreeting(AttendanceDto attendanceDto) {
    AttendanceEntity attendanceEntity = getMostRecentlyAttendance();

    String greeting = attendanceDto.getGreetings();
    attendanceEntity.setGreetings(greeting);
    attendanceRepository.save(attendanceEntity);
  }

  public List<String> getMyAttendanceDateList(AttendanceDto attendanceDto) {
    List<AttendanceEntity> attendanceEntities = getAttendanceEntitiesInPeriodWithMemberId(
        attendanceDto);

    List<String> myAttendanceDateList = new ArrayList<>();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    for (AttendanceEntity attendance : attendanceEntities) {
      myAttendanceDateList.add(dateFormat.format(attendance.getTime()));
    }
    return myAttendanceDateList;
  }

  public AttendanceEntity getMyAttendance(AttendanceDto attendanceDto) {

    return getMyAttendanceWithDate(attendanceDto);
  }

  public List<AttendanceEntity> getAllAttendance(AttendanceDto attendanceDto) {

    LocalDate date = attendanceDto.getDate();
    if (date == null) {
      throw new CustomAttendanceException("date를 입력하지 않았습니다.");
    }
    LocalDate startDate = date.atStartOfDay().toLocalDate();
    LocalDate endDate = date.plusDays(1).atStartOfDay().toLocalDate();

    return attendanceRepository.findAllByTimeBetween(
        java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
  }

  private List<AttendanceEntity> getAttendanceEntitiesInPeriodWithMemberId(
      AttendanceDto attendanceDto) {
    LocalDate startDate =
        attendanceDto.getStartDate() == null ? LocalDate.EPOCH : attendanceDto.getStartDate();
    LocalDate endDate =
        attendanceDto.getEndDate() == null ? LocalDate.now() : attendanceDto.getEndDate();

    if (startDate.isAfter(endDate)) {
      throw new CustomAttendanceException("시작 날짜와 종료 날짜를 잘못 입력하였습니다.");
    }
    MemberEntity member = getMemberEntity();

    return attendanceRepository.findByMemberIdAndTimeBetween(
        member, java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
  }

  private AttendanceEntity getMostRecentlyAttendance() {
    MemberEntity memberEntity = getMemberEntity();
    Optional<AttendanceEntity> attendanceEntity = attendanceRepository
        .findTopByMemberIdOrderByIdDesc(memberEntity);

    if (attendanceEntity.isEmpty() || !isToday(attendanceEntity.get().getTime())) {
      throw new CustomAttendanceException("출석을 하지 않았습니다.");
    }
    return attendanceEntity.get();
  }

  private MemberEntity getMemberEntity() {
    Long memberId = authService.getMemberIdByJWT();
    Optional<MemberEntity> member = memberRepository.findById(memberId);
    if (member.isEmpty()) {
      throw new CustomAttendanceException("존재하지 않는 회원 입니다.");
    }
    return member.get();
  }

  private AttendanceEntity getMyAttendanceWithDate(AttendanceDto attendanceDto) {

    MemberEntity member = getMemberEntity();

    LocalDate date = attendanceDto.getDate();
    if (date == null) {
      throw new CustomAttendanceException("date를 입력하지 않았습니다.");
    }
    LocalDate startDate = date.atStartOfDay().toLocalDate();
    LocalDate endDate = date.plusDays(1).atStartOfDay().toLocalDate();

    List<AttendanceEntity> attendanceEntities = attendanceRepository.findByMemberIdAndTimeBetween(
        member, java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));

    if (attendanceEntities.size() != 1) {
      throw new CustomAttendanceException("출석 저장에 문제가 생겼습니다");
    }
    return attendanceEntities.get(0);
  }
}
