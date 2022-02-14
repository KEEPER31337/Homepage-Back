package keeper.project.homepage.controller.posting;

import java.util.List;
import keeper.project.homepage.dto.posting.CategoryDto;
import keeper.project.homepage.dto.result.CategoryResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.posting.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/category")
public class CategoryController {

  private final CategoryService categoryService;
  private final ResponseService responseService;

  @GetMapping("/id/{parentId}")
  public ResponseEntity<List<CategoryResult>> getAllCategoryByParentId(
      @PathVariable("parentId") Long parentId
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategoryByParentId(parentId));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<List<CategoryResult>> getAllCategoryByName(
      @PathVariable("name") String name
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategoryByName(name));
  }

  @Secured("ROLE_회장")
  @PostMapping(value = "/new")
  public SingleResult<CategoryResult> createCategory(
      @RequestBody CategoryDto categoryDto
  ) {
    return responseService.getSuccessSingleResult(categoryService.createCategory(categoryDto));
  }

  @Secured("ROLE_회장")
  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/modify/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<CategoryResult> modifyCategory(
      @PathVariable("id") Long id,
      @RequestBody CategoryDto categoryDto
  ) {
    return responseService.getSuccessSingleResult(categoryService.modifyCategory(categoryDto, id));
  }

  @Secured("ROLE_회장")
  @DeleteMapping("/delete/{id}")
  public SingleResult<CategoryResult> deleteCategoryById(
      @PathVariable("id") Long id
  ) {
    return responseService.getSuccessSingleResult(categoryService.deleteCategoryById(id));
  }

}
