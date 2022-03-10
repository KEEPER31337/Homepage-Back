package keeper.project.homepage.admin.dto.category.reqeust;

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
public class CategoryRequest {

  private String name;

  private Long parentId;

  private String href;

  public CategoryEntity toEntity() {

    return CategoryEntity.builder()
        .name(name)
        .parentId(parentId)
        .href(href)
        .build();
  }

}
