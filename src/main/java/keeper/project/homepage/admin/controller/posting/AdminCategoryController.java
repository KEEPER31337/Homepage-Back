package keeper.project.homepage.admin.controller.posting;

import keeper.project.homepage.admin.service.posting.AdminCategoryService;
import keeper.project.homepage.admin.dto.category.reqeust.CategoryRequest;
import keeper.project.homepage.admin.dto.category.result.CategoryResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/admin/category")
public class AdminCategoryController {

  private final ResponseService responseService;
  private final AdminCategoryService adminCategoryService;

  @Secured("ROLE_회장")
  @PostMapping(value = "/create")
  public SingleResult<CategoryResult> createCategory(
      @RequestBody CategoryRequest categoryDto
  ) {
    return responseService.getSuccessSingleResult(adminCategoryService.createCategory(categoryDto));
  }

  @Secured("ROLE_회장")
  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/modify/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<CategoryResult> modifyCategory(
      @PathVariable("id") Long id,
      @RequestBody CategoryRequest categoryDto
  ) {
    return responseService.getSuccessSingleResult(adminCategoryService.modifyCategoryById(categoryDto, id));
  }

  @Secured("ROLE_회장")
  @DeleteMapping("/delete/{id}")
  public SingleResult<CategoryResult> deleteCategoryById(
      @PathVariable("id") Long id
  ) {
    return responseService.getSuccessSingleResult(adminCategoryService.deleteCategoryById(id));
  }
}
