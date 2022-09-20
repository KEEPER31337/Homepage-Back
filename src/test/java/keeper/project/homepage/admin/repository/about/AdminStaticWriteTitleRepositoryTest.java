package keeper.project.homepage.admin.repository.about;

import static org.assertj.core.api.Assertions.*;

import keeper.project.homepage.about.entity.StaticWriteTitleEntity;
import keeper.project.homepage.repository.about.StaticWriteTitleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminStaticWriteTitleRepositoryTest {

  @Autowired
  private StaticWriteTitleRepository staticWriteTitleRepository;

  @Test
  @DisplayName("페이지 블럭 타이틀 수정")
  void updateTitle() {
    // given
    String type = "intro";
    StaticWriteTitleEntity staticWriteTitle = StaticWriteTitleEntity.builder()
        .title("키퍼(KEEPER) 소개글")
        .type(type)
        .build();
    String newTitle = "KEEPER 소개글";
    staticWriteTitle.updateTitle(newTitle);

    // when
    StaticWriteTitleEntity updateStaticWriteTitle = staticWriteTitleRepository.save(
        staticWriteTitle);

    // then
    assertThat(updateStaticWriteTitle.getId()).isEqualTo(staticWriteTitle.getId());
    assertThat(updateStaticWriteTitle.getTitle()).isEqualTo(newTitle);
    assertThat(updateStaticWriteTitle.getType()).isEqualTo(type);
  }


}