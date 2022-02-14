package keeper.project.homepage.exception.library;

public class CustomBookNotFoundException extends RuntimeException {

  public CustomBookNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomBookNotFoundException(String msg) {
    super(msg);
  }

  public CustomBookNotFoundException() {
    super();
  }
}
