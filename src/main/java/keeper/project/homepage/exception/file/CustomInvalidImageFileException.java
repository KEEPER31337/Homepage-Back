package keeper.project.homepage.exception.file;

public class CustomInvalidImageFileException extends RuntimeException {

  public CustomInvalidImageFileException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomInvalidImageFileException(String msg) {
    super(msg);
  }

  public CustomInvalidImageFileException() {
    super();
  }
}
