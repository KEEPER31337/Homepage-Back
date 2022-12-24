package keeper.project.homepage.about.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.about"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AboutExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomStaticWriteTypeNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult staticWriteTitleNotFound(CustomStaticWriteTypeNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("staticWriteTypeNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("staticWriteTypeNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomStaticWriteTitleNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult staticWriteTitleNotFound(CustomStaticWriteTitleNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("staticWriteTitleNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("staticWriteTitleNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomStaticWriteSubtitleImageNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult staticWriteSubtitleImageNotFound(
      CustomStaticWriteSubtitleImageNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("staticWriteSubtitleImageNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("staticWriteSubtitleImageNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomStaticWriteContentNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult staticWriteContentNotFound(CustomStaticWriteContentNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("staticWriteContentNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("staticWriteContentNotFound.msg")
            : e.getMessage());
  }
}
