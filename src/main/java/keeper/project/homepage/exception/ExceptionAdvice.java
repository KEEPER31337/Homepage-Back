package keeper.project.homepage.exception;

import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.exception.attendance.CustomAttendanceException;
import keeper.project.homepage.exception.attendance.CustomGameIsOverException;
import keeper.project.homepage.exception.ctf.CustomContestNotFoundException;
import keeper.project.homepage.exception.file.CustomFileDeleteFailedException;
import keeper.project.homepage.exception.file.CustomFileEntityNotFoundException;
import keeper.project.homepage.exception.file.CustomFileNotFoundException;
import keeper.project.homepage.exception.file.CustomFileTransferFailedException;
import keeper.project.homepage.exception.file.CustomImageFormatException;
import keeper.project.homepage.exception.file.CustomImageIOException;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.exception.library.CustomBookBorrowNotFoundException;
import keeper.project.homepage.exception.library.CustomBookDepartmentNotFoundException;
import keeper.project.homepage.exception.library.CustomBookNotFoundException;
import keeper.project.homepage.exception.library.CustomBookOverTheMaxException;
import keeper.project.homepage.exception.member.CustomAccessVirtualMemberException;
import keeper.project.homepage.exception.member.CustomAccountDeleteFailedException;
import keeper.project.homepage.exception.member.CustomMemberDuplicateException;
import keeper.project.homepage.exception.member.CustomMemberEmptyFieldException;
import keeper.project.homepage.exception.member.CustomMemberInfoNotFoundException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.exception.point.CustomPointLogRequestNullException;
import keeper.project.homepage.exception.point.CustomPointLackException;
import keeper.project.homepage.exception.posting.CustomAccessRootCategoryException;
import keeper.project.homepage.exception.posting.CustomCategoryNotFoundException;
import keeper.project.homepage.exception.posting.CustomCommentEmptyFieldException;
import keeper.project.homepage.exception.posting.CustomCommentNotFoundException;
import keeper.project.homepage.exception.posting.CustomPostingAccessDeniedException;
import keeper.project.homepage.exception.posting.CustomPostingIncorrectException;
import keeper.project.homepage.exception.posting.CustomPostingNotFoundException;
import keeper.project.homepage.exception.posting.CustomPostingTempException;
import keeper.project.homepage.exception.sign.CustomAuthenticationEntryPointException;
import keeper.project.homepage.exception.sign.CustomLoginIdSigninFailedException;
import keeper.project.homepage.exception.sign.CustomSignUpFailedException;
import keeper.project.homepage.common.service.ResponseService;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.exception.study.CustomIpAddressNotFoundException;
import keeper.project.homepage.exception.study.CustomSeasonInvalidException;
import keeper.project.homepage.exception.study.CustomStudyIsNotMineException;
import keeper.project.homepage.exception.study.CustomStudyNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
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
        e.getMessage() == null ? getMessage("memberNotFound.msg") : e.getMessage());
  }

  // code정보에 해당하는 메시지를 조회합니다.
  public String getMessage(String code) {
    return getMessage(code, null);
  }

  // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
  public String getMessage(String code, Object[] args) {
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

  @ExceptionHandler(CustomAuthenticationEntryPointException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CommonResult authenticationEntryPointException(HttpServletRequest request,
      CustomAuthenticationEntryPointException e) {
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
  public CommonResult fileNotFoundException(HttpServletRequest request,
      CustomFileNotFoundException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("fileNotFound.code")),
        e.getMessage() == null ? getMessage("fileNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPointLackException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CommonResult transferPointLackException(HttpServletRequest request,
      CustomPointLackException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("pointLackException.code")),
        e.getMessage() == null ? getMessage("pointLackException.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomAttendanceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult attendanceException(HttpServletRequest request,
      CustomAttendanceException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("attendanceFailed.code")),
        e.getMessage() == null ? getMessage("attendanceFailed.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomGameIsOverException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult gameIsOverException(HttpServletRequest request, CustomGameIsOverException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("gameIsOver.code")),
        e.getMessage() == null ? getMessage("gameIsOver.msg") : e.getMessage());
  }

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

  @ExceptionHandler(CustomFileDeleteFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult fileDeleteFailedException(HttpServletRequest request,
      CustomFileDeleteFailedException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("fileDeleteFailed.code")),
        getMessage("fileDeleteFailed.msg"));
  }

  @ExceptionHandler(CustomFileTransferFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult fileTransferFailedException(HttpServletRequest request,
      CustomFileTransferFailedException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("fileTransferFailed.code")),
        getMessage("fileTransferFailed.msg"));
  }

  @ExceptionHandler(CustomFileEntityNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult fileEntityNotFoundException(HttpServletRequest request,
      CustomFileEntityNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(getMessage("fileEntityNotFoundFailed.code")),
        getMessage("fileEntityNotFoundFailed.msg"));
  }

  @ExceptionHandler(CustomThumbnailEntityNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult CustomThumbnailEntityNotFoundException(HttpServletRequest request,
      CustomThumbnailEntityNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(getMessage("thumbnailEntityNotFoundFailed.code")),
        getMessage("thumbnailEntityNotFoundFailed.msg"));
  }

  @ExceptionHandler(CustomImageFormatException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult imageFormatException(HttpServletRequest request,
      CustomImageFormatException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("imageFormat.code")),
        getMessage("imageFormat.msg"));
  }

  @ExceptionHandler(CustomImageIOException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult imageIOException(HttpServletRequest request,
      CustomImageIOException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("imageIO.code")),
        getMessage("imageIO.msg"));
  }

  @ExceptionHandler(CustomMemberEmptyFieldException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult memberEmptyFieldException(HttpServletRequest request,
      CustomMemberEmptyFieldException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("memberEmptyField.code")),
        getMessage("memberEmptyField.msg"));
  }

  @ExceptionHandler(CustomMemberInfoNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult memberInfoNotFoundException(HttpServletRequest request,
      CustomMemberInfoNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("memberInfoNotFound.code")),
        getMessage("memberInfoNotFound.msg"));
  }

  @ExceptionHandler(CustomMemberDuplicateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult memberDuplicateException(HttpServletRequest request,
      CustomMemberDuplicateException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("memberDuplicate.code")),
        e.getMessage() == null ? getMessage("memberDuplicate.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomCommentNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult commentNotFoundException(HttpServletRequest request,
      CustomCommentNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("commentNotFound.code")),
        getMessage("commentNotFound.msg"));
  }

  @ExceptionHandler(CustomAccountDeleteFailedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult accountDeleteFailedException(HttpServletRequest request,
      CustomAccountDeleteFailedException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("accountDeleteFailed.code")),
        getMessage("accountDeleteFailed.msg"));
  }

  @ExceptionHandler(CustomCommentEmptyFieldException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult commentEmptyFieldException(HttpServletRequest request,
      CustomCommentEmptyFieldException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("commentEmptyField.code")),
        getMessage("commentEmptyField.msg"));
  }

  @ExceptionHandler(CustomNumberOverflowException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult numberOverflowException(HttpServletRequest request,
      CustomNumberOverflowException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("numberOverflow.code")),
        getMessage("numberOverflow.msg"));
  }

  @ExceptionHandler(CustomCategoryNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult categoryNotFoundException(HttpServletRequest request,
      CustomCategoryNotFoundException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("categoryNotFound.code")),
        getMessage("categoryNotFound.msg"));
  }

  @ExceptionHandler(CustomAccessRootCategoryException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult accessRootCategoryException(HttpServletRequest request,
      CustomAccessRootCategoryException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("accessRootCategory.code")),
        getMessage("accessRootCategory.msg"));
  }

  @ExceptionHandler(CustomBookBorrowNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult bookBorrowNotFoundException(HttpServletRequest request,
      CustomBookBorrowNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(getMessage("bookBorrowNotFound.code")),
        getMessage("bookBorrowNotFound.msg"));
  }

  @ExceptionHandler(CustomPointLogRequestNullException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult pointLogRequestNullException(HttpServletRequest request,
      CustomPointLogRequestNullException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(getMessage("pointLogRequestNullException.code")),
        getMessage("pointLogRequestNullException.msg"));
  }

  @ExceptionHandler(CustomBookDepartmentNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult bookDepartmentNotFoundException(HttpServletRequest request,
      CustomBookDepartmentNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(getMessage("bookDepartmentNotFound.code")),
        getMessage("bookDepartmentNotFound.msg"));
  }

  @ExceptionHandler(CustomSeasonInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult seasonInvalid(HttpServletRequest request,
      CustomSeasonInvalidException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("seasonInvalid.code")),
        e.getMessage() == null ? getMessage("seasonInvalid.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomIpAddressNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult ipAddressNotFound(HttpServletRequest request,
      CustomIpAddressNotFoundException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("ipAddressNotFound.code")),
        e.getMessage() == null ? getMessage("ipAddressNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomStudyNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult studyNotFound(HttpServletRequest request,
      CustomStudyNotFoundException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("studyNotFound.code")),
        e.getMessage() == null ? getMessage("studyNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomStudyIsNotMineException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult studyNotMine(HttpServletRequest request,
      CustomStudyIsNotMineException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("studyNotMine.code")),
        e.getMessage() == null ? getMessage("studyNotMine.msg") : e.getMessage());
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult dataAccessException(HttpServletRequest request,
      EmptyResultDataAccessException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("dataNotFound.code")),
        e.getMessage() == null ? getMessage("dataNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomAccessVirtualMemberException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult accessVirtualMember(HttpServletRequest request,
      CustomAccessVirtualMemberException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("accessVirtualMember.code")),
        e.getMessage() == null ? getMessage("accessVirtualMember.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingNotFound(HttpServletRequest request,
      CustomPostingNotFoundException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("postingNotFound.code")),
        e.getMessage() == null ? getMessage("postingNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingIncorrectException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingIncorrect(HttpServletRequest request,
      CustomPostingIncorrectException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("postingIncorrect.code")),
        e.getMessage() == null ? getMessage("postingIncorrect.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingTempException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingTemp(HttpServletRequest request,
      CustomPostingTempException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("postingTemp.code")),
        e.getMessage() == null ? getMessage("postingTemp.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingAccessDeniedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingAccessDenied(HttpServletRequest request,
      CustomPostingAccessDeniedException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("postingAccessDenied.code")),
        e.getMessage() == null ? getMessage("postingAccessDenied.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomContestNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingAccessDenied(HttpServletRequest request,
      CustomContestNotFoundException e) {
    return responseService.getFailResult(Integer.parseInt(getMessage("contestNotFound.code")),
        e.getMessage() == null ? getMessage("contestNotFound.msg") : e.getMessage());
  }
}