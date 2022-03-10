package keeper.project.homepage.user.controller.posting;

import keeper.project.homepage.admin.dto.category.result.CategoryResult;
import keeper.project.homepage.admin.dto.category.result.CategoryWithChildResult;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.service.posting.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/category")
public class CategoryController {

  private final CategoryService categoryService;
  private final ResponseService responseService;

  @GetMapping("/lists/head/all")
  public ListResult<CategoryWithChildResult> getAllHeadCategoryAndChild() {
    return responseService.getSuccessListResult(categoryService.getAllHeadCategoryAndChild());
  }

  @GetMapping("/lists/head")
  public ListResult<CategoryResult> getAllHeadCategory() {
    return responseService.getSuccessListResult(categoryService.getAllHeadCategory());
  }

  @GetMapping("/lists/all/{parentId}")
  public ListResult<CategoryWithChildResult> getAllCategoryAndChildByParentId(
      @PathVariable("parentId") Long parentId
  ) {
    return responseService.getSuccessListResult(
        categoryService.getAllCategoryAndChildByParentId(parentId));
  }

  @GetMapping("/lists/{parentId}")
  public ListResult<CategoryResult> getAllCategoryByParentId(
      @PathVariable("parentId") Long parentId
  ) {
    return responseService.getSuccessListResult(categoryService.getAllCategoryByParentId(parentId));
  }

}
