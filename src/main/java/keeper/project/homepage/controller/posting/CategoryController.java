package keeper.project.homepage.controller.posting;

import java.util.List;
import keeper.project.homepage.dto.result.CategoryResult;
import keeper.project.homepage.service.posting.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

}
