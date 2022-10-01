package keeper.project.homepage.ctf.repository;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CtfChallengeTypeRepositoryTest extends CtfTestHelper {

  @Test
  @DisplayName("저장된 타입이 조회되는지 확인")
  void viewType() {
    // given
    // 1L: STANDARD, 2L: DYNAMIC 가 DB에 들어있다고 가정.
    // type 수정 시 변경 필요

    // when
    List<CtfChallengeTypeEntity> ctfChallengeTypeEntities = new ArrayList<>();
    ctfChallengeTypeEntities.add(ctfChallengeTypeRepository.getById(1L));
    ctfChallengeTypeEntities.add(ctfChallengeTypeRepository.getById(2L));

    // then
    Assertions.assertThat(ctfChallengeTypeEntities.size()).isEqualTo(2);
    Assertions.assertThat(ctfChallengeTypeEntities.get(0).getName()).isEqualTo("STANDARD");
    Assertions.assertThat(ctfChallengeTypeEntities.get(1).getName()).isEqualTo("DYNAMIC");
  }
}
