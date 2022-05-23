package keeper.project.homepage.user.repostiory.about;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.entity.about.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;
import keeper.project.homepage.repository.about.StaticWriteSubtitleImageRepository;
import keeper.project.homepage.repository.about.StaticWriteTitleRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Log4j2
public class StaticWriteTitleRepositoryTest {

  @Autowired
  private StaticWriteTitleRepository staticWriteTitleRepository;

  @Autowired
  private StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;

  @Autowired
  private EntityManager entityManager;

  @Test
  @DisplayName("연결된 이미지와 함께 페이지 블럭 타이틀 조회")
  void getTitleWithImage() {
    // given
    StaticWriteTitleEntity staticWriteTitle = StaticWriteTitleEntity.builder()
        .title("키퍼(KEEPER) 소개글")
        .type("test")
        .build();

    StaticWriteTitleEntity savedStaticWriteTitle = staticWriteTitleRepository.save(
        staticWriteTitle);

    StaticWriteSubtitleImageEntity staticWriteSubtitleImage = StaticWriteSubtitleImageEntity.builder()
        .subtitle("동아리?")
        .displayOrder(1)
        .staticWriteTitle(savedStaticWriteTitle)
        .build();

    staticWriteSubtitleImageRepository.save(staticWriteSubtitleImage);

    // when
    // 영속성 컨텍스트에 저장되어 있는 값을 지워버린다
    entityManager.clear();

    List<StaticWriteTitleEntity> staticWriteTitles = staticWriteTitleRepository.findAllByType(
        "test");

    // then
    assertThat(staticWriteTitles.size()).isEqualTo(1);
    assertThat(staticWriteTitles.get(0).getStaticWriteSubtitleImages().size()).isEqualTo(1);
    assertThat(
        staticWriteTitles.get(0).getStaticWriteSubtitleImages().get(0).getSubtitle()).isEqualTo(
        "동아리?");
  }

  @Test
  @DisplayName("타입 종류 리스트 반환")
  void getAllDistinctTypes() {
    // given
    staticWriteTitleRepository.save(StaticWriteTitleEntity.builder()
        .title("테스트")
        .type("test")
        .build());
    staticWriteTitleRepository.save(StaticWriteTitleEntity.builder()
        .title("중복")
        .type("intro")
        .build());
    String basicType1 = "intro";
    String basicType2 = "activity";
    String basicType3 = "excellence";
    String basicType4 = "history";

    // when
    List<String> result = staticWriteTitleRepository.getAllDistinctTypes();

    // then
    assertThat(result).contains("test");
    assertThat(result).contains(basicType1, basicType2, basicType3, basicType4);
    assertThat(Collections.frequency(result, "intro")).isEqualTo(1);
  }
}
