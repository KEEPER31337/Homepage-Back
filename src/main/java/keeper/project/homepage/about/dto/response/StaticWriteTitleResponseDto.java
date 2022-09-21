package keeper.project.homepage.about.dto.response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.about.entity.StaticWriteTitleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class StaticWriteTitleResponseDto {

  private Long id;

  private String title;

  private String type;

  @Builder.Default
  private List<StaticWriteSubtitleImageResponseDto> subtitleImageResults = new ArrayList<>();

  public StaticWriteTitleResponseDto(StaticWriteTitleEntity staticWriteTitleEntity) {
    this.id = staticWriteTitleEntity.getId();
    this.title = staticWriteTitleEntity.getTitle();
    this.type = staticWriteTitleEntity.getType();
    if (staticWriteTitleEntity.getStaticWriteSubtitleImages() != null) {
      this.subtitleImageResults = staticWriteTitleEntity.getStaticWriteSubtitleImages()
          .stream()
          .map(StaticWriteSubtitleImageResponseDto::new)
          .sorted(Comparator.comparing(StaticWriteSubtitleImageResponseDto::getDisplayOrder))
          .collect(Collectors.toList());
    }
  }

}
