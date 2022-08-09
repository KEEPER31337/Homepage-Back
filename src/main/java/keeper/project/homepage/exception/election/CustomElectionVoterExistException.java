package keeper.project.homepage.exception.election;

public class CustomElectionVoterExistException extends RuntimeException {

  public CustomElectionVoterExistException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomElectionVoterExistException(String msg) {
    super(msg);
  }

  public CustomElectionVoterExistException() {
    super();
  }

}
