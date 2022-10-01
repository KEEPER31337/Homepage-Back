package keeper.project.homepage.util.exception.file;

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
