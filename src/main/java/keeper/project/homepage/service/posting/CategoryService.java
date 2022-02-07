package keeper.project.homepage.service.posting;

import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.dto.result.CategoryResult;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.repository.posting.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public List<CategoryResult> getAllCategoryByParentId(Long parentId) {
    List<CategoryEntity> categories = categoryRepository.findAllByParentId(parentId);
    return categories.stream().map(CategoryResult::new).collect(Collectors.toList());
  }

  public List<CategoryResult> getAllCategoryByName(String name) {
    List<CategoryEntity> categories = categoryRepository.findAllByName(name);
    return categories.stream().map(CategoryResult::new).collect(Collectors.toList());
  }
}
