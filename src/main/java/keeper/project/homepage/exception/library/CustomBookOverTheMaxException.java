package keeper.project.homepage.exception.library;

public class CustomBookOverTheMaxException extends RuntimeException {

  public CustomBookOverTheMaxException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomBookOverTheMaxException(String msg) {
    super(msg);
  }

  public CustomBookOverTheMaxException() {
    super();
  }
}
