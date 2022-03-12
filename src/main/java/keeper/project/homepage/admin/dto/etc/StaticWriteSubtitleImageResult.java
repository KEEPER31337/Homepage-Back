package keeper.project.homepage.admin.dto.etc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.admin.dto.etc.StaticWriteContentResult;
import keeper.project.homepage.common.controller.util.ImageController;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StaticWriteSubtitleImageResult {

  private Long id;

  private String subtitle;

  private Long staticWriteTitleId;

  private String thumbnailPath;

  private Integer displayOrder;

  private List<StaticWriteContentResult> staticWriteContentResults = new ArrayList<>();

  public StaticWriteSubtitleImageResult(
      StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity) {
    this.id = staticWriteSubtitleImageEntity.getId();
    this.subtitle = staticWriteSubtitleImageEntity.getSubtitle();
    this.staticWriteTitleId = staticWriteSubtitleImageEntity.getStaticWriteTitle().getId();
    this.thumbnailPath =
        ImageController.THUMBNAIL_PATH + staticWriteSubtitleImageEntity.getThumbnail().getId();
    this.displayOrder = staticWriteSubtitleImageEntity.getDisplayOrder();
    if (staticWriteSubtitleImageEntity.getStaticWriteContents() != null) {
      this.staticWriteContentResults = staticWriteSubtitleImageEntity.getStaticWriteContents()
          .stream()
          .map(StaticWriteContentResult::new).collect(
              Collectors.toList());
    }
  }

}
