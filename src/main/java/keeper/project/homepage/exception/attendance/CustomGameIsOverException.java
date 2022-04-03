package keeper.project.homepage.exception.attendance;

public class CustomGameIsOverException extends RuntimeException {

  public CustomGameIsOverException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomGameIsOverException(String msg) {
    super(msg);
  }

  public CustomGameIsOverException() {
    super();
  }
}