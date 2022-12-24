package keeper.project.homepage.attendance.service;


import static keeper.project.homepage.about.dto.AttendancePointDto.DAILY_ATTENDANCE_POINT;
import static keeper.project.homepage.about.dto.AttendancePointDto.FIRST_PLACE_POINT;
import static keeper.project.homepage.about.dto.AttendancePointDto.MONTH_ATTENDANCE_POINT;
import static keeper.project.homepage.about.dto.AttendancePointDto.SECOND_PLACE_POINT;
import static keeper.project.homepage.about.dto.AttendancePointDto.THIRD_PLACE_POINT;
import static keeper.project.homepage.about.dto.AttendancePointDto.WEEK_ATTENDANCE;
import static keeper.project.homepage.about.dto.AttendancePointDto.WEEK_ATTENDANCE_POINT;
import static keeper.project.homepage.about.dto.AttendancePointDto.YEAR_ATTENDANCE_POINT;
import static keeper.project.homepage.attendance.service.DateUtils.isBeforeDay;
import static keeper.project.homepage.attendance.service.DateUtils.isToday;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder.In;
import keeper.project.homepage.point.dto.request.PointLogRequestDto;
import keeper.project.homepage.about.dto.AttendanceDto;
import keeper.project.homepage.about.dto.AttendancePointDto;
import keeper.project.homepage.about.dto.AttendanceResultDto;
import keeper.project.homepage.attendance.entity.AttendanceEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.attendance.exception.CustomAttendanceException;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.attendance.repository.AttendanceRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.util.redis.RedisUtil;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.point.service.PointLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final MemberRepository memberRepository;
  private final AuthService authService;
  private final PointLogService pointLogService;
  private final RedisUtil redisUtil;

  private static final long rankDataExpireDuration = 60 * 60 * 24; // 60s * 60m * 24h로 하루를 의미함.
  private static final int MIN_POINT = 100;
  private static final int MAX_POINT = 1000;
  private static final String DEFAULT_GREETINGS = "자동 출석입니다.";
  private static final List<Integer> LEAP_YEAR_DAY_OF_MONTH = List.of(0, 31, 29, 31, 30, 31, 30,
      31, 31, 30, 31, 30, 31);
  private static final List<Integer> NON_LEAP_YEAR_DAY_OF_MONTH = List.of(0, 31, 28, 31, 30, 31, 30,
      31, 31, 30, 31, 30, 31);

  @Transactional
  public void save(AttendanceDto attendanceDto) {

    if (isAlreadyAttendance()) {
      throw new CustomAttendanceException("이미 출석을 완료했습니다.");
    }
    MemberEntity memberEntity = getMemberEntityWithJWT();

    int point = saveAttendance(attendanceDto, memberEntity);
    createPointSaveLog(point, memberEntity);
  }

  @Transactional
  int saveAttendance(AttendanceDto attendanceDto, MemberEntity memberEntity) {
    LocalDateTime now = LocalDateTime.now();

    String greeting = attendanceDto.getGreetings();
    if (greeting == "" || greeting == null) {
      greeting = DEFAULT_GREETINGS;
    }

    long rank = getMyTodayRank(now, memberEntity);
    int rankPoint = 0;
    if (rank == 1) {
      rankPoint += FIRST_PLACE_POINT;
    } else if (rank == 2) {
      rankPoint += SECOND_PLACE_POINT;
    } else if (rank == 3) {
      rankPoint += THIRD_PLACE_POINT;
    }

    int continuousDay = getContinuousDay();
    int continuousPoint = getContinuousPoint(continuousDay, now);
    int randomPoint = getRandomPointBetween(MIN_POINT, MAX_POINT);
    int totalPoint = continuousPoint + DAILY_ATTENDANCE_POINT + randomPoint + rankPoint;
    attendanceRepository.save(AttendanceEntity.builder()
        .point(totalPoint)
        .rankPoint(0)
        .continuousPoint(continuousPoint)
        .continuousDay(continuousDay)
        .greetings(greeting)
        .ipAddress(attendanceDto.getIpAddress())
        .time(now)
        .date(now.toLocalDate())
        .member(memberEntity)
        .randomPoint(randomPoint)
        .rankPoint(rankPoint)
        .rank(rank)
        .build());
    return totalPoint;
  }

  private void createPointSaveLog(int point, MemberEntity memberEntity) {
    pointLogService.createPointSaveLog(memberEntity,
        new PointLogRequestDto(LocalDateTime.now(), point, "출석 포인트"));
  }

  private int getRandomPointBetween(int min, int max) {
    if (max < min) {
      throw new IllegalArgumentException("min값인 " + min + "은 max값인 " + max + "보다 클 수 없습니다.");
    }
    return (int) (Math.random() * (max - min) + min);
  }

  private int getContinuousPoint(int continuousDay, LocalDateTime now) {
    List<Integer> dayOfMonth;
    if (isLeapYear(now.getYear())) {
      dayOfMonth = LEAP_YEAR_DAY_OF_MONTH;
    } else {
      dayOfMonth = NON_LEAP_YEAR_DAY_OF_MONTH;
    }

    int continuousPoint = 0;
    if (isWeekPerfectAttendance(continuousDay, now)) {
      continuousPoint += WEEK_ATTENDANCE_POINT;
    }
    if (isMonthPerfectAttendance(continuousDay, now, dayOfMonth)) {
      continuousPoint += MONTH_ATTENDANCE_POINT;
    }
    if (isYearPerfectAttendance(continuousDay, now, dayOfMonth)) {
      continuousPoint += YEAR_ATTENDANCE_POINT;
    }

    return continuousPoint;
  }

  private boolean isWeekPerfectAttendance(int continuousDay, LocalDateTime now) {
    return now.getDayOfWeek() == DayOfWeek.SATURDAY && continuousDay >= WEEK_ATTENDANCE;
  }

  private boolean isMonthPerfectAttendance(int continuousDay, LocalDateTime now,
      List<Integer> dayOfMonth) {
    return now.getDayOfMonth() == dayOfMonth.get(now.getMonthValue())
        && continuousDay >= dayOfMonth.get(now.getMonthValue());
  }

  private boolean isYearPerfectAttendance(int continuousDay, LocalDateTime now,
      List<Integer> dayOfMonth) {
    return now.getMonthValue() == 12 && now.getDayOfMonth() == 31
        && continuousDay >= dayOfMonth.stream().mapToInt(Integer::intValue).sum();
  }

  private static boolean isLeapYear(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
  }

  private long getMyTodayRank(LocalDateTime now, MemberEntity memberEntity) {
    String todayKey = "attendance:" + now.toLocalDate().toString();
    return redisUtil.increaseAndGetWithExpire(todayKey, rankDataExpireDuration);
  }

  @Transactional
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
      throw new CustomMemberNotFoundException(memberId);
    }
    return member.get();
  }

  private int getContinuousDay() {
    AttendanceEntity recentAttendanceEntity = getMostRecentlyAttendance();
    if (recentAttendanceEntity == null) {
      return 1;
    }

    int continuousDay = 1;
    if (isConsecutiveAttendance(recentAttendanceEntity.getTime())) {
      continuousDay = recentAttendanceEntity.getContinuousDay() + 1;
    }
    return continuousDay;
  }

  private static boolean isConsecutiveAttendance(LocalDateTime recentAttendanceTime) {
    return recentAttendanceTime.plusDays(1).getDayOfMonth() == LocalDate.now()
        .getDayOfMonth();
  }

  private boolean isAlreadyAttendance() {
    AttendanceEntity recentAttendanceEntity = getMostRecentlyAttendance();
    if (recentAttendanceEntity == null) {
      return false;
    }

    return isToday(Timestamp.valueOf(recentAttendanceEntity.getTime()));
  }
}
