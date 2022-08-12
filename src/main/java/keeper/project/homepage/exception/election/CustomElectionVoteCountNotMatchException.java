package keeper.project.homepage.exception.election;

public class CustomElectionVoteCountNotMatchException extends RuntimeException {

  public CustomElectionVoteCountNotMatchException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomElectionVoteCountNotMatchException(String msg) {
    super(msg);
  }

  public CustomElectionVoteCountNotMatchException() {
    super();
  }

}
