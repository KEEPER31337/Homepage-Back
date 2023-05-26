package keeper.project.homepage.point.exception;

public class CustomPointAbuseException extends RuntimeException {
  public CustomPointAbuseException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomPointAbuseException(String msg) {
    super(msg);
  }

  public CustomPointAbuseException() {
    super();
  }

}
