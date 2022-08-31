package keeper.project.homepage.controller.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.LATENESS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.clerk.SeminarAttendanceExcuseRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceStatusRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AutoAttendanceSpringTestHelper extends ApiControllerTestHelper {

  private static final Integer ABSENCE_DEMERIT = 3;
  private static final Integer NO_DEMERIT = 0;
  @Autowired
  protected SeminarRepository seminarRepository;
  @Autowired
  protected SeminarAttendanceExcuseRepository seminarAttendanceExcuseRepository;
  @Autowired
  protected SeminarAttendanceRepository seminarAttendanceRepository;
  @Autowired
  protected SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected void generateSeminarAttendance(SeminarEntity seminar, MemberEntity member) {
    seminarAttendanceRepository.save(
        SeminarAttendanceEntity.builder()
            .seminarEntity(seminar)
            .memberEntity(member)
            .seminarAttendanceStatusEntity(seminarAttendanceStatusRepository.getById(
                BEFORE_ATTENDANCE.getId())) // 출석 전 상태로 저장 되어있음.
            .seminarAttendTime(LocalDateTime.now().withNano(0))
            .build());

  }

  protected SeminarAttendanceEntity generateSeminarAttendanceWithAttendTime(SeminarEntity seminar,
      MemberEntity member, int minutes) {
    return seminarAttendanceRepository.save(
        SeminarAttendanceEntity.builder()
            .seminarEntity(seminar)
            .memberEntity(member)
            .seminarAttendTime(LocalDateTime.now().plusMinutes(minutes))
            .seminarAttendanceStatusEntity(seminarAttendanceStatusRepository.getById(
                BEFORE_ATTENDANCE.getId()))
            .build()
    );
  }

  protected void processingAttendance(MemberEntity member, SeminarEntity seminar) {
    SeminarAttendanceEntity seminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow();
    seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
        seminarAttendanceStatusRepository.getById(ATTENDANCE.getId()));
  }

  protected void processingAbsence(List<SeminarAttendanceEntity> seminarAttendanceEntities) {
    for (SeminarAttendanceEntity seminarAttendanceEntity : seminarAttendanceEntities) {
      seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
          seminarAttendanceStatusRepository.getById(ABSENCE.getId()));
      MemberEntity member = seminarAttendanceEntity.getMemberEntity();
      member.changeDemerit(member.getDemerit() + ABSENCE_DEMERIT);
    }
  }

  protected Integer judgeAttendanceStatus(SeminarEntity seminar,
      SeminarAttendanceEntity seminarAttendanceEntity, LocalDateTime attendTime) {

    MemberEntity member = seminarAttendanceEntity.getMemberEntity();

    int demerit = NO_DEMERIT;

    if (seminar.getAttendanceCloseTime().isAfter(attendTime)) { // 출석
      seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
          seminarAttendanceStatusRepository.getById(ATTENDANCE.getId()));
    } else if (seminar.getLatenessCloseTime().isAfter(attendTime)) { // 지각
      // 지각 짝수 회는 결석에 해당하는 벌점 부여
      if (getLatenessCount(member) % 2 == 1) {
        member.changeDemerit(member.getDemerit() + ABSENCE_DEMERIT);
        demerit = ABSENCE_DEMERIT;
      }
      seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
          seminarAttendanceStatusRepository.getById(LATENESS.getId()));
    } else if (seminar.getLatenessCloseTime().isBefore(attendTime)) { // 결석
      seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
          seminarAttendanceStatusRepository.getById(ABSENCE.getId()));
      member.changeDemerit(member.getDemerit() + ABSENCE_DEMERIT);
      demerit = ABSENCE_DEMERIT;
    }
    return demerit;
  }

  protected long getLatenessCount(MemberEntity member) {
    return member.getSeminarAttendances().stream()
        .filter(seminarAttendance ->
            seminarAttendance.getSeminarAttendanceStatusEntity().getType()
                .equals(LATENESS.getType())
        ).count();
  }


}
