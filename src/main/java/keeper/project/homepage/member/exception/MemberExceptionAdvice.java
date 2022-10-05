package keeper.project.homepage.member.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.member"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MemberExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(CustomMemberEmptyFieldException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult memberEmptyFieldException(CustomMemberEmptyFieldException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("memberEmptyField.code")),
        exceptionUtil.getMessage("memberEmptyField.msg"));
  }

  @ExceptionHandler(CustomMemberDuplicateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult memberDuplicateException(CustomMemberDuplicateException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("memberDuplicate.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("memberDuplicate.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomAccessVirtualMemberException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult accessVirtualMember(CustomAccessVirtualMemberException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("accessVirtualMember.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("accessVirtualMember.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomAccountDeleteFailedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult accountDeleteFailedException(CustomAccountDeleteFailedException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("accountDeleteFailed.code")),
        exceptionUtil.getMessage("accountDeleteFailed.msg"));
  }
}
