package keeper.project.homepage.clerk.exception;

public class CustomDuplicateAbsenceLogException extends RuntimeException {

  public CustomDuplicateAbsenceLogException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomDuplicateAbsenceLogException(String msg) {
    super(msg);
  }

  public CustomDuplicateAbsenceLogException() {
    super();
  }
}
