package keeper.project.homepage.clerk.exception;

public class CustomDuplicateSeminarException extends RuntimeException {

  public CustomDuplicateSeminarException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomDuplicateSeminarException(String msg) {
    super(msg);
  }

  public CustomDuplicateSeminarException() {
    super();
  }

}
