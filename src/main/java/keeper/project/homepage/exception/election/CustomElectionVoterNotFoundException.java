package keeper.project.homepage.exception.election;

public class CustomElectionVoterNotFoundException extends RuntimeException {

  public CustomElectionVoterNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomElectionVoterNotFoundException(String msg) {
    super(msg);
  }

  public CustomElectionVoterNotFoundException() {
    super();
  }

}
