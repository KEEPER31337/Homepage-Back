package keeper.project.homepage.admin.controller.library;

import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.admin.service.library.BookManageService;
import keeper.project.homepage.common.ImageCenterCrop;
import keeper.project.homepage.dto.library.BookDto;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.entity.library.BookBorrowEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.service.FileService;
import keeper.project.homepage.service.ThumbnailService;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.ThumbnailService.ThumbnailSize;
import keeper.project.homepage.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("/v1/admin/book")
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

  @GetMapping(value = "/overdue")
  public ListResult<BookBorrowEntity> sendOverdueBooks(
      @PageableDefault(size = 10, sort = "expireDate", direction = Direction.ASC)
          Pageable pageable) {

    return responseService.getSuccessListResult(bookManageService.sendOverdueBooks(pageable));
  }

  @PostMapping(value = "/add", consumes = "multipart/form-data", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public CommonResult add(
      @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
      BookDto bookDto) {

    HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String ip = httpServletRequest.getHeader("X-FORWARDED-FOR");
    if (ip == null) {
      ip = httpServletRequest.getRemoteAddr();
    }

    ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
        thumbnail, ThumbnailSize.LARGE, ip);

    if (thumbnailEntity == null) {
      throw new CustomAboutFailedException();
    }

    return bookManageService.doAdd(bookDto, thumbnailEntity);
  }

  @PostMapping(value = "/delete")
  public CommonResult delete(@RequestBody String title, @RequestBody String author,
      @RequestParam Long quantity) {

    return bookManageService.doDelete(title, author, quantity);

  }

  @PostMapping(value = "/borrow")
  public CommonResult borrow(
      @RequestBody String title,
      @RequestBody String author,
      @RequestBody Long quantity) {

    Long borrowMemberId = authService.getMemberIdByJWT();
    return bookManageService.doBorrow(title, author, borrowMemberId, quantity);
  }

  @PostMapping(value = "/return")
  public CommonResult returnBook(
      @RequestBody String title,
      @RequestBody String author,
      @RequestBody Long quantity) {

    Long returnMemberId = authService.getMemberIdByJWT();
    return bookManageService.doReturn(title, author, returnMemberId, quantity);
  }
}
