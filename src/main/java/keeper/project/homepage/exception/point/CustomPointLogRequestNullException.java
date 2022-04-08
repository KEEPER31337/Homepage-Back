package keeper.project.homepage.exception.point;

public class CustomPointLogRequestNullException extends RuntimeException {

  public CustomPointLogRequestNullException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomPointLogRequestNullException(String msg) {
    super(msg);
  }

  public CustomPointLogRequestNullException() {
    super();
  }
}
