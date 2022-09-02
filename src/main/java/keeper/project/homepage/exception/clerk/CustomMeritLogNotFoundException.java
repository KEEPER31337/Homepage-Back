package keeper.project.homepage.exception.clerk;

public class CustomMeritLogNotFoundException extends RuntimeException {

  public CustomMeritLogNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomMeritLogNotFoundException(String msg) {
    super(msg);
  }

  public CustomMeritLogNotFoundException() {
    super();
  }

}
