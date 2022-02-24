package keeper.project.homepage.service.attendance;


import static keeper.project.homepage.dto.attendance.AttendancePointDto.DAILY_ATTENDANCE_POINT;
import static keeper.project.homepage.dto.attendance.AttendancePointDto.FIRST_PLACE_POINT;
import static keeper.project.homepage.dto.attendance.AttendancePointDto.MONTH_ATTENDANCE;
import static keeper.project.homepage.dto.attendance.AttendancePointDto.MONTH_ATTENDANCE_POINT;
import static keeper.project.homepage.dto.attendance.AttendancePointDto.SECOND_PLACE_POINT;
import static keeper.project.homepage.dto.attendance.AttendancePointDto.THIRD_PLACE_POINT;
import static keeper.project.homepage.dto.attendance.AttendancePointDto.WEEK_ATTENDANCE;
import static keeper.project.homepage.dto.attendance.AttendancePointDto.WEEK_ATTENDANCE_POINT;
import static keeper.project.homepage.dto.attendance.AttendancePointDto.YEAR_ATTENDANCE;
import static keeper.project.homepage.dto.attendance.AttendancePointDto.YEAR_ATTENDANCE_POINT;
import static keeper.project.homepage.service.attendance.DateUtils.clearTime;
import static keeper.project.homepage.service.attendance.DateUtils.isBeforeDay;
import static keeper.project.homepage.service.attendance.DateUtils.isToday;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.dto.attendance.AttendancePointDto;
import keeper.project.homepage.dto.attendance.AttendanceResultDto;
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
  private List<Integer> DAY_OF_MONTH = new ArrayList<>();

  public static boolean isLeapYear(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
  }

  public void save(AttendanceDto attendanceDto) {

    if (isAlreadyAttendance()) {
      throw new CustomAttendanceException("이미 출석을 완료했습니다.");
    }
    LocalDateTime now = LocalDateTime.now();

    int point = 0;
    int continuousDay = getContinuousDay(Timestamp.valueOf(now));
    int continuousPoint = getContinuousPoint(continuousDay);
    int randomPoint = (int) (Math.random() * 900 + 100);
    point = continuousPoint + DAILY_ATTENDANCE_POINT + randomPoint;

    MemberEntity memberEntity = getMemberEntityWithJWT();
    String greeting = attendanceDto.getGreetings();
    if (greeting == "" || greeting == null) {
      greeting = DEFAULT_GREETINGS;
    }

    AttendanceEntity attendanceEntity = AttendanceEntity.builder()
        .point(point)
        .rankPoint(0)
        .continuousPoint(continuousPoint)
        .continuousDay(continuousDay)
        .greetings(greeting)
        .ipAddress(attendanceDto.getIpAddress())
        .time(now)
        .date(now.toLocalDate())
        .member(memberEntity)
        .randomPoint(randomPoint)
        .rank(0)
        .build();
    attendanceRepository.saveAndFlush(attendanceEntity);

    int rank = getMyTodayRank(now, memberEntity);
    int rankPoint = 0;
    if (rank == 1) {
      rankPoint += FIRST_PLACE_POINT;
    } else if (rank == 2) {
      rankPoint += SECOND_PLACE_POINT;
    } else if (rank == 3) {
      rankPoint += THIRD_PLACE_POINT;
    }

    attendanceEntity.setRank(rank);
    attendanceEntity.setRankPoint(rankPoint);
    attendanceEntity.setPoint(attendanceEntity.getPoint() + rankPoint);
    attendanceRepository.save(attendanceEntity);
  }

  private int getContinuousPoint(int continuousDay) {
    LocalDateTime date = LocalDateTime.now();

    if (isLeapYear(date.getYear())) {
      DAY_OF_MONTH.addAll(List.of(0, 31, 29, 31, 30, 31, 30, 31, 31, 30,
          31, 30, 31));
    } else {
      DAY_OF_MONTH.addAll(List.of(0, 31, 28, 31, 30, 31, 30, 31, 31, 30,
          31, 30, 31));
    }

    int continuousPoint = 0;
    if (date.getDayOfWeek() == DayOfWeek.SATURDAY && continuousDay >= WEEK_ATTENDANCE) {
      continuousPoint += WEEK_ATTENDANCE_POINT;
    }
    if (date.getDayOfMonth() == DAY_OF_MONTH.get(date.getMonthValue())
        && continuousDay >= DAY_OF_MONTH.get(date.getMonthValue())) {
      continuousPoint += MONTH_ATTENDANCE_POINT;
    }
    if (date.getMonthValue() == 12 && date.getDayOfMonth() == 31
        && continuousDay >= DAY_OF_MONTH.stream().mapToInt(Integer::intValue).sum()) {
      continuousPoint += YEAR_ATTENDANCE_POINT;
    }

    return continuousPoint;
  }

  private int getMyTodayRank(LocalDateTime now, MemberEntity memberEntity) {
    List<AttendanceEntity> attendanceEntitiesByDate = attendanceRepository
        .findAllByTimeBetweenAndMemberNotLike(now.toLocalDate().atStartOfDay(), now, memberEntity);
    return attendanceEntitiesByDate.size() + 1;
  }

  public void updateGreeting(AttendanceDto attendanceDto) {
    AttendanceEntity attendanceEntity = getMostRecentlyAttendance();

    if (!isToday(Timestamp.valueOf(attendanceEntity.getTime()))) {
      throw new CustomAttendanceException("출석을 하지 않았습니다.");
    }
    String greeting = attendanceDto.getGreetings();
    if (greeting.isEmpty()) {
      throw new CustomAttendanceException("출석 메시지가 비어있습니다.");
    }
    attendanceEntity.setGreetings(greeting);
    attendanceRepository.save(attendanceEntity);
  }

  public List<String> getMyAttendanceDateList(LocalDate startDate, LocalDate endDate) {
    List<AttendanceEntity> attendanceEntities = getAttendanceEntitiesInPeriodWithMemberId(
        startDate.atStartOfDay(), endDate.atStartOfDay());

    List<String> myAttendanceDateList = new ArrayList<>();

    for (AttendanceEntity attendance : attendanceEntities) {
      myAttendanceDateList.add(attendance.getTime()
          .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA)));
    }
    return myAttendanceDateList;
  }

  public AttendanceResultDto getMyAttendanceWithDate(LocalDate date) {

    if (date == null) {
      throw new CustomAttendanceException("date를 입력하지 않았습니다.");
    }
    MemberEntity member = getMemberEntityWithJWT();

    LocalDateTime startDate = date.atStartOfDay();
    LocalDateTime endDate = date.plusDays(1).atStartOfDay();

    List<AttendanceEntity> attendanceEntities = attendanceRepository.findByMemberAndTimeBetween(
        member, startDate, endDate);

    checkTodayAttend(attendanceEntities);

    return getAttendanceResultDto(attendanceEntities.get(0));
  }

  private void checkTodayAttend(List<AttendanceEntity> attendanceEntities) {
    if (attendanceEntities.size() == 0) {
      throw new CustomAttendanceException("해당 날짜에 출석하지 않았습니다.");
    }
  }

  public List<AttendanceResultDto> getAllAttendance(LocalDate date) {
    if (date == null) {
      throw new CustomAttendanceException("date를 입력하지 않았습니다.");
    }
    LocalDateTime startDate = date.atStartOfDay();
    LocalDateTime endDate = date.plusDays(1).atStartOfDay();

    List<AttendanceEntity> attendanceEntities = attendanceRepository.findAllByTimeBetween(
        startDate, endDate);

    return getAttendanceResultDtoList(attendanceEntities);
  }

  private AttendanceResultDto getAttendanceResultDto(AttendanceEntity attendanceEntity) {
    AttendanceResultDto attendanceResultDto = new AttendanceResultDto();
    attendanceResultDto.initWithEntity(attendanceEntity);
    return attendanceResultDto;
  }

  private List<AttendanceResultDto> getAttendanceResultDtoList(
      List<AttendanceEntity> attendanceEntities) {
    List<AttendanceResultDto> attendanceResultDtoList = new ArrayList<>();
    for (AttendanceEntity attendanceEntity : attendanceEntities) {
      attendanceResultDtoList.add(getAttendanceResultDto(attendanceEntity));
    }
    return attendanceResultDtoList;
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
      LocalDateTime startDate, LocalDateTime endDate) {
    startDate = startDate == null ? LocalDate.EPOCH.atStartOfDay() : startDate;
    endDate = endDate == null ? LocalDate.now().plusDays(1).atStartOfDay() : endDate;
    System.out.println(startDate.toString() + ' ' + endDate);
    if (startDate.isAfter(endDate)) {
      throw new CustomAttendanceException("시작 날짜와 종료 날짜를 잘못 입력하였습니다.");
    }
    MemberEntity member = getMemberEntityWithJWT();

    return attendanceRepository.findByMemberAndTimeBetween(
        member, startDate, endDate);
  }

  private AttendanceEntity getMostRecentlyAttendance() {
    MemberEntity memberEntity = getMemberEntityWithJWT();
    Optional<AttendanceEntity> attendanceEntity = attendanceRepository
        .findTopByMemberOrderByIdDesc(memberEntity);

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

  private int getContinuousDay(Date now) {
    AttendanceEntity recentAttendanceEntity = getMostRecentlyAttendance();
    if (recentAttendanceEntity == null) {
      return 1;
    }

    int continuousDay = 1;
    if (isBeforeDay(Timestamp.valueOf(recentAttendanceEntity.getTime()), now)) {
      continuousDay = recentAttendanceEntity.getContinuousDay() + 1;
    }
    return continuousDay;
  }

  private boolean isAlreadyAttendance() {
    AttendanceEntity recentAttendanceEntity = getMostRecentlyAttendance();
    if (recentAttendanceEntity == null) {
      return false;
    }

    return isToday(Timestamp.valueOf(recentAttendanceEntity.getTime()));
  }
}
