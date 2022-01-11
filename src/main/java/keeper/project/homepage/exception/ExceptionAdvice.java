package keeper.project.homepage.exception;

import keeper.project.homepage.dto.CommonResult;
import keeper.project.homepage.service.ResponseService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

  private final ResponseService responseService;

  private final MessageSource messageSource;

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult defaultException(HttpServletRequest request, Exception e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("unKnown.code")),
        getMessage("unKnown.msg"));
  }

  @ExceptionHandler(CustomMemberNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult memberNotFoundException(HttpServletRequest request,
      CustomMemberNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("memberNotFound.code")),
        getMessage("memberNotFound.msg"));
  }

  // code정보에 해당하는 메시지를 조회합니다.
  private String getMessage(String code) {
    return getMessage(code, null);
  }

  // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
  private String getMessage(String code, Object[] args) {
    return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
  }

  // ExceptionAdvice
  @ExceptionHandler(keeper.project.homepage.exception.CustomEmailSigninFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult emailSigninFailed(HttpServletRequest request,
      keeper.project.homepage.exception.CustomEmailSigninFailedException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("emailSigninFailed.code")),
        getMessage("emailSigninFailed.msg"));
  }

  @ExceptionHandler(keeper.project.homepage.exception.CustomAuthenticationEntryPointException.class)
  public CommonResult authenticationEntryPointException(HttpServletRequest request,
      keeper.project.homepage.exception.CustomAuthenticationEntryPointException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("entryPointException.code")),
        getMessage("entryPointException.msg"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public CommonResult AccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("accessDenied.code")),
        getMessage("accessDenied.msg"));
  }
}