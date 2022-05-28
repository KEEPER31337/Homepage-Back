package keeper.project.homepage.exception.about;

public class CustomStaticWriteContentNotFoundException extends RuntimeException {

  public CustomStaticWriteContentNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomStaticWriteContentNotFoundException(String msg) {
    super(msg);
  }

  public CustomStaticWriteContentNotFoundException() { super(); }
}
