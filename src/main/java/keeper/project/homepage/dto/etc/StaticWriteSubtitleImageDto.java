package keeper.project.homepage.dto.etc;

import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
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
  private Integer displayOrder;
  private StaticWriteTitleEntity staticWriteTitle;
  private ThumbnailEntity thumbnail;
}
