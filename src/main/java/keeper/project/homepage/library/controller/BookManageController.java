package keeper.project.homepage.library.controller;

import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.library.service.BookManageService;
import keeper.project.homepage.util.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.util.image.preprocessing.ImageCenterCropping;
import keeper.project.homepage.library.dto.BookDto;
import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.library.entity.BookBorrowEntity;
import keeper.project.homepage.util.image.preprocessing.ImageSize;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import keeper.project.homepage.util.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("/v1/admin")
@RestController
@RequiredArgsConstructor
@Log4j2
@Secured({"ROLE_사서", "ROLE_회장"})
public class BookManageController {

  private final BookManageService bookManageService;
  private final AuthService authService;
  private final FileService fileService;
  private final ThumbnailService thumbnailService;
  private final ResponseService responseService;

  @GetMapping(value = "/overduebooks")
  public ListResult<BookBorrowEntity> sendOverdueBooks(
      @PageableDefault(size = 10, sort = "expireDate", direction = Direction.ASC)
          Pageable pageable) {

    return responseService.getSuccessListResult(bookManageService.sendOverdueBooks(pageable));
  }

  @PostMapping(value = "/addbook", consumes = "multipart/form-data", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public CommonResult add(
      @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
      BookDto bookDto) {

    HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String ip = httpServletRequest.getHeader("X-FORWARDED-FOR");
    if (ip == null) {
      ip = httpServletRequest.getRemoteAddr();
    }

    ThumbnailEntity thumbnailEntity = thumbnailService.save(ThumbType.BookThumbnail,
        new ImageCenterCropping(ImageSize.LARGE), thumbnail, ip);

    if (thumbnailEntity == null) {
      throw new CustomThumbnailEntityNotFoundException();
    }

    return bookManageService.doAdd(bookDto, thumbnailEntity);
  }

  @PostMapping(value = "/deletebook")
  public CommonResult delete(@RequestParam String title, @RequestParam String author,
      @RequestParam Long quantity) {

    return bookManageService.doDelete(title, author, quantity);

  }

  @PostMapping(value = "/borrowbook")
  public CommonResult borrow(
      @RequestParam String title,
      @RequestParam String author,
      @RequestParam Long quantity) {

    Long borrowMemberId = authService.getMemberIdByJWT();
    return bookManageService.doBorrow(title, author, borrowMemberId, quantity);
  }

  @PostMapping(value = "/returnbook")
  public CommonResult returnBook(
      @RequestParam String title,
      @RequestParam String author,
      @RequestParam Long quantity) {

    Long returnMemberId = authService.getMemberIdByJWT();
    return bookManageService.doReturn(title, author, returnMemberId, quantity);
  }
}
