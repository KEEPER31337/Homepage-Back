package keeper.project.homepage.admin.repository.about;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import keeper.project.homepage.about.dto.request.StaticWriteSubtitleImageDto;
import keeper.project.homepage.about.entity.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.about.entity.StaticWriteTitleEntity;
import keeper.project.homepage.repository.about.StaticWriteSubtitleImageRepository;
import keeper.project.homepage.repository.about.StaticWriteTitleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AdminStaticWriteSubtitleImageRepositoryTest {

  private final StaticWriteTitleRepository staticWriteTitleRepository;
  private final StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;

  @Autowired
  public AdminStaticWriteSubtitleImageRepositoryTest(
      StaticWriteTitleRepository staticWriteTitleRepository,
      StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository) {
    this.staticWriteTitleRepository = staticWriteTitleRepository;
    this.staticWriteSubtitleImageRepository = staticWriteSubtitleImageRepository;
  }

  @Test
  @DisplayName("서브 타이틀 생성")
  void create() {
    // given
    StaticWriteTitleEntity staticWriteTitleEntity = staticWriteTitleRepository.save(
        StaticWriteTitleEntity.builder()
            .title("테스트 타이틀")
            .type("테스트 타입")
            .build());
    StaticWriteSubtitleImageDto staticWriteSubtitleImageDto = StaticWriteSubtitleImageDto.builder()
        .subtitle("2022")
        .displayOrder(1)
        .build();
    StaticWriteSubtitleImageEntity staticWriteSubtitleImage = staticWriteSubtitleImageDto.toEntity(
        staticWriteTitleEntity, null);

    // when
    StaticWriteSubtitleImageEntity result = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImage);

    // then
    assertThat(result.getSubtitle()).isEqualTo("2022");
    assertThat(result.getDisplayOrder()).isEqualTo(1);
  }

  @Test
  @DisplayName("서브 타이틀 수정")
  void update() {
    // given
    StaticWriteTitleEntity staticWriteTitleEntity = staticWriteTitleRepository.save(
        StaticWriteTitleEntity.builder()
            .title("테스트 타이틀")
            .type("테스트 타입")
            .build());
    StaticWriteSubtitleImageDto newInfo = StaticWriteSubtitleImageDto.builder()
        .subtitle("2023")
        .displayOrder(2)
        .build();
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = StaticWriteSubtitleImageEntity.builder()
        .subtitle("2022")
        .staticWriteTitle(staticWriteTitleEntity)
        .displayOrder(1)
        .build();
    StaticWriteSubtitleImageEntity saved = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageEntity);

    // when
    saved.update(newInfo, null);
    StaticWriteSubtitleImageEntity result = staticWriteSubtitleImageRepository.save(saved);

    // then
    assertThat(result.getId()).isEqualTo(saved.getId());
    assertThat(result.getSubtitle()).isEqualTo(newInfo.getSubtitle());
    assertThat(result.getDisplayOrder()).isEqualTo(newInfo.getDisplayOrder());
  }

  @Test
  @DisplayName("서브 타이틀 삭제")
  void delete() {
    // given
    StaticWriteTitleEntity staticWriteTitleEntity = staticWriteTitleRepository.save(
        StaticWriteTitleEntity.builder()
            .title("테스트 타이틀")
            .type("테스트 타입")
            .build());
    StaticWriteSubtitleImageEntity saved = staticWriteSubtitleImageRepository.save(
        StaticWriteSubtitleImageEntity.builder()
            .subtitle("2022")
            .staticWriteTitle(staticWriteTitleEntity)
            .displayOrder(1)
            .build());

    // when
    staticWriteSubtitleImageRepository.delete(saved);
    Optional<StaticWriteSubtitleImageEntity> result = staticWriteSubtitleImageRepository.findById(
        saved.getId());

    // then
    assertThat(result).isEmpty();
  }
}
