package keeper.project.homepage.exception.clerk;

public class CustomSeminarAttendanceFailException extends RuntimeException {

  public CustomSeminarAttendanceFailException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSeminarAttendanceFailException(String msg) {
    super(msg);
  }

  public CustomSeminarAttendanceFailException() {
    super();
  }

}
