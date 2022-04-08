package keeper.project.homepage.admin.dto.etc;

import keeper.project.homepage.entity.etc.StaticWriteContentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StaticWriteContentResult {

  private Long id;

  private String content;

  private Long staticWriteSubtitleImageId;

  private Integer displayOrder;

  public StaticWriteContentResult(StaticWriteContentEntity staticWriteContentEntity) {
    this.id = staticWriteContentEntity.getId();
    this.content = staticWriteContentEntity.getContent();
    this.staticWriteSubtitleImageId = staticWriteContentEntity.getStaticWriteSubtitleImage().getId();
    this.displayOrder = staticWriteContentEntity.getDisplayOrder();
  }

}