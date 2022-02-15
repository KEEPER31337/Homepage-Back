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
import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.dto.attendance.AttendanceForListDto;
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
    // db변경되기전 미리 살짝 바꿔놓음
    int rankPoint = 0;
    if (rank == 1) {
      rankPoint += FIRST_PLACE_POINT;
    } else if (rank == 2) {
      rankPoint += SECOND_PLACE_POINT;
    } else if (rank == 3) {
      rankPoint += THIRD_PLACE_POINT;
    }

    int continuousDay = getContinousDay(now);
    int continuousPoint = 0;
    if (continuousDay == WEEK_ATTENDANCE) {
      continuousPoint += WEEK_ATTENDANCE_POINT;
    } else if (continuousDay == MONTH_ATTENDANCE) {
      continuousPoint += MONTH_ATTENDANCE_POINT;
    } else if (continuousDay == YEAR_ATTENDANCE) {
      continuousPoint += YEAR_ATTENDANCE_POINT;
    }

    int randomPoint = (int) (Math.random() * 900 + 100);
    point = rankPoint + continuousPoint + DAILY_ATTENDANCE_POINT + randomPoint;

    MemberEntity memberEntity = getMemberEntityWithJWT();
    String greeting = attendanceDto.getGreetings();
    if (greeting == "" || greeting == null) {
      greeting = DEFAULT_GREETINGS;
    }
    attendanceRepository.save(
        AttendanceEntity.builder()
            .point(point)
            .continousDay(continuousDay)
            .greetings(greeting)
            .ipAddress(attendanceDto.getIpAddress())
            .time(now)
            .member(memberEntity)
            .randomPoint(randomPoint)
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

  public List<AttendanceForListDto> getAllAttendance(LocalDate date) {
    if (date == null) {
      throw new CustomAttendanceException("date를 입력하지 않았습니다.");
    }
    LocalDate startDate = date.atStartOfDay().toLocalDate();
    LocalDate endDate = date.plusDays(1).atStartOfDay().toLocalDate();

    List<AttendanceEntity> attendanceEntities = attendanceRepository.findAllByTimeBetween(
        java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));

    return makeEntityToDto(attendanceEntities);
  }

  public List<AttendanceForListDto> makeEntityToDto(List<AttendanceEntity> attendanceEntities) {

    List<AttendanceForListDto> returnList = new ArrayList<>();
    for (AttendanceEntity attendanceEntity : attendanceEntities) {
      returnList.add(new AttendanceForListDto(hidingIpAddress(attendanceEntity.getIpAddress()),
          attendanceEntity.getMember().getNickName(), attendanceEntity.getMember().getThumbnail(),
          attendanceEntity.getGreetings(), attendanceEntity.getContinousDay(),
          attendanceEntity.getRank()));
    }
    return returnList;
  }

  public String hidingIpAddress(String ipAddress) {
    String[] splits = ipAddress.split("\\.");
    splits[0] = "*";
    splits[1] = "*";

    return String.join(".", splits);
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

    return attendanceRepository.findByMemberAndTimeBetween(
        member, java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
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

  private AttendanceEntity getMyAttendanceWithDate(LocalDate date) {

    if (date == null) {
      throw new CustomAttendanceException("date를 입력하지 않았습니다.");
    }
    MemberEntity member = getMemberEntityWithJWT();

    LocalDate startDate = date.atStartOfDay().toLocalDate();
    LocalDate endDate = date.plusDays(1).atStartOfDay().toLocalDate();

    List<AttendanceEntity> attendanceEntities = attendanceRepository.findByMemberAndTimeBetween(
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

    int continousDay = 1;
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
