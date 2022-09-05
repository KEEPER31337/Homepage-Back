package keeper.project.homepage.util.service;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceStatusRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
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

  @Test
  @DisplayName("스케쥴 작업 테스트")
  public void testScheduledTask() throws Exception {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime attendanceCloseTime = LocalDateTime.now().plusSeconds(1);
    LocalDateTime latenessCloseTime = LocalDateTime.now().plusSeconds(1);
    String code = "1234";
    MemberEntity member = memberRepository.getById(1L);
    SeminarAttendanceStatusEntity beforeAttendance = seminarAttendanceStatusRepository.getById(
        BEFORE_ATTENDANCE.getId());
    SeminarAttendanceStatusEntity absence = seminarAttendanceStatusRepository.getById(
        ABSENCE.getId());
    SeminarEntity seminar = seminarRepository.save(SeminarEntity.builder()
        .name(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
        .openTime(now)
        .attendanceCloseTime(attendanceCloseTime)
        .latenessCloseTime(latenessCloseTime)
        .attendanceCode(code)
        .build()
    );
    SeminarAttendanceEntity seminarAttendance = seminarAttendanceRepository.save(
        SeminarAttendanceEntity.builder()
            .memberEntity(member)
            .seminarEntity(seminar)
            .seminarAttendanceStatusEntity(beforeAttendance)
            .seminarAttendTime(LocalDateTime.now())
            .build()
    );

    List<SeminarAttendanceEntity> notAttendances = seminarAttendanceRepository.findAllBySeminarEntityAndSeminarAttendanceStatusEntity(
        seminar, beforeAttendance);

    Date date = Date.from(
        seminar.getLatenessCloseTime().plusSeconds(1).atZone(ZoneId.of("Asia/Seoul")).toInstant());
    Runnable task = () -> {
      notAttendances.forEach(
          attendance -> attendance.setSeminarAttendanceStatusEntity(absence)
      );
    };
    schedulerService.scheduleTask(task, date);

    Thread.sleep(3000);

    SeminarAttendanceEntity after = seminarAttendanceRepository.getById(seminarAttendance.getId());

    assertThat(after.getSeminarAttendanceStatusEntity().getType()).isEqualTo(ABSENCE.getType());
  }

}
