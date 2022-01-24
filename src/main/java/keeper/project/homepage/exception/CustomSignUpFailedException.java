package keeper.project.homepage.exception;

public class CustomSignUpFailedException extends RuntimeException {

  public CustomSignUpFailedException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSignUpFailedException(String msg) {
    super(msg);
  }

  public CustomSignUpFailedException() {
    super();
  }
}
