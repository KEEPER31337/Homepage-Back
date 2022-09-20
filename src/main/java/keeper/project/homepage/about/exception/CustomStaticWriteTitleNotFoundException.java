package keeper.project.homepage.about.exception;

public class CustomStaticWriteTitleNotFoundException extends RuntimeException {

  public CustomStaticWriteTitleNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomStaticWriteTitleNotFoundException(String msg) {
    super(msg);
  }

  public CustomStaticWriteTitleNotFoundException() { super(); }
}
