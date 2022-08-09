package keeper.project.homepage.exception.election;

public class CustomElectionAlreadyVotedException extends RuntimeException {

  public CustomElectionAlreadyVotedException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomElectionAlreadyVotedException(String msg) {
    super(msg);
  }

  public CustomElectionAlreadyVotedException() {
    super();
  }

}
