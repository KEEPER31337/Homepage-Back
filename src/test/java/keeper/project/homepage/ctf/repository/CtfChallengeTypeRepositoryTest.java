package keeper.project.homepage.ctf.repository;

import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.DYNAMIC;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.STANDARD;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType;
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

  @Test
  @DisplayName("타입 record와 Enum 일치 여부 확인 테스트")
  void typeEnumMatch() {

    // given
    // 1L: Misc, 2L: System, 3L: Reversing, 4L: Forensic, 5L: Web, 6L: Crypto 가 DB에 들어있다고 가정.
    // category 수정 시 변경 필요

    // when
    List<CtfChallengeTypeEntity> ctfTypeEntities = ctfChallengeTypeRepository.findAll();

    // then
    Assertions.assertThat(ctfTypeEntities.size()).isEqualTo(CtfChallengeType.values().length);

    var standard = ctfChallengeTypeRepository.getById(STANDARD.getId());
    var dynamic = ctfChallengeTypeRepository.getById(DYNAMIC.getId());

    // then
    Assertions.assertThat(standard.getName()).isEqualTo(STANDARD.getName());
    Assertions.assertThat(dynamic.getName()).isEqualTo(DYNAMIC.getName());
  }
}
