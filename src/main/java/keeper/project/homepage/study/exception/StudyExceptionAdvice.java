package keeper.project.homepage.study.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.study"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StudyExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomSeasonInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult seasonInvalid(CustomSeasonInvalidException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("seasonInvalid.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("seasonInvalid.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomIpAddressNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult ipAddressNotFound(CustomIpAddressNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ipAddressNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ipAddressNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomStudyNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult studyNotFound(CustomStudyNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("studyNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("studyNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomStudyIsNotMineException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult studyNotMine(CustomStudyIsNotMineException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("studyNotMine.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("studyNotMine.msg") : e.getMessage());
  }
}
