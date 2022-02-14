package keeper.project.homepage.exception.member;

public class CustomAccountDeleteFailedException extends RuntimeException {

  public CustomAccountDeleteFailedException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomAccountDeleteFailedException(String msg) {
    super(msg);
  }

  public CustomAccountDeleteFailedException() {
    super();
  }
}
