package keeper.project.homepage.clerk.repository;

import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.BEFORE_ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.LATENESS;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.PERSONAL;

import java.util.List;
import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SeminarAttendanceStatusRepositoryTest extends SeminarRepositoryTestHelper {

  @Test
  @DisplayName("세미나 출석 타입 개수 테스트")
  void SeminarAttendanceStatusTest() {
    // given

    // when
    List<SeminarAttendanceStatusEntity> seminarAttendanceStatusEntities = seminarAttendanceStatusRepository.findAll();

    // then
    Assertions.assertThat(seminarAttendanceStatusEntities.size()).isEqualTo(SeminarAttendanceStatus.values().length);
  }


  @Test
  @DisplayName("세미나 출석 타입 테스트")
  void SeminarAttendanceStatusTypeTest() {
    // given

    // when
    SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.getById(
        ATTENDANCE.getId());
    SeminarAttendanceStatusEntity lateness = seminarAttendanceStatusRepository.getById(
        LATENESS.getId());
    SeminarAttendanceStatusEntity absence = seminarAttendanceStatusRepository.getById(
        ABSENCE.getId());
    SeminarAttendanceStatusEntity personal = seminarAttendanceStatusRepository.getById(
        PERSONAL.getId());
    SeminarAttendanceStatusEntity beforeAttendance = seminarAttendanceStatusRepository.getById(
        BEFORE_ATTENDANCE.getId());

    // then
    Assertions.assertThat(attendance.getType()).isEqualTo(ATTENDANCE.getType());
    Assertions.assertThat(lateness.getType()).isEqualTo(LATENESS.getType());
    Assertions.assertThat(absence.getType()).isEqualTo(ABSENCE.getType());
    Assertions.assertThat(personal.getType()).isEqualTo(PERSONAL.getType());
    Assertions.assertThat(beforeAttendance.getType()).isEqualTo(BEFORE_ATTENDANCE.getType());

  }
}
