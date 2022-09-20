package keeper.project.homepage.about.dto.response;

import keeper.project.homepage.about.entity.StaticWriteContentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StaticWriteContentResponseDto {

  private Long id;

  private String content;

  private Long staticWriteSubtitleImageId;

  private Integer displayOrder;

  public StaticWriteContentResponseDto(StaticWriteContentEntity staticWriteContentEntity) {
    this.id = staticWriteContentEntity.getId();
    this.content = staticWriteContentEntity.getContent();
    this.staticWriteSubtitleImageId = staticWriteContentEntity.getStaticWriteSubtitleImage().getId();
    this.displayOrder = staticWriteContentEntity.getDisplayOrder();
  }

}