package keeper.project.homepage.dto.result;

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
public class CategoryResult {

  private Long id;

  private String name;

  private List<CategoryResult> children = new ArrayList<>();

  public CategoryResult(CategoryEntity category) {
    this.id = category.getId();
    this.name = category.getName();
    if(category.getChildren() != null) {
      this.children = category.getChildren().stream().map(CategoryResult::new).collect(
          Collectors.toList());
    }
  }
}
