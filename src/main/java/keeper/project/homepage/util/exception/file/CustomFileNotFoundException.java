package keeper.project.homepage.util.exception.file;

public class CustomFileNotFoundException extends RuntimeException {

  public CustomFileNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomFileNotFoundException(String msg) {
    super(msg);
  }

  public CustomFileNotFoundException() {
    super();
  }
}
