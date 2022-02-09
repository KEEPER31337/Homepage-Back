package keeper.project.homepage.dto.etc;

import javax.persistence.Column;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
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
  private Integer displayOrder;
  private StaticWriteSubtitleImageEntity staticWriteSubtitleImage;
}
