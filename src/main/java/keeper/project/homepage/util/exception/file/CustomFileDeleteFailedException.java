package keeper.project.homepage.util.exception.file;

public class CustomFileDeleteFailedException extends RuntimeException{

  public CustomFileDeleteFailedException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomFileDeleteFailedException(String msg) {
    super(msg);
  }

  public CustomFileDeleteFailedException() {
    super();
  }
}
