package keeper.project.homepage.dto.posting.category.result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.entity.posting.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryWithChildResult {

  private Long id;

  private String name;

  private String href;

  private List<CategoryWithChildResult> children = new ArrayList<>();

  public CategoryWithChildResult(CategoryEntity category) {
    this.id = category.getId();
    this.name = category.getName();
    this.href = category.getHref();
    if(category.getChildren() != null) {
      this.children = category.getChildren().stream().map(CategoryWithChildResult::new).collect(
          Collectors.toList());
    }
  }

}
