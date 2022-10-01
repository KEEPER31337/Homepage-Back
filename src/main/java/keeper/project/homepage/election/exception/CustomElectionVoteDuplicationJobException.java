package keeper.project.homepage.election.exception;

public class CustomElectionVoteDuplicationJobException extends RuntimeException {

  public CustomElectionVoteDuplicationJobException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomElectionVoteDuplicationJobException(String msg) {
    super(msg);
  }

  public CustomElectionVoteDuplicationJobException() {
    super();
  }

}
