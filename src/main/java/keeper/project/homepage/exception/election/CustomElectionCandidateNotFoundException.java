package keeper.project.homepage.exception.election;

public class CustomElectionCandidateNotFoundException extends RuntimeException {

  public CustomElectionCandidateNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomElectionCandidateNotFoundException(String msg) {
    super(msg);
  }

  public CustomElectionCandidateNotFoundException() {
    super();
  }

}
