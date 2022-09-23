package keeper.project.homepage.util.exception.file;

public class CustomFileTransferFailedException extends RuntimeException {

  public CustomFileTransferFailedException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomFileTransferFailedException(String msg) {
    super(msg);
  }

  public CustomFileTransferFailedException() {
    super();
  }
}
