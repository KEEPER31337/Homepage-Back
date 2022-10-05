package keeper.project.homepage.ctf.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.ctf"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CtfExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomContestNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult contestNotFound(CustomContestNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("contestNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("contestNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomCtfCategoryNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult ctfCategoryNotFound(CustomCtfCategoryNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ctfCategoryNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ctfCategoryNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomCtfTypeNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult ctfTypeNotFound(CustomCtfTypeNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ctfTypeNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ctfTypeNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomCtfChallengeNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult ctfChallengeNotFound(CustomCtfChallengeNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ctfChallengeNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ctfChallengeNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomCtfTeamNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult teamNotFound(CustomCtfTeamNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ctfTeamNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ctfTeamNotFound.msg") : e.getMessage());
  }
}
