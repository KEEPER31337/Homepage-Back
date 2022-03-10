package keeper.project.homepage.admin.service.posting;

import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.admin.dto.category.reqeust.CategoryRequest;
import keeper.project.homepage.admin.dto.category.result.CategoryResult;
import keeper.project.homepage.exception.posting.CustomAccessRootCategoryException;
import keeper.project.homepage.exception.posting.CustomCategoryNotFoundException;
import keeper.project.homepage.exception.posting.CustomParentCategoryNotFoundException;
import keeper.project.homepage.repository.posting.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminCategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryResult createCategory(CategoryRequest categoryRequest) {
    checkParentCategoryNotFound(categoryRequest);

    CategoryEntity newCategory = categoryRepository.save(categoryRequest.toEntity());
    return new CategoryResult(newCategory);
  }

  public CategoryResult modifyCategoryById(CategoryRequest categoryRequest, Long categoryId) {
    checkRootCategory(categoryId);

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(CustomCategoryNotFoundException::new);

    checkParentCategoryNotFound(categoryRequest);

    categoryEntity.updateInfo(categoryRequest);
    CategoryEntity modifyEntity = categoryRepository.save(categoryEntity);
    return new CategoryResult(modifyEntity);
  }

  public CategoryResult deleteCategoryById(Long categoryId) {
    checkRootCategory(categoryId);

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(CustomCategoryNotFoundException::new);

    categoryRepository.delete(categoryEntity);
    return new CategoryResult(categoryEntity);
  }

  private void checkRootCategory(Long categoryId) {
    if (categoryId == 0) {
      throw new CustomAccessRootCategoryException();
    }
  }

  private void checkParentCategoryNotFound(CategoryRequest categoryRequest) {
    if (categoryRequest.getParentId() != null && categoryRequest.getParentId() != 0) {
      CategoryEntity categoryEntity = categoryRepository.findById(categoryRequest.getParentId())
          .orElseThrow(CustomParentCategoryNotFoundException::new);
    }
  }

}
