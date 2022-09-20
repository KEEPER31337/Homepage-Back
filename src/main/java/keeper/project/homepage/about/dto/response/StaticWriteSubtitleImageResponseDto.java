package keeper.project.homepage.about.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.entity.about.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.util.EnvironmentProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StaticWriteSubtitleImageResponseDto {

  private Long id;

  private String subtitle;

  private Long staticWriteTitleId;

  private String thumbnailPath;

  private Integer displayOrder;

  private List<StaticWriteContentResponseDto> staticWriteContents = new ArrayList<>();

  public StaticWriteSubtitleImageResponseDto(
      StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity) {
    this.id = staticWriteSubtitleImageEntity.getId();
    this.subtitle = staticWriteSubtitleImageEntity.getSubtitle();
    this.staticWriteTitleId = staticWriteSubtitleImageEntity.getStaticWriteTitle().getId();
    this.thumbnailPath =
        staticWriteSubtitleImageEntity.getThumbnail() == null ?
            EnvironmentProperty.getThumbnailPath(1L) :
            EnvironmentProperty.getThumbnailPath(
                staticWriteSubtitleImageEntity.getThumbnail().getId());
    this.displayOrder = staticWriteSubtitleImageEntity.getDisplayOrder();
    if (staticWriteSubtitleImageEntity.getStaticWriteContents() != null) {
      this.staticWriteContents = staticWriteSubtitleImageEntity.getStaticWriteContents()
          .stream()
          .map(StaticWriteContentResponseDto::new).collect(
              Collectors.toList());
    }
  }

}
