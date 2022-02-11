package keeper.project.homepage.exception.file;

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
