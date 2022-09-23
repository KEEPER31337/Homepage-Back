package keeper.project.homepage.about.dto.request;

import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.about.entity.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.about.entity.StaticWriteTitleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaticWriteSubtitleImageDto {

  private String subtitle;
  private Long staticWriteTitleId;
  private Integer displayOrder;

  public StaticWriteSubtitleImageEntity toEntity(StaticWriteTitleEntity staticWriteTitleEntity,
      ThumbnailEntity thumbnailEntity) {

    return StaticWriteSubtitleImageEntity.builder()
        .subtitle(subtitle)
        .staticWriteTitle(staticWriteTitleEntity)
        .thumbnail(thumbnailEntity)
        .displayOrder(displayOrder)
        .build();
  }
}
