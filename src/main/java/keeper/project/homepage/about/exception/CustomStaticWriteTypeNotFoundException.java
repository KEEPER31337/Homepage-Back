package keeper.project.homepage.about.exception;

public class CustomStaticWriteTypeNotFoundException extends RuntimeException {

  public CustomStaticWriteTypeNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomStaticWriteTypeNotFoundException(String msg) {
    super(msg);
  }

  public CustomStaticWriteTypeNotFoundException() { super(); }
}
