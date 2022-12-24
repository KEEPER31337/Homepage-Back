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

  @ExceptionHandler(CustomAttendanceAbsenceExcuseIsNullException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult absenceExcuseIsNull(CustomAttendanceAbsenceExcuseIsNullException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("absenceExcuseIsNull.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("absenceExcuseIsNull.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomDuplicateAbsenceLogException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult duplicatedAbsenceLog(CustomDuplicateAbsenceLogException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("duplicatedAbsenceLog.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("duplicatedAbsenceLog.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomDuplicateSeminarException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult duplicatedSeminar(CustomDuplicateSeminarException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("duplicatedSeminar.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("duplicatedSeminar.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomSeminarAttendanceNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult seminarAttendanceNotfound(CustomSeminarAttendanceNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("seminarAttendanceNotfound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("seminarAttendanceNotfound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomSeminarNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult seminarNotfound(CustomSeminarNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("seminarNotfound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("seminarNotfound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomSeminarAttendanceStatusNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult seminarAttendanceStatusNotfound(
      CustomSeminarAttendanceStatusNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("seminarAttendanceStatusNotfound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("seminarAttendanceStatusNotfound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomMeritLogNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult meritLogNotFound(CustomMeritLogNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("meritLogNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("meritLogNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomMeritTypeNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult meritTypeNotFound(CustomMeritTypeNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("meritTypeNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("meritTypeNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomSurveyInVisibleException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult surveyInvisible(CustomSurveyInVisibleException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("surveyInvisible.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("surveyInvisible.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomSurveyMemberReplyNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult surveyInvisible(CustomSurveyMemberReplyNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("surveyMemberReplyNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("surveyMemberReplyNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomSurveyNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult surveyNotFound(CustomSurveyNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("surveyNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("surveyNotFound.msg")
            : e.getMessage());
  }
}
