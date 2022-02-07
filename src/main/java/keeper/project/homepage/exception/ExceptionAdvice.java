package keeper.project.homepage.exception;

import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.service.ResponseService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
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
  @ExceptionHandler(CustomLoginIdSigninFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult signInFailed(HttpServletRequest request,
      CustomLoginIdSigninFailedException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("SigninFailed.code")),
        e.getMessage() == null ? getMessage("SigninFailed.msg") : e.getMessage());
  }

  @ExceptionHandler(keeper.project.homepage.exception.CustomAuthenticationEntryPointException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CommonResult authenticationEntryPointException(HttpServletRequest request,
      keeper.project.homepage.exception.CustomAuthenticationEntryPointException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("entryPointException.code")),
        getMessage("entryPointException.msg"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public CommonResult accessDeniedException(HttpServletRequest request, AccessDeniedException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("accessDenied.code")),
        getMessage("accessDenied.msg"));
  }

  @ExceptionHandler(CustomSignUpFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult signUpFailedException(HttpServletRequest request,
      CustomSignUpFailedException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("signUpFailed.code")),
        e.getMessage() == null ? getMessage("signUpFailed.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomAboutFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult aboutFailedException(HttpServletRequest request,
      CustomAboutFailedException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("aboutFailed.code")),
        e.getMessage() == null ? getMessage("aboutFailed.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomFileNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult signUpFailedException(HttpServletRequest request,
      CustomFileNotFoundException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("fileNotFound.code")),
        e.getMessage() == null ? getMessage("fileNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomAttendanceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult attendanceException(HttpServletRequest request,
      CustomAttendanceException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("attendanceFailed.code")),
        e.getMessage() == null ? getMessage("attendanceFailed.msg") : e.getMessage());
  
  @ExceptionHandler(CustomBookNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult bookNotFoundException(HttpServletRequest request,
      CustomBookNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("bookNotFound.code")),
        getMessage("bookNotFound.msg"));
  }

  @ExceptionHandler(CustomBookOverTheMaxException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult bookOverTheMaxException(HttpServletRequest request,
      CustomBookOverTheMaxException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("bookOverTheMax.code")),
        getMessage("bookOverTheMax.msg"));
  }
}