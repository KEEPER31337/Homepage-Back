package keeper.project.homepage.util.exception.file;

public class CustomImageFormatException extends RuntimeException {

  public CustomImageFormatException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomImageFormatException(String msg) {
    super(msg);
  }

  public CustomImageFormatException() {
    super();
  }
}
