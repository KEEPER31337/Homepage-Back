package keeper.project.homepage.admin.dto.about.request;

import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.about.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;
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
