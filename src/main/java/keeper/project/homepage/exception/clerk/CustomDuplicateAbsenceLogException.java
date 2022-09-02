package keeper.project.homepage.exception.clerk;

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
