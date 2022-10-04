package keeper.project.homepage;

import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.about.exception.CustomStaticWriteContentNotFoundException;
import keeper.project.homepage.about.exception.CustomStaticWriteSubtitleImageNotFoundException;
import keeper.project.homepage.about.exception.CustomStaticWriteTitleNotFoundException;
import keeper.project.homepage.about.exception.CustomStaticWriteTypeNotFoundException;
import keeper.project.homepage.attendance.exception.CustomAttendanceException;
import keeper.project.homepage.attendance.exception.CustomGameIsOverException;
import keeper.project.homepage.clerk.exception.CustomClerkInaccessibleJobException;
import keeper.project.homepage.clerk.exception.CustomSeminarAttendanceFailException;
import keeper.project.homepage.ctf.exception.CustomContestNotFoundException;
import keeper.project.homepage.ctf.exception.CustomCtfCategoryNotFoundException;
import keeper.project.homepage.ctf.exception.CustomCtfChallengeNotFoundException;
import keeper.project.homepage.ctf.exception.CustomCtfTypeNotFoundException;
import keeper.project.homepage.election.exception.CustomCloseElectionVoteException;
import keeper.project.homepage.election.exception.CustomElectionAlreadyVotedException;
import keeper.project.homepage.election.exception.CustomElectionIsNotClosedException;
import keeper.project.homepage.election.exception.CustomElectionNotMatchCandidateException;
import keeper.project.homepage.election.exception.CustomElectionVoteCountNotMatchException;
import keeper.project.homepage.election.exception.CustomElectionCandidateExistException;
import keeper.project.homepage.election.exception.CustomElectionCandidateNotFoundException;
import keeper.project.homepage.election.exception.CustomElectionNotFoundException;
import keeper.project.homepage.election.exception.CustomElectionVoteDuplicationJobException;
import keeper.project.homepage.election.exception.CustomElectionVoterExistException;
import keeper.project.homepage.election.exception.CustomElectionVoterNotFoundException;
import keeper.project.homepage.util.exception.CustomNumberOverflowException;
import keeper.project.homepage.util.exception.ExceptionAdviceUtil;
import keeper.project.homepage.util.exception.file.CustomInvalidImageFileException;
import keeper.project.homepage.ctf.exception.CustomCtfTeamNotFoundException;
import keeper.project.homepage.util.exception.file.CustomFileDeleteFailedException;
import keeper.project.homepage.util.exception.file.CustomFileEntityNotFoundException;
import keeper.project.homepage.util.exception.file.CustomFileNotFoundException;
import keeper.project.homepage.util.exception.file.CustomFileTransferFailedException;
import keeper.project.homepage.util.exception.file.CustomImageFormatException;
import keeper.project.homepage.util.exception.file.CustomImageIOException;
import keeper.project.homepage.util.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.library.exception.CustomBookBorrowNotFoundException;
import keeper.project.homepage.library.exception.CustomBookDepartmentNotFoundException;
import keeper.project.homepage.library.exception.CustomBookNotFoundException;
import keeper.project.homepage.library.exception.CustomBookOverTheMaxException;
import keeper.project.homepage.member.exception.CustomAccessVirtualMemberException;
import keeper.project.homepage.member.exception.CustomAccountDeleteFailedException;
import keeper.project.homepage.member.exception.CustomMemberDuplicateException;
import keeper.project.homepage.member.exception.CustomMemberEmptyFieldException;
import keeper.project.homepage.member.exception.CustomMemberInfoNotFoundException;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.point.exception.CustomPointLogRequestNullException;
import keeper.project.homepage.point.exception.CustomPointLackException;
import keeper.project.homepage.posting.exception.CustomAccessRootCategoryException;
import keeper.project.homepage.posting.exception.CustomCategoryNotFoundException;
import keeper.project.homepage.posting.exception.CustomCommentEmptyFieldException;
import keeper.project.homepage.posting.exception.CustomCommentNotFoundException;
import keeper.project.homepage.posting.exception.CustomPostingAccessDeniedException;
import keeper.project.homepage.posting.exception.CustomPostingIncorrectException;
import keeper.project.homepage.posting.exception.CustomPostingNotFoundException;
import keeper.project.homepage.posting.exception.CustomPostingTempException;
import keeper.project.homepage.sign.exception.CustomAuthenticationEntryPointException;
import keeper.project.homepage.sign.exception.CustomLoginIdSigninFailedException;
import keeper.project.homepage.sign.exception.CustomSignUpFailedException;
import keeper.project.homepage.util.service.result.ResponseService;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.study.exception.CustomIpAddressNotFoundException;
import keeper.project.homepage.study.exception.CustomSeasonInvalidException;
import keeper.project.homepage.study.exception.CustomStudyIsNotMineException;
import keeper.project.homepage.study.exception.CustomStudyNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult defaultException(Exception e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(exceptionUtil.getMessage("unKnown.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("unKnown.msg") : e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();
    StringBuilder errorMessage = new StringBuilder();
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      errorMessage.append("[");
      errorMessage.append(fieldError.getField());
      errorMessage.append("] 입력: ");
      errorMessage.append(fieldError.getRejectedValue()).append(" / ");
      errorMessage.append(fieldError.getDefaultMessage()).append(" ");

    }
    return responseService.getFailResult(HttpStatus.BAD_REQUEST.value(),
        errorMessage.toString().trim());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult methodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("argumentTypeMismatch.code")),
        exceptionUtil.getMessage("argumentTypeMismatch.msg"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public CommonResult accessDeniedException(AccessDeniedException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("accessDenied.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("accessDenied.msg") : e.getMessage());
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult methodArgumentTypeMismatchException(
      MissingServletRequestParameterException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("missingServletRequestParameter.code")),
        exceptionUtil.getMessage("missingServletRequestParameter.msg"));
  }

  @ExceptionHandler(CustomMemberNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult memberNotFoundException(CustomMemberNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("memberNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("memberNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomMemberInfoNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult memberInfoNotFoundException(CustomMemberInfoNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("memberInfoNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("memberInfoNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomFileNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResult fileNotFoundException(CustomFileNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("fileNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("fileNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomFileDeleteFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult fileDeleteFailedException(CustomFileDeleteFailedException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("fileDeleteFailed.code")),
        exceptionUtil.getMessage("fileDeleteFailed.msg"));
  }

  @ExceptionHandler(CustomFileTransferFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult fileTransferFailedException(CustomFileTransferFailedException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("fileTransferFailed.code")),
        exceptionUtil.getMessage("fileTransferFailed.msg"));
  }

  @ExceptionHandler(CustomFileEntityNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult fileEntityNotFoundException(CustomFileEntityNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("fileEntityNotFoundFailed.code")),
        exceptionUtil.getMessage("fileEntityNotFoundFailed.msg"));
  }

  @ExceptionHandler(CustomThumbnailEntityNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult CustomThumbnailEntityNotFoundException(
      CustomThumbnailEntityNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("thumbnailEntityNotFoundFailed.code")),
        exceptionUtil.getMessage("thumbnailEntityNotFoundFailed.msg"));
  }

  @ExceptionHandler(CustomImageFormatException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult imageFormatException(CustomImageFormatException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("imageFormat.code")),
        exceptionUtil.getMessage("imageFormat.msg"));
  }

  @ExceptionHandler(CustomImageIOException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult imageIOException(CustomImageIOException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(Integer.parseInt(exceptionUtil.getMessage("imageIO.code")),
        exceptionUtil.getMessage("imageIO.msg"));
  }

  @ExceptionHandler(CustomInvalidImageFileException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CommonResult invalidImageFileException(CustomInvalidImageFileException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("invalidImageFile.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("invalidImageFile.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomCommentNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult commentNotFoundException(CustomCommentNotFoundException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("commentNotFound.code")),
        exceptionUtil.getMessage("commentNotFound.msg"));
  }

  @ExceptionHandler(CustomCommentEmptyFieldException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult commentEmptyFieldException(CustomCommentEmptyFieldException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("commentEmptyField.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("commentEmptyField.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomNumberOverflowException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult numberOverflowException(CustomNumberOverflowException e) {
    // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("numberOverflow.code")),
        exceptionUtil.getMessage("numberOverflow.msg"));
  }

  @ExceptionHandler(CustomCategoryNotFoundException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult categoryNotFoundException(CustomCategoryNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("categoryNotFound.code")),
        exceptionUtil.getMessage("categoryNotFound.msg"));
  }

  @ExceptionHandler(CustomAccessRootCategoryException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected CommonResult accessRootCategoryException(CustomAccessRootCategoryException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("accessRootCategory.code")),
        exceptionUtil.getMessage("accessRootCategory.msg"));
  }

  @ExceptionHandler(CustomSeasonInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult seasonInvalid(CustomSeasonInvalidException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("seasonInvalid.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("seasonInvalid.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomIpAddressNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult ipAddressNotFound(CustomIpAddressNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ipAddressNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ipAddressNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomStudyNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult studyNotFound(CustomStudyNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("studyNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("studyNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomStudyIsNotMineException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult studyNotMine(CustomStudyIsNotMineException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("studyNotMine.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("studyNotMine.msg") : e.getMessage());
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult dataAccessException(EmptyResultDataAccessException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("dataNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("dataNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingNotFound(CustomPostingNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("postingNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("postingNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingIncorrectException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingIncorrect(CustomPostingIncorrectException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("postingIncorrect.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("postingIncorrect.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingTempException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingTemp(CustomPostingTempException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("postingTemp.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("postingTemp.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomPostingAccessDeniedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult postingAccessDenied(CustomPostingAccessDeniedException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("postingAccessDenied.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("postingAccessDenied.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomStaticWriteTypeNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult staticWriteTitleNotFound(CustomStaticWriteTypeNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("staticWriteTypeNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("staticWriteTypeNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomStaticWriteTitleNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult staticWriteTitleNotFound(CustomStaticWriteTitleNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("staticWriteTitleNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("staticWriteTitleNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomStaticWriteSubtitleImageNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult staticWriteSubtitleImageNotFound(
      CustomStaticWriteSubtitleImageNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("staticWriteSubtitleImageNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("staticWriteSubtitleImageNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomStaticWriteContentNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult staticWriteContentNotFound(CustomStaticWriteContentNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("staticWriteContentNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("staticWriteContentNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomContestNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult contestNotFound(CustomContestNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("contestNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("contestNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomCtfCategoryNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult ctfCategoryNotFound(CustomCtfCategoryNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ctfCategoryNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ctfCategoryNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomCtfTypeNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult ctfTypeNotFound(CustomCtfTypeNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ctfTypeNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ctfTypeNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomCtfChallengeNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult ctfChallengeNotFound(CustomCtfChallengeNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ctfChallengeNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ctfChallengeNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomCtfTeamNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult teamNotFound(CustomCtfTeamNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("ctfTeamNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("ctfTeamNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  protected CommonResult dataDuplicate(DataIntegrityViolationException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("dataDuplicate.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("dataDuplicate.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomElectionNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionNotFound(CustomElectionNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("electionNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("electionNotFound.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomElectionCandidateNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionCandidateNotFound(CustomElectionCandidateNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("electionCandidateNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("electionCandidateNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomElectionVoterNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionVoterNotFound(CustomElectionVoterNotFoundException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("electionVoterNotFound.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("electionVoterNotFound.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomElectionCandidateExistException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionCandidateExist(CustomElectionCandidateExistException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("electionCandidateExist.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("electionCandidateExist.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomElectionVoterExistException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionVoterExist(CustomElectionVoterExistException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("electionVoterExist.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("electionVoterExist.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomElectionVoteCountNotMatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionCandidateCountNotMatch(
      CustomElectionVoteCountNotMatchException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("electionVoteCountNotMatch.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("electionVoteCountNotMatch.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomElectionVoteDuplicationJobException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionVoteDuplicationJob(CustomElectionVoteDuplicationJobException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("electionVoteDuplicationJob.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("electionVoteDuplicationJob.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomCloseElectionVoteException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult closeElectionVote(CustomCloseElectionVoteException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("closeElectionVote.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("closeElectionVote.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomElectionNotMatchCandidateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionNotMatchCandidate(CustomElectionNotMatchCandidateException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("electionNotMatchCandidate.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("electionNotMatchCandidate.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomElectionAlreadyVotedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionAlreadyVoted(CustomElectionAlreadyVotedException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("alreadyVoted.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("alreadyVoted.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomElectionIsNotClosedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult electionIsNotClosed(CustomElectionIsNotClosedException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("IsNotClosedElection.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("IsNotClosedElection.msg")
            : e.getMessage());
  }

  @ExceptionHandler(CustomClerkInaccessibleJobException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult clerkInaccessibleJob(CustomClerkInaccessibleJobException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("inaccessibleJob.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("inaccessibleJob.msg") : e.getMessage());
  }

  @ExceptionHandler(CustomSeminarAttendanceFailException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected CommonResult seminarAttendanceFail(CustomSeminarAttendanceFailException e) {
    return responseService.getFailResult(
        Integer.parseInt(exceptionUtil.getMessage("seminarAttendanceFail.code")),
        e.getMessage() == null ? exceptionUtil.getMessage("seminarAttendanceFail.msg")
            : e.getMessage());
  }

}