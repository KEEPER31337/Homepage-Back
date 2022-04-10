package keeper.project.homepage.exception.attendance;

public class CustomAttendanceException extends RuntimeException {

  public CustomAttendanceException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomAttendanceException(String msg) {
    super(msg);
  }

  public CustomAttendanceException() {
    super();
  }

}
