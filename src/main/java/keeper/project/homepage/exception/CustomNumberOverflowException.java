package keeper.project.homepage.exception;

public class CustomNumberOverflowException extends RuntimeException {

  public CustomNumberOverflowException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomNumberOverflowException(String msg) {
    super(msg);
  }

  public CustomNumberOverflowException() {
    super();
  }
}
