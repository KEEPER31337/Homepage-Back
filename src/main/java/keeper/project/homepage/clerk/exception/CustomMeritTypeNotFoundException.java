package keeper.project.homepage.clerk.exception;

public class CustomMeritTypeNotFoundException extends RuntimeException {

  public CustomMeritTypeNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomMeritTypeNotFoundException(String msg) {
    super(msg);
  }

  public CustomMeritTypeNotFoundException() {
    super();
  }

}
