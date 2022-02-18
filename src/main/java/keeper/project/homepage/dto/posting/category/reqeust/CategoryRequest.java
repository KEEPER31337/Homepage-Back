package keeper.project.homepage.dto.posting;

import keeper.project.homepage.entity.posting.CategoryEntity;
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
public class CategoryDto {

  private String name;

  private Long parentId;

  public CategoryEntity toEntity() {

    return CategoryEntity.builder()
        .name(name)
        .parentId(parentId)
        .build();
  }

}
