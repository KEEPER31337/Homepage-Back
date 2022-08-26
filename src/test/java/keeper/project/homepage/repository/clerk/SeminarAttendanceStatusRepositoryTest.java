package keeper.project.homepage.repository.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.PERSONAL;

import java.util.List;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
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
    Assertions.assertThat(seminarAttendanceStatusEntities.size()).isEqualTo(seminarAttendanceStatus.values().length);
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

    // then
    Assertions.assertThat(attendance.getType()).isEqualTo(ATTENDANCE.getType());
    Assertions.assertThat(lateness.getType()).isEqualTo(LATENESS.getType());
    Assertions.assertThat(absence.getType()).isEqualTo(ABSENCE.getType());
    Assertions.assertThat(personal.getType()).isEqualTo(PERSONAL.getType());
  }
}
