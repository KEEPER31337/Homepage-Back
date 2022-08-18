package keeper.project.homepage.repository.clerk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import keeper.project.homepage.entity.clerk.SeminarEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.InvalidDataAccessApiUsageException;

class SeminarRepositoryTest extends SeminarRepositoryTestHelper {

  @Test
  @DisplayName("name 으로 seminarEntity 조회")
  void findByNameTest() {
    // given
    SeminarEntity seminarEntity = seminarRepository.getById(1L);

    // when
    SeminarEntity findSeminarEntity = seminarRepository.findByNameIsLike(seminarEntity.getName());

    // then
    assertThat(findSeminarEntity).isEqualTo(seminarEntity);
  }

  @Test
  @DisplayName("name 이 null 일 때 seminarEntity 조회시 예외 발생 테스트")
  void findByNameIsNullTest() {
    // given

    // when
    InvalidDataAccessApiUsageException invalidDataAccessApiUsageException = assertThrows(
        InvalidDataAccessApiUsageException.class,
        () -> seminarRepository.findByNameIsLike(null));

    // then
    assertThat(invalidDataAccessApiUsageException.getClass()).isEqualTo(
        InvalidDataAccessApiUsageException.class);
  }

}