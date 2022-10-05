package keeper.project.homepage.clerk.exception;


import keeper.project.homepage.about.exception.CustomStaticWriteContentNotFoundException;
import keeper.project.homepage.about.exception.CustomStaticWriteSubtitleImageNotFoundException;
import keeper.project.homepage.about.exception.CustomStaticWriteTitleNotFoundException;
import keeper.project.homepage.about.exception.CustomStaticWriteTypeNotFoundException;
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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.clerk"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClerkExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomSeminarAttendanceFailException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult seminarAttendanceFail(CustomSeminarAttendanceFailException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("seminarAttendanceFail.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("seminarAttendanceFail.msg")
            : e.getMessage());
  }
}
