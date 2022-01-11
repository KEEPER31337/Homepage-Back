package keeper.project.homepage.exception;

public class CustomEmailSigninFailedException extends RuntimeException {
  public CustomEmailSigninFailedException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomEmailSigninFailedException(String msg) {
    super(msg);
  }

  public CustomEmailSigninFailedException() {
    super();
  }

}
