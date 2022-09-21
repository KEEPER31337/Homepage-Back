package keeper.project.homepage.util.service;

import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import keeper.project.homepage.clerk.entity.SeminarAttendanceEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity;
import keeper.project.homepage.clerk.entity.SeminarEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.clerk.repository.SeminarAttendanceRepository;
import keeper.project.homepage.clerk.repository.SeminarAttendanceStatusRepository;
import keeper.project.homepage.clerk.repository.SeminarRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
public class SchedulerServiceTest {

  @Autowired
  private SchedulerService schedulerService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private SeminarRepository seminarRepository;

  @Autowired
  private SeminarAttendanceRepository seminarAttendanceRepository;

  @Autowired
  private SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;

  private MemberEntity member;
  private SeminarEntity seminar;
  private SeminarAttendanceEntity seminarAttendance;
  private SeminarAttendanceStatusEntity beforeAttendance;
  private SeminarAttendanceStatusEntity absence;

  @BeforeEach
  public void setUp() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime attendanceCloseTime = LocalDateTime.now().plusSeconds(1);
    LocalDateTime latenessCloseTime = LocalDateTime.now().plusSeconds(1);
    String code = "1234";
    member = memberRepository.getById(1L);
    beforeAttendance = seminarAttendanceStatusRepository.getById(
        BEFORE_ATTENDANCE.getId());
    absence = seminarAttendanceStatusRepository.getById(
        ABSENCE.getId());
    seminar = seminarRepository.save(SeminarEntity.builder()
        .name(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
        .openTime(now)
        .attendanceCloseTime(attendanceCloseTime)
        .latenessCloseTime(latenessCloseTime)
        .attendanceCode(code)
        .build()
    );
    seminarAttendance = seminarAttendanceRepository.save(
        SeminarAttendanceEntity.builder()
            .memberEntity(member)
            .seminarEntity(seminar)
            .seminarAttendanceStatusEntity(beforeAttendance)
            .seminarAttendTime(LocalDateTime.now())
            .build()
    );
  }

  @Test
  @DisplayName("스케줄 작업 테스트")
  public void testScheduledTask() throws Exception {
    Date date = Date.from(
        seminar.getLatenessCloseTime().plusSeconds(1).atZone(ZoneId.of("Asia/Seoul")).toInstant());

    log.info(seminarAttendance.getSeminarAttendanceStatusEntity().getType());

    Runnable task = () -> {
      seminarAttendance.setSeminarAttendanceStatusEntity(absence);
    };
    schedulerService.scheduleTask(task, date);

    Thread.sleep(3000);

    SeminarAttendanceEntity result = seminarAttendanceRepository.getById(seminarAttendance.getId());

    log.info(seminarAttendance.getSeminarAttendanceStatusEntity().getType());

    Assertions.assertThat(result.getSeminarAttendanceStatusEntity().getType()).isEqualTo(absence.getType());
  }

}
