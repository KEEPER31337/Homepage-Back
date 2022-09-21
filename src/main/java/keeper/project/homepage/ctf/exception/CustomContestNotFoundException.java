package keeper.project.homepage.ctf.exception;

public class CustomContestNotFoundException extends RuntimeException {

  public CustomContestNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomContestNotFoundException(String msg) {
    super(msg);
  }

  public CustomContestNotFoundException() {
    super();
  }

}
