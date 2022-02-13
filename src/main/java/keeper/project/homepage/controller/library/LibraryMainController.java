package keeper.project.homepage.controller.library;

import java.util.List;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.library.LibraryMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1")
@RestController
@RequiredArgsConstructor
@Log4j2
public class LibraryMainController {

  private final LibraryMainService libraryMainService;
  private final ResponseService responseService;

  @GetMapping(value = "/recentbooks")
  public ListResult<BookEntity> displayRecentBooks(
      @PageableDefault(size = 10, sort = "registerDate", direction = Direction.DESC) Pageable pageable) {

    return responseService.getSuccessListResult(libraryMainService.displayTenBooks(pageable));
  }

  @GetMapping(value = "/searchbooks")
  public ListResult<BookEntity> searchBooks(@RequestParam String keyword,
      @PageableDefault(size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {

    return responseService.getSuccessListResult(libraryMainService.searchBooks(keyword, pageable));
  }

  @GetMapping(value = "/selectedbook/information")
  public SingleResult<BookEntity> sendBookInformation(@RequestParam String title, @RequestParam String author) {

    return responseService.getSuccessSingleResult(libraryMainService.selectedBook(title, author));
  }
}
