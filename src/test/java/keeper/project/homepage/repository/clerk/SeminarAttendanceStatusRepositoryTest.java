package keeper.project.homepage.repository.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.PERSONAL;

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
    Assertions.assertThat(seminarAttendanceStatusEntities.size()).isEqualTo(4);
  }


  @Test
  @DisplayName("세미나 출석 타입 테스트")
  void SeminarAttendanceStatusTypeTest() {
    // given

    // when
    SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.getById(1L);
    SeminarAttendanceStatusEntity lateness = seminarAttendanceStatusRepository.getById(2L);
    SeminarAttendanceStatusEntity absence = seminarAttendanceStatusRepository.getById(3L);
    SeminarAttendanceStatusEntity personal = seminarAttendanceStatusRepository.getById(4L);

    // then
    Assertions.assertThat(attendance.getType()).isEqualTo(ATTENDANCE.getType());
    Assertions.assertThat(lateness.getType()).isEqualTo(LATENESS.getType());
    Assertions.assertThat(absence.getType()).isEqualTo(ABSENCE.getType());
    Assertions.assertThat(personal.getType()).isEqualTo(PERSONAL.getType());
  }
}
