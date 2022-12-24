package keeper.project.homepage.attendance.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.attendance"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AttendanceExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomAttendanceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult attendanceException(CustomAttendanceException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("attendanceFailed.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("attendanceFailed.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomGameIsOverException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult gameIsOverException(CustomGameIsOverException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("gameIsOver.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("gameIsOver.msg") : e.getMessage());
  }
}
