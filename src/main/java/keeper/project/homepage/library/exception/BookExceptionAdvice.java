package keeper.project.homepage.library.exception;


import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.util.exception.ExceptionAdviceUtil;
import keeper.project.homepage.util.service.result.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"keeper.project.homepage.library"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BookExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomBookNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult bookNotFoundException(CustomBookNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("bookNotFound.code")),
        exceptionUtil.getMessage("bookNotFound.msg"));
  }

  @ExceptionHandler(CustomBookOverTheMaxException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult bookOverTheMaxException(CustomBookOverTheMaxException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("bookOverTheMax.code")),
        exceptionUtil.getMessage("bookOverTheMax.msg"));
  }

  @ExceptionHandler(CustomBookBorrowNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult bookBorrowNotFoundException(CustomBookBorrowNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("bookBorrowNotFound.code")),
        exceptionUtil.getMessage("bookBorrowNotFound.msg"));
  }

  @ExceptionHandler(CustomBookDepartmentNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult bookDepartmentNotFoundException(CustomBookDepartmentNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("bookDepartmentNotFound.code")),
        exceptionUtil.getMessage("bookDepartmentNotFound.msg"));
  }
}
