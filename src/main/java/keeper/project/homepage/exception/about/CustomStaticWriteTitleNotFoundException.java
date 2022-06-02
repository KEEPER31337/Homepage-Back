package keeper.project.homepage.exception.about;

public class CustomStaticWriteTitleNotFoundException extends RuntimeException {

  public CustomStaticWriteTitleNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomStaticWriteTitleNotFoundException(String msg) {
    super(msg);
  }

  public CustomStaticWriteTitleNotFoundException() { super(); }
}
