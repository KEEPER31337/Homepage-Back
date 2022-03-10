package keeper.project.homepage.admin.dto.etc;

import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.repository.etc.StaticWriteTitleRepository;
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
