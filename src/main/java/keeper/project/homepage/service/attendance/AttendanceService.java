package keeper.project.homepage.service.attendance;

import static keeper.project.homepage.service.attendance.DateUtils.clearTime;
import static keeper.project.homepage.service.attendance.DateUtils.isBeforeDay;
import static keeper.project.homepage.service.attendance.DateUtils.isToday;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  public static final int FIRST_PLACE_POINT = 500;
  public static final int SECOND_PLACE_POINT = 300;
  public static final int THIRD_PLACE_POINT = 100;
  public static final int WEEK_ATTENDANCE = 7 - 1;
  public static final int MONTH_ATTENDANCE = 28 - 1;
  public static final int YEAR_ATTENDANCE = 365 - 1;
  public static final int WEEK_ATTENDANCE_POINT = 3000;
  public static final int MONTH_ATTENDANCE_POINT = 10000;
  public static final int YEAR_ATTENDANCE_POINT = 100000;

  public boolean save(AttendanceDto attendanceDto) {

    if (isAlreadyAttendance()) {
      throw new CustomAttendanceException("이미 출석을 완료했습니다.");
    }
    Date now = java.sql.Timestamp.valueOf(LocalDateTime.now());

    int point = 0;
    int rank = getMyTodayRank(now);
    if (rank == 1) {
      point += FIRST_PLACE_POINT;
    } else if (rank == 2) {
      point += SECOND_PLACE_POINT;
    } else if (rank == 3) {
      point += THIRD_PLACE_POINT;
    }

    int continousDay = getContinousDay(now);
    if (continousDay == WEEK_ATTENDANCE) {
      point += WEEK_ATTENDANCE_POINT;
    } else if (continousDay == MONTH_ATTENDANCE) {
      point += MONTH_ATTENDANCE_POINT;
    } else if (continousDay == YEAR_ATTENDANCE) {
      point += YEAR_ATTENDANCE_POINT;
    }

    Random random = new Random();
    MemberEntity memberEntity = getMemberEntityWithJWT();
    attendanceRepository.save(
        AttendanceEntity.builder()
            .point(point)
            .continousDay(continousDay)
            .greetings(attendanceDto.getGreetings())
            .ipAddress(attendanceDto.getIpAddress())
            .time(now)
            .memberId(memberEntity)
            .randomPoint(random.nextInt(100, 1001))
            .build());

    return true;
  }

  private int getMyTodayRank(Date now) {
    List<AttendanceEntity> attendanceEntitiesByDate = attendanceRepository
        .findAllByTimeBetween(clearTime(now), now);
    return attendanceEntitiesByDate.size() + 1;
  }

  public void updateGreeting(AttendanceDto attendanceDto) {
    AttendanceEntity attendanceEntity = getMostRecentlyAttendance();

    if (!isToday(attendanceEntity.getTime())) {
      throw new CustomAttendanceException("출석을 하지 않았습니다.");
    }
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
    MemberEntity member = getMemberEntityWithJWT();

    return attendanceRepository.findByMemberIdAndTimeBetween(
        member, java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
  }

  private AttendanceEntity getMostRecentlyAttendance() {
    MemberEntity memberEntity = getMemberEntityWithJWT();
    Optional<AttendanceEntity> attendanceEntity = attendanceRepository
        .findTopByMemberIdOrderByIdDesc(memberEntity);

    if (attendanceEntity.isEmpty()) {
      throw new CustomAttendanceException("출석을 하지 않았습니다.");
    }
    return attendanceEntity.get();
  }

  private MemberEntity getMemberEntityWithJWT() {
    Long memberId = authService.getMemberIdByJWT();
    Optional<MemberEntity> member = memberRepository.findById(memberId);
    if (member.isEmpty()) {
      throw new CustomAttendanceException("존재하지 않는 회원 입니다.");
    }
    return member.get();
  }

  private AttendanceEntity getMyAttendanceWithDate(AttendanceDto attendanceDto) {

    MemberEntity member = getMemberEntityWithJWT();

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

  private int getContinousDay(Date now) {
    AttendanceEntity recentAttendanceEntity = getMostRecentlyAttendance();

    int continousDay = 0;
    if (isBeforeDay(recentAttendanceEntity.getTime(), now)) {
      continousDay = recentAttendanceEntity.getContinousDay() + 1;
    }
    return continousDay;
  }

  private boolean isAlreadyAttendance() {
    AttendanceEntity recentAttendanceEntity = getMostRecentlyAttendance();
    return isToday(recentAttendanceEntity.getTime());
  }
}
