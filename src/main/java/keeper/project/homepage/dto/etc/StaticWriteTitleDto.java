package keeper.project.homepage.dto.etc;

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
public class StaticWriteTitleDto {

  private String title;

  private String type;

  public StaticWriteTitleEntity toEntity() {

    return StaticWriteTitleEntity.builder()
        .title(title)
        .type(type)
        .build();
  }
}
