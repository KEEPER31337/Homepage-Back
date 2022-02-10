package keeper.project.homepage.exception;

public class CustomTransferPointLackException extends RuntimeException {
  public CustomTransferPointLackException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomTransferPointLackException(String msg) {
    super(msg);
  }

  public CustomTransferPointLackException() {
    super();
  }

}
