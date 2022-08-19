package keeper.project.homepage.repository.clerk;

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
    SeminarAttendanceStatusEntity ATTENDANCE = seminarAttendanceStatusRepository.getById(1L);
    SeminarAttendanceStatusEntity LATENESS = seminarAttendanceStatusRepository.getById(2L);
    SeminarAttendanceStatusEntity ABSENCE = seminarAttendanceStatusRepository.getById(3L);
    SeminarAttendanceStatusEntity PERSONAL = seminarAttendanceStatusRepository.getById(4L);

    // then
    Assertions.assertThat(ATTENDANCE.getType()).isEqualTo("출석");
    Assertions.assertThat(LATENESS.getType()).isEqualTo("지각");
    Assertions.assertThat(ABSENCE.getType()).isEqualTo("결석");
    Assertions.assertThat(PERSONAL.getType()).isEqualTo("개인사정");
  }
}
