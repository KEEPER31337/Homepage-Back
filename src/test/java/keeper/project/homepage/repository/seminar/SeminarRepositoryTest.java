package keeper.project.homepage.repository.seminar;

import keeper.project.homepage.entity.seminar.SeminarEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SeminarRepositoryTest extends SeminarRepositoryTestHelper {

  @Test
  @DisplayName("name 으로 seminarEntity 조회")
  void findByNameTest() {
    // given
    SeminarEntity seminarEntity = seminarRepository.getById(1L);

    // when
    SeminarEntity findSeminarEntity = seminarRepository.findByName(seminarEntity.getName());

    // then
    Assertions.assertThat(findSeminarEntity).isEqualTo(seminarEntity);
  }

}