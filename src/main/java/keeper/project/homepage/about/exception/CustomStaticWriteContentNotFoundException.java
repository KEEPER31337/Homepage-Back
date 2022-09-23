package keeper.project.homepage.about.exception;

public class CustomStaticWriteContentNotFoundException extends RuntimeException {

  public CustomStaticWriteContentNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomStaticWriteContentNotFoundException(String msg) {
    super(msg);
  }

  public CustomStaticWriteContentNotFoundException() { super(); }
}
