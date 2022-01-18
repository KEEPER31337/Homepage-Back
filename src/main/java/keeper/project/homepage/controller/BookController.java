package keeper.project.homepage.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import keeper.project.homepage.dto.CommonResult;
import keeper.project.homepage.entity.BookEntity;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.BookRepository;
import keeper.project.homepage.service.BookManageService;
import keeper.project.homepage.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
  private final BookEntity bookEntity;

  @PostMapping(value = "/addbook")
  @ResponseBody
  public CommonResult add(
      @RequestParam String title,
      @RequestParam String author,
      @RequestParam String picture,
      @RequestParam String information,
      @RequestParam Long quantity) {

    if(bookManageService.isNotMax(title, quantity)) {
      bookRepository.save(BookEntity.builder()
          .title(title)
          .author(author)
          .picture(picture)
          .information(information)
          .total(quantity)
          .borrow(0L)
          .enable(quantity)
          .registerDate(new Date())
          .build());
      return responseService.getSuccessResult();
    }else{
      return responseService.getFailResult();
    }
  }

  @PostMapping(value = "/deleteBook")
  @ResponseBody
  public CommonResult delete(@RequestParam String title, @RequestParam Long quantity) {

    String author = bookRepository.findByTitle(title).get().getAuthor();
    String picture = bookRepository.findByTitle(title).get().getPicture();
    String information = bookRepository.findByTitle(title).get().getInformation();
    Long borrow = bookRepository.findByTitle(title).get().getBorrow();

    if(bookManageService.isCanDelete(title, quantity)){
      if(bookRepository.findByTitle(title).get().getTotal()-quantity == 0){
        bookRepository.delete(bookEntity);
      }else{
        bookRepository.save(BookEntity.builder()
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
    }else{
      return responseService.getFailResult();
    }
  }
}
