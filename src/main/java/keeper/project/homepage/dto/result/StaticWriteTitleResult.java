package keeper.project.homepage.dto.result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StaticWriteTitleResult {

  private Long id;

  private String title;

  private String type;

  private List<StaticWriteSubtitleImageResult> subtitleImageResults = new ArrayList<>();

  public StaticWriteTitleResult(StaticWriteTitleEntity staticWriteTitleEntity) {
    this.id = staticWriteTitleEntity.getId();
    this.title = staticWriteTitleEntity.getTitle();
    this.type = staticWriteTitleEntity.getType();
    if (staticWriteTitleEntity.getStaticWriteSubtitleImages() != null) {
      this.subtitleImageResults = staticWriteTitleEntity.getStaticWriteSubtitleImages().stream()
          .map(StaticWriteSubtitleImageResult::new).collect(
              Collectors.toList());
    }
  }

}
