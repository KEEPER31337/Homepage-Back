package keeper.project.homepage.exception.point;

public class CustomPointLackException extends RuntimeException {
  public CustomPointLackException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomPointLackException(String msg) {
    super(msg);
  }

  public CustomPointLackException() {
    super();
  }

}
