package keeper.project.homepage.election.exception;

public class CustomElectionNotFoundException extends RuntimeException {

  public CustomElectionNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomElectionNotFoundException(String msg) {
    super(msg);
  }

  public CustomElectionNotFoundException() {
    super();
  }

}
