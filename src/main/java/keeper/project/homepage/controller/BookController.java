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

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, false));
  }

  @PostMapping(value = "/addbook")
  @ResponseBody
  public CommonResult add(
      @RequestParam String title,
      @RequestParam String author,
      @RequestParam String picture,
      @RequestParam String information,
      @RequestParam Long quantity) {
    bookManageService.addBook(title, author, picture, information, quantity);
    return responseService.getSuccessResult();
  }

}
