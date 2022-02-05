package keeper.project.homepage.dto.result;

import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.entity.posting.CategoryEntity;
import lombok.Getter;

@Getter
public class CategoryResult {

  private Long id;

  private String name;

  private List<CategoryResult> children;

  public CategoryResult(CategoryEntity category) {
    this.id = category.getId();
    this.name = category.getName();
    this.children = category.getChildren().stream().map(CategoryResult::new).collect(
        Collectors.toList());
  }
}
