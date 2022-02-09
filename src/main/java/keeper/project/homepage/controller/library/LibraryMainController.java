package keeper.project.homepage.controller.library;

import java.util.List;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.service.library.LibraryMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1")
@RestController
@RequiredArgsConstructor
@Log4j2
public class LibraryMainController {

  private final LibraryMainService libraryMainService;

  @GetMapping(value = "/recentbooks")
  public Page<BookEntity> displayRecentBooks() {

    return libraryMainService.displayTenBooks();
  }

}
