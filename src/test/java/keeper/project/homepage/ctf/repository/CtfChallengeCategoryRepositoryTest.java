package keeper.project.homepage.ctf.repository;

import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.CRYPTO;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.FORENSIC;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.MISC;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.OSINT;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.REVERSING;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.SYSTEM;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.WEB;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory;
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

  @Test
  @DisplayName("카테고리 record와 Enum 일치 여부 확인 테스트")
  void categoryEnumMatch() {

    // given
    // 1L: Misc, 2L: System, 3L: Reversing, 4L: Forensic, 5L: Web, 6L: Crypto 가 DB에 들어있다고 가정.
    // category 수정 시 변경 필요

    // when
    List<CtfChallengeCategoryEntity> ctfCategoryEntities = ctfChallengeCategoryRepository.findAll();

    System.out.println(ctfCategoryEntities.stream().map(CtfChallengeCategoryEntity::getName).toList());
    // then
    Assertions.assertThat(ctfCategoryEntities.size())
        .isEqualTo(CtfChallengeCategory.values().length);

    var misc = ctfChallengeCategoryRepository.getById(MISC.getId());
    var system = ctfChallengeCategoryRepository.getById(SYSTEM.getId());
    var reversing = ctfChallengeCategoryRepository.getById(REVERSING.getId());
    var forensic = ctfChallengeCategoryRepository.getById(FORENSIC.getId());
    var web = ctfChallengeCategoryRepository.getById(WEB.getId());
    var crypto = ctfChallengeCategoryRepository.getById(CRYPTO.getId());
    var osint = ctfChallengeCategoryRepository.getById(OSINT.getId());

    // then
    Assertions.assertThat(misc.getName()).isEqualTo(MISC.getName());
    Assertions.assertThat(system.getName()).isEqualTo(SYSTEM.getName());
    Assertions.assertThat(reversing.getName()).isEqualTo(REVERSING.getName());
    Assertions.assertThat(forensic.getName()).isEqualTo(FORENSIC.getName());
    Assertions.assertThat(web.getName()).isEqualTo(WEB.getName());
    Assertions.assertThat(crypto.getName()).isEqualTo(CRYPTO.getName());
    Assertions.assertThat(osint.getName()).isEqualTo(OSINT.getName());
  }
}
