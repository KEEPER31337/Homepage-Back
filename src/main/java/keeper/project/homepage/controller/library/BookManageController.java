package keeper.project.homepage.controller.library;

import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.repository.library.BookRepository;
import keeper.project.homepage.service.library.BookManageService;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/v1")
@RestController
@RequiredArgsConstructor
@Log4j2
public class BookManageController {

  private final BookRepository bookRepository;
  private final ResponseService responseService;
  private final BookManageService bookManageService;
  private final AuthService authService;

  @Secured({"ROLE_사서", "ROLE_회장"})
  @PostMapping(value = "/addbook")
  public CommonResult add(
      @RequestParam String title,
      @RequestParam String author,
      @RequestParam @Nullable String information,
      @RequestParam Long quantity) {

    return bookManageService.doAdd(title, author, information, quantity);

  }

  @Secured({"ROLE_사서", "ROLE_회장"})
  @PostMapping(value = "/deletebook")
  public CommonResult delete(@RequestParam String title, @RequestParam String author,
      @RequestParam Long quantity) {

    return bookManageService.doDelete(title, author, quantity);

  }

  @Secured({"ROLE_사서", "ROLE_회장"})
  @PostMapping(value = "/borrowbook")
  public CommonResult borrow(
      @RequestParam String title,
      @RequestParam String author,
      @RequestParam Long quantity) {

    Long borrowMemberId = authService.getMemberIdByJWT();
    return bookManageService.doBorrow(title, author, borrowMemberId, quantity);
  }
}
