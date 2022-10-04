package keeper.project.homepage.sign.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.sign"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SignExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomLoginIdSigninFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult signInFailed(CustomLoginIdSigninFailedException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("SigninFailed.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("SigninFailed.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomAuthenticationEntryPointException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CommonResult authenticationEntryPointException(CustomAuthenticationEntryPointException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("entryPointException.code")),
        exceptionUtil.getMessage("entryPointException.msg"));
  }

  @ExceptionHandler(CustomSignUpFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult signUpFailedException(CustomSignUpFailedException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("signUpFailed.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("signUpFailed.msg") : e.getMessage());
  }
}
