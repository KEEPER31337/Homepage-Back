package keeper.project.homepage.sign.exception;

public class CustomLoginIdSigninFailedException extends RuntimeException {
  public CustomLoginIdSigninFailedException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomLoginIdSigninFailedException(String msg) {
    super(msg);
  }

  public CustomLoginIdSigninFailedException() {
    super();
  }

}
