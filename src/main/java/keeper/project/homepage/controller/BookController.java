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

    Long total = bookManageService.isCanAdd(title, quantity);

    if (total != -1L) {
      bookRepository.save(
          BookEntity.builder()
              .title(title)
              .author(author)
              .picture(picture)
              .information(information)
              .total(total)
              .borrow(0L)
              .enable(total)
              .registerDate(new Date())
              .build());
      return responseService.getSuccessResult();
    }
    return responseService.getFailResult(-1, "수량 초과입니다.");
  }

  @PostMapping(value = "/deletebook")
  @ResponseBody
  public CommonResult delete(@RequestParam String title, @RequestParam Long quantity) {

    Long numOfBooks = bookRepository.findByTitle(title).get().getTotal();

    if (bookManageService.isCanDelete(title, quantity)) {
      if (numOfBooks - quantity == 0) {
        BookEntity bookEntity = bookRepository.findByTitle(title).get();
        bookRepository.delete(bookEntity);
      } else if (numOfBooks - quantity < 0) {
        return responseService.getFailResult(-1, "삭제 가능한 수량보다 많습니다.");
      } else {
        String author = bookRepository.findByTitle(title).get().getAuthor();
        String picture = bookRepository.findByTitle(title).get().getPicture();
        String information = bookRepository.findByTitle(title).get().getInformation();
        Long borrow = bookRepository.findByTitle(title).get().getBorrow();

        bookRepository.save(
            BookEntity.builder()
                .title(title)
                .author(author)
                .picture(picture)
                .information(information)
                .total(quantity)
                .borrow(borrow)
                .enable(quantity)
                .registerDate(new Date())
                .build());
      }
      return responseService.getSuccessResult();
    }

    return responseService.getFailResult(-2, "책이 존재하지 않습니다.");

  }
}
