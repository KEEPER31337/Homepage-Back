package keeper.project.homepage.exception;

public class CustomAboutFailedException extends RuntimeException {

  public CustomAboutFailedException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomAboutFailedException(String msg) {
    super(msg);
  }

  public CustomAboutFailedException() {
    super();
  }

}
