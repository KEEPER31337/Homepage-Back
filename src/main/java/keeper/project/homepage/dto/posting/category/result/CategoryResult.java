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
public class CategoryResult {

  private Long id;

  private String name;

  private String href;

  public CategoryResult(CategoryEntity category) {
    this.id = category.getId();
    this.name = category.getName();
    this.href = category.getHref();
  }
}