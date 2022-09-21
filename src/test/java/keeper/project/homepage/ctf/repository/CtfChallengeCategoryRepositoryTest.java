package keeper.project.homepage.ctf.repository;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CtfChallengeCategoryRepositoryTest extends CtfTestHelper {


  @Test
  @DisplayName("카테고리 조회 테스트")
  void viewCategory() {

    // given
    // 1L: Misc, 2L: System, 3L: Reversing, 4L: Forensic, 5L: Web, 6L: Crypto 가 DB에 들어있다고 가정.
    // category 수정 시 변경 필요

    // when
    List<CtfChallengeCategoryEntity> ctfChallengeCategoryEntities = new ArrayList<>();
    ctfChallengeCategoryEntities.add(ctfChallengeCategoryRepository.findById(1L).get());
    ctfChallengeCategoryEntities.add(ctfChallengeCategoryRepository.findById(2L).get());
    ctfChallengeCategoryEntities.add(ctfChallengeCategoryRepository.findById(3L).get());
    ctfChallengeCategoryEntities.add(ctfChallengeCategoryRepository.findById(4L).get());
    ctfChallengeCategoryEntities.add(ctfChallengeCategoryRepository.findById(5L).get());
    ctfChallengeCategoryEntities.add(ctfChallengeCategoryRepository.findById(6L).get());

    // then
    Assertions.assertThat(ctfChallengeCategoryEntities.size()).isEqualTo(6);
    Assertions.assertThat(ctfChallengeCategoryEntities.get(0).getName()).isEqualTo("Misc");
    Assertions.assertThat(ctfChallengeCategoryEntities.get(1).getName()).isEqualTo("System");
    Assertions.assertThat(ctfChallengeCategoryEntities.get(2).getName()).isEqualTo("Reversing");
    Assertions.assertThat(ctfChallengeCategoryEntities.get(3).getName()).isEqualTo("Forensic");
    Assertions.assertThat(ctfChallengeCategoryEntities.get(4).getName()).isEqualTo("Web");
    Assertions.assertThat(ctfChallengeCategoryEntities.get(5).getName()).isEqualTo("Crypto");
  }

}
