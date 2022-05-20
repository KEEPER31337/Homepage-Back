package keeper.project.homepage.exception.about;

public class CustomStaticWriteNotFoundException extends RuntimeException {

  public CustomStaticWriteNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomStaticWriteNotFoundException(String msg) {
    super(msg);
  }

  public CustomStaticWriteNotFoundException() { super(); }
}
