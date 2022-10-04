package keeper.project.homepage.election.exception;


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
@RestControllerAdvice(basePackages = {"keeper.project.homepage.election"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ElectionExceptionAdvice {

  private final ResponseService responseService;
  private final ExceptionAdviceUtil exceptionUtil;

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
}
