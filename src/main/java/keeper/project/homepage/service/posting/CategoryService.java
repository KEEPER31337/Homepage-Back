package keeper.project.homepage.service.posting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import keeper.project.homepage.dto.posting.CategoryDto;
import keeper.project.homepage.dto.result.CategoryResult;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.exception.posting.CustomAccessRootCategoryException;
import keeper.project.homepage.exception.posting.CustomCategoryNotFoundException;
import keeper.project.homepage.exception.posting.CustomParentCategoryNotFoundException;
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

  public void checkRootCategory(Long id) {
    if(id == 0) {
      throw new CustomAccessRootCategoryException();
    }
  }

  public CategoryResult createCategory(CategoryDto categoryDto) {

    if(categoryDto.getParentId() != null && categoryDto.getParentId() != 0) {
      CategoryEntity categoryEntity = categoryRepository.findById(categoryDto.getParentId())
            .orElseThrow(CustomParentCategoryNotFoundException::new);
    }

    CategoryEntity newCategory = categoryRepository.save(categoryDto.toEntity());
    return new CategoryResult(newCategory);
  }

  public CategoryResult modifyCategory(CategoryDto categoryDto, Long id) {
    checkRootCategory(id);

    CategoryEntity categoryEntity = categoryRepository.findById(id)
        .orElseThrow(CustomCategoryNotFoundException::new);

    if(categoryDto.getParentId() != null && categoryDto.getParentId() != 0) {
      CategoryEntity parentEntity = categoryRepository.findById(categoryDto.getParentId())
          .orElseThrow(CustomParentCategoryNotFoundException::new);
    }

    categoryEntity.updateInfo(categoryDto);
    CategoryEntity modifyEntity = categoryRepository.save(categoryEntity);
    return new CategoryResult(modifyEntity);
  }

  public CategoryResult deleteCategoryById(Long id) {
    checkRootCategory(id);

    CategoryEntity categoryEntity = categoryRepository.findById(id)
        .orElseThrow(CustomCategoryNotFoundException::new);
    categoryRepository.delete(categoryEntity);
    return new CategoryResult(categoryEntity);
  }
}
