package keeper.project.homepage.service.attendance;


import static keeper.project.homepage.dto.attendance.AttendancePointDto.*;
import static keeper.project.homepage.service.attendance.DateUtils.clearTime;
import static keeper.project.homepage.service.attendance.DateUtils.isBeforeDay;
import static keeper.project.homepage.service.attendance.DateUtils.isToday;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.dto.attendance.AttendancePointDto;
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomAttendanceException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
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

  private static final String DEFAULT_GREETINGS = "자동 출석입니다.";

  public void save(AttendanceDto attendanceDto) {

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

    MemberEntity memberEntity = getMemberEntityWithJWT();
    String greeting = attendanceDto.getGreetings();
    if (greeting == "" || greeting == null) {
      greeting = DEFAULT_GREETINGS;
    }
    attendanceRepository.save(
        AttendanceEntity.builder()
            .point(point)
            .continousDay(continousDay)
            .greetings(greeting)
            .ipAddress(attendanceDto.getIpAddress())
            .time(now)
            .memberId(memberEntity)
            .randomPoint((int) (Math.random() * 900 + 100))
            .rank(rank)
            .build());
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
    if (greeting == "" || greeting == null) {
      greeting = DEFAULT_GREETINGS;
    }
    attendanceEntity.setGreetings(greeting);
    attendanceRepository.save(attendanceEntity);
  }

  public List<String> getMyAttendanceDateList(LocalDate startDate, LocalDate endDate) {
    List<AttendanceEntity> attendanceEntities = getAttendanceEntitiesInPeriodWithMemberId(
        startDate, endDate);

    List<String> myAttendanceDateList = new ArrayList<>();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    for (AttendanceEntity attendance : attendanceEntities) {
      myAttendanceDateList.add(dateFormat.format(attendance.getTime()));
    }
    return myAttendanceDateList;
  }

  public AttendanceEntity getMyAttendance(LocalDate date) {

    return getMyAttendanceWithDate(date);
  }

  public List<AttendanceEntity> getAllAttendance(LocalDate date) {
    if (date == null) {
      throw new CustomAttendanceException("date를 입력하지 않았습니다.");
    }
    LocalDate startDate = date.atStartOfDay().toLocalDate();
    LocalDate endDate = date.plusDays(1).atStartOfDay().toLocalDate();

    return attendanceRepository.findAllByTimeBetween(
        java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
  }

  public HashMap<String, Integer> getAllBonusPointInfo() throws IllegalAccessException {
    Field[] declaredFields = AttendancePointDto.class.getDeclaredFields();
    HashMap<String, Integer> staticFields = new HashMap<>();
    for (Field field : declaredFields) {
      if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
        staticFields.put(field.getName(), field.getInt(null));
      }
    }
    return staticFields;
  }

  private List<AttendanceEntity> getAttendanceEntitiesInPeriodWithMemberId(
      LocalDate startDate, LocalDate endDate) {
    startDate = startDate == null ? LocalDate.EPOCH : startDate;
    endDate = endDate == null ? LocalDate.now() : endDate;

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
//      throw new CustomAttendanceException("출석을 하지 않았습니다.");
      return null;
    }
    return attendanceEntity.get();
  }

  private MemberEntity getMemberEntityWithJWT() {
    Long memberId = authService.getMemberIdByJWT();
    Optional<MemberEntity> member = memberRepository.findById(memberId);
    if (member.isEmpty()) {
      throw new CustomMemberNotFoundException();
    }
    return member.get();
  }

  private AttendanceEntity getMyAttendanceWithDate(LocalDate date) {

    if (date == null) {
      throw new CustomAttendanceException("date를 입력하지 않았습니다.");
    }
    MemberEntity member = getMemberEntityWithJWT();

    LocalDate startDate = date.atStartOfDay().toLocalDate();
    LocalDate endDate = date.plusDays(1).atStartOfDay().toLocalDate();

    List<AttendanceEntity> attendanceEntities = attendanceRepository.findByMemberIdAndTimeBetween(
        member, java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));

    if (attendanceEntities.size() != 1) {
      throw new CustomAttendanceException("해당 날짜에 출석하지 않았습니다.");
    }
    return attendanceEntities.get(0);
  }

  private int getContinousDay(Date now) {
    AttendanceEntity recentAttendanceEntity = getMostRecentlyAttendance();
    if (recentAttendanceEntity == null) {
      return 0;
    }

    int continousDay = 0;
    if (isBeforeDay(recentAttendanceEntity.getTime(), now)) {
      continousDay = recentAttendanceEntity.getContinousDay() + 1;
    }
    return continousDay;
  }

  private boolean isAlreadyAttendance() {
    AttendanceEntity recentAttendanceEntity = getMostRecentlyAttendance();
    if (recentAttendanceEntity == null) {
      return false;
    }

    return isToday(recentAttendanceEntity.getTime());
  }
}
