package keeper.project.homepage.about.dto.request;

import keeper.project.homepage.about.entity.StaticWriteContentEntity;
import keeper.project.homepage.about.entity.StaticWriteSubtitleImageEntity;
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
public class StaticWriteContentDto {

  private String content;
  private Long staticWriteSubtitleImageId;
  private Integer displayOrder;

  public StaticWriteContentEntity toEntity(
      StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity) {

    return StaticWriteContentEntity.builder()
        .content(content)
        .staticWriteSubtitleImage(staticWriteSubtitleImageEntity)
        .displayOrder(displayOrder)
        .build();
  }

}
