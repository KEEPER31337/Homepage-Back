package keeper.project.homepage.systemadmin.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.systemadmin"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SystemAdminExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomClerkInaccessibleJobException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult clerkInaccessibleJob(CustomClerkInaccessibleJobException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("inaccessibleJob.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("inaccessibleJob.msg") : e.getMessage());
  }
}
