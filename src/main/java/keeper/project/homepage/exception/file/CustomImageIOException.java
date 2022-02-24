package keeper.project.homepage.exception.file;

public class CustomImageIOException extends RuntimeException {

  public CustomImageIOException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomImageIOException(String msg) {
    super(msg);
  }

  public CustomImageIOException() {
    super();
  }
}
