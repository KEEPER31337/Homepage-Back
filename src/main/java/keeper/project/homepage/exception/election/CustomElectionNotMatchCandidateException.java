package keeper.project.homepage.exception.election;

public class CustomElectionNotMatchCandidateException extends RuntimeException {

  public CustomElectionNotMatchCandidateException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomElectionNotMatchCandidateException(String msg) {
    super(msg);
  }

  public CustomElectionNotMatchCandidateException() {
    super();
  }

}
