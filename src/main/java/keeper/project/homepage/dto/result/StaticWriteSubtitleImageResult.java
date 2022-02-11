package keeper.project.homepage.dto.result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

  private ThumbnailEntity thumbnail;

  private Integer displayOrder;

  private List<StaticWriteContentResult> staticWriteContentResults = new ArrayList<>();

  public StaticWriteSubtitleImageResult(StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity) {
    this.id = staticWriteSubtitleImageEntity.getId();
    this.subtitle = staticWriteSubtitleImageEntity.getSubtitle();
    this.staticWriteTitleId = staticWriteSubtitleImageEntity.getStaticWriteTitle().getId();
    this.thumbnail = staticWriteSubtitleImageEntity.getThumbnail();
    if(staticWriteSubtitleImageEntity.getStaticWriteContents() != null) {
      this.staticWriteContentResults = staticWriteSubtitleImageEntity.getStaticWriteContents().stream()
          .map(StaticWriteContentResult::new).collect(
              Collectors.toList());
    }
  }

}
