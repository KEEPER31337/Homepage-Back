package keeper.project.homepage.controller.admin.library;

import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.common.ImageCenterCrop;
import keeper.project.homepage.dto.library.BookDto;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.entity.library.BookBorrowEntity;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.repository.library.BookRepository;
import keeper.project.homepage.service.FileService;
import keeper.project.homepage.service.ThumbnailService;
import keeper.project.homepage.service.library.BookManageService;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
      MediaType.TEXT_PLAIN_VALUE})
  public CommonResult add(
      @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
      BookDto bookDto) {

    HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String ip = httpServletRequest.getHeader("X-FORWARDED-FOR");
    if (ip == null) {
      ip = httpServletRequest.getRemoteAddr();
    }

    ThumbnailEntity thumbnailEntity = null;
    FileEntity fileEntity = fileService.saveOriginalThumbnail(thumbnail, ip);
    thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(), thumbnail, fileEntity,
        "large");

    if (thumbnailEntity == null) {
      throw new CustomAboutFailedException();
    }

    bookDto.setThumbnailId(thumbnailEntity.getId());

    return bookManageService.doAdd(bookDto, thumbnail, ip);
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
