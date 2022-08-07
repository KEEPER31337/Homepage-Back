package keeper.project.homepage.exception.election;

public class CustomCloseElectionVoteException extends RuntimeException {

  public CustomCloseElectionVoteException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomCloseElectionVoteException(String msg) {
    super(msg);
  }

  public CustomCloseElectionVoteException() {
    super();
  }

}
