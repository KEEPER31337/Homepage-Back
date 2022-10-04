package keeper.project.homepage.posting.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.posting"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PostingExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomCommentNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult commentNotFoundException(CustomCommentNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("commentNotFound.code")),
        exceptionUtil.getMessage("commentNotFound.msg"));
  }

  @ExceptionHandler(CustomCommentEmptyFieldException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult commentEmptyFieldException(CustomCommentEmptyFieldException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("commentEmptyField.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("commentEmptyField.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomCategoryNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult categoryNotFoundException(CustomCategoryNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("categoryNotFound.code")),
        exceptionUtil.getMessage("categoryNotFound.msg"));
  }

  @ExceptionHandler(CustomAccessRootCategoryException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult accessRootCategoryException(CustomAccessRootCategoryException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("accessRootCategory.code")),
        exceptionUtil.getMessage("accessRootCategory.msg"));
  }

  @ExceptionHandler(CustomPostingNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingNotFound(CustomPostingNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("postingNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("postingNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingIncorrectException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingIncorrect(CustomPostingIncorrectException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("postingIncorrect.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("postingIncorrect.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingTempException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingTemp(CustomPostingTempException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("postingTemp.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("postingTemp.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingAccessDeniedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingAccessDenied(CustomPostingAccessDeniedException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("postingAccessDenied.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("postingAccessDenied.msg")
            : e.getMessage());
  }
}
