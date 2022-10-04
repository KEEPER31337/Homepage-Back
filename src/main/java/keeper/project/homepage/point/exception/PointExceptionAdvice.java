package keeper.project.homepage.point.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.point"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PointExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomPointLackException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CommonResult transferPointLackException(CustomPointLackException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("pointLackException.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("pointLackException.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomPointLogRequestNullException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult pointLogRequestNullException(CustomPointLogRequestNullException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("pointLogRequestNullException.code")),
        exceptionUtil.getMessage("pointLogRequestNullException.msg"));
  }
}
