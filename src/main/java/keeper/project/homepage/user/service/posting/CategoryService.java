package keeper.project.homepage.user.service.posting;

import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.dto.posting.category.result.CategoryResult;
import keeper.project.homepage.dto.posting.category.result.CategoryWithChildResult;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.repository.posting.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public List<CategoryWithChildResult> getAllHeadCategoryAndChild() {
    List<CategoryEntity> categoryEntities = categoryRepository.findAllByParentIdIsNull();
    return categoryEntities.stream().map(CategoryWithChildResult::new).collect(Collectors.toList());
  }

  public List<CategoryResult> getAllHeadCategory() {
    List<CategoryEntity> categoryEntities = categoryRepository.findAllByParentIdIsNull();
    return categoryEntities.stream().map(CategoryResult::new).collect(Collectors.toList());
  }

  public List<CategoryWithChildResult> getAllCategoryAndChildByParentId(Long parentId) {
    List<CategoryEntity> categoryEntities = categoryRepository.findAllByParentId(parentId);
    return categoryEntities.stream().map(CategoryWithChildResult::new).collect(Collectors.toList());
  }

  public List<CategoryResult> getAllCategoryByParentId(Long parentId) {
    List<CategoryEntity> categoryEntities = categoryRepository.findAllByParentId(parentId);
    return categoryEntities.stream().map(CategoryResult::new).collect(Collectors.toList());
  }

}
