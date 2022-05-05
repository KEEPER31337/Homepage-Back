package keeper.project.homepage.admin.service.about;

import java.io.File;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteContentEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class AdminAboutTitleServiceTest extends ApiControllerTestHelper {

  @Autowired
  private AdminAboutTitleService adminAboutTitleService;

  private ThumbnailEntity thumbnailEntity;
  private StaticWriteTitleEntity staticWriteTitleEntity;
  private StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity;
  private StaticWriteContentEntity staticWriteContentEntity;

  @BeforeEach
  public void setUp() throws Exception {
    thumbnailEntity = generateThumbnailEntity();

    staticWriteTitleEntity = generateTestTitle(1);
    staticWriteSubtitleImageEntity = generateTestSubtitle(1);
    staticWriteContentEntity = generateTestContent(1);

    staticWriteTitleEntity.getStaticWriteSubtitleImages().add(staticWriteSubtitleImageEntity);
    staticWriteSubtitleImageEntity.getStaticWriteContents().add(staticWriteContentEntity);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  public StaticWriteTitleEntity generateTestTitle(Integer index) {
    StaticWriteTitleEntity staticWriteTitleEntity = StaticWriteTitleEntity.builder()
        .title("테스트 타이틀" + index)
        .type("테스트 타입" + index)
        .build();
    return staticWriteTitleRepository.save(staticWriteTitleEntity);
  }

  public StaticWriteSubtitleImageEntity generateTestSubtitle(Integer index) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = StaticWriteSubtitleImageEntity.builder()
        .subtitle("테스트 서브 타이틀" + index)
        .displayOrder(index)
        .staticWriteTitle(staticWriteTitleEntity)
        .thumbnail(thumbnailEntity)
        .build();
    return staticWriteSubtitleImageRepository.save(staticWriteSubtitleImageEntity);
  }

  public StaticWriteContentEntity generateTestContent(Integer index) {
    StaticWriteContentEntity staticWriteContentEntity = StaticWriteContentEntity.builder()
        .content("테스트 컨텐츠" + index)
        .displayOrder(index)
        .staticWriteSubtitleImage(staticWriteSubtitleImageEntity)
        .build();
    return staticWriteContentRepository.save(staticWriteContentEntity);
  }

  @Test
  @DisplayName("페이지 블록 삭제 시 서브 타이틀, 썸네일, 컨텐츠 자동 삭제 테스트")
  public void titleCascadeTest() throws Exception {
    Assertions.assertTrue(
        staticWriteSubtitleImageRepository.findById(staticWriteSubtitleImageEntity.getId())
            .isPresent());
    Assertions.assertTrue(
        thumbnailRepository.findById(thumbnailEntity.getId()).isPresent()
    );
    Assertions.assertTrue(
        staticWriteContentRepository.findById(staticWriteContentEntity.getId()).isPresent()
    );

    // 삭제 작업 진행
    adminAboutTitleService.deleteTitleById(staticWriteTitleEntity.getId());

    Assertions.assertTrue(
        staticWriteSubtitleImageRepository.findById(staticWriteSubtitleImageEntity.getId())
            .isEmpty());
    Assertions.assertTrue(
        thumbnailRepository.findById(thumbnailEntity.getId()).isEmpty()
    );
    Assertions.assertTrue(
        staticWriteContentRepository.findById(staticWriteContentEntity.getId()).isEmpty()
    );
  }

}
