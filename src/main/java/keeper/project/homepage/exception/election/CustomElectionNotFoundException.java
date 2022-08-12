package keeper.project.homepage.exception.election;

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
