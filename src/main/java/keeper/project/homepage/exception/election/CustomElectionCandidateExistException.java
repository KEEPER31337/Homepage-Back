package keeper.project.homepage.exception.election;

public class CustomElectionCandidateExistException extends RuntimeException {

  public CustomElectionCandidateExistException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomElectionCandidateExistException(String msg) {
    super(msg);
  }

  public CustomElectionCandidateExistException() {
    super();
  }

}
