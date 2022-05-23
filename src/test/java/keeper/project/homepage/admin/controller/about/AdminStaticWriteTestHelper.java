package keeper.project.homepage.admin.controller.about;

import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.about.StaticWriteContentEntity;
import keeper.project.homepage.entity.about.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;

public class AdminStaticWriteTestHelper extends ApiControllerTestHelper {
  
  protected StaticWriteTitleEntity generateTestTitle(Integer index) {
    StaticWriteTitleEntity staticWriteTitleEntity = StaticWriteTitleEntity.builder()
        .title("테스트 타이틀" + index)
        .type("테스트 타입" + index)
        .build();
    return staticWriteTitleRepository.save(staticWriteTitleEntity);
  }

  protected StaticWriteSubtitleImageEntity generateTestSubtitleImage(StaticWriteTitleEntity title, Integer index) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = StaticWriteSubtitleImageEntity.builder()
        .subtitle("테스트 서브 타이틀" + index)
        .displayOrder(index)
        .staticWriteTitle(title)
        .thumbnail(generateThumbnailEntity())
        .build();
    return staticWriteSubtitleImageRepository.save(staticWriteSubtitleImageEntity);
  }

  protected StaticWriteContentEntity generateTestContent(StaticWriteSubtitleImageEntity subTitleImage, Integer index) {
    StaticWriteContentEntity staticWriteContentEntity = StaticWriteContentEntity.builder()
        .content("테스트 컨텐츠" + index)
        .displayOrder(index)
        .staticWriteSubtitleImage(subTitleImage)
        .build();
    return staticWriteContentRepository.save(staticWriteContentEntity);
  }

}
