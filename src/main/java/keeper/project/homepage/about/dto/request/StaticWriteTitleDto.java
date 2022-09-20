package keeper.project.homepage.about.dto.request;

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
public class StaticWriteTitleDto {

  private String title;

  private String type;
}
