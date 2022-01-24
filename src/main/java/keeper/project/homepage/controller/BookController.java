package keeper.project.homepage.controller;

import java.util.Date;
import java.util.Optional;
import keeper.project.homepage.dto.CommonResult;
import keeper.project.homepage.entity.BookEntity;
import keeper.project.homepage.repository.BookRepository;
import keeper.project.homepage.service.BookManageService;
import keeper.project.homepage.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/v1")
@RestController
@RequiredArgsConstructor
@Log4j2
public class BookController {

  private final BookRepository bookRepository;
  private final ResponseService responseService;
  private final BookManageService bookManageService;

  @PostMapping(value = "/addbook")
  @ResponseBody
  public CommonResult add(
      @RequestParam String title,
      @RequestParam String author,
      @RequestParam @Nullable String picture,
      @RequestParam @Nullable String information,
      @RequestParam Long quantity) {

    Long total = bookManageService.isCanAdd(title, author, quantity);

    if (total != -1L) {
      bookManageService.addBook(title, author, picture, information, total);
      return responseService.getSuccessResult();
    }
    return responseService.getFailResult(-1, "수량 초과입니다.");
  }

  @PostMapping(value = "/deletebook")
  @ResponseBody
  public CommonResult delete(@RequestParam String title, @RequestParam String author,
      @RequestParam Long quantity) {

    if (bookManageService.isExist(title, author)) {
      Long numOfBooks = bookRepository.findByTitleAndAuthor(title, author).get().getEnable();
      Long numOfBorrow = bookRepository.findByTitleAndAuthor(title, author).get().getBorrow();
      if (numOfBooks - quantity == 0 && numOfBorrow == 0) {
        BookEntity bookEntity = bookRepository.findByTitleAndAuthor(title, author).get();
        bookRepository.delete(bookEntity);
      } else if (numOfBooks - quantity < 0) {
        return responseService.getFailResult(-1, "삭제 가능한 수량보다 많습니다.");
      } else {
        bookManageService.updateDeleteInformation(title, author, quantity);
      }
      return responseService.getSuccessResult();
    }

    return responseService.getFailResult(-2, "책이 존재하지 않습니다.");

  }
}
