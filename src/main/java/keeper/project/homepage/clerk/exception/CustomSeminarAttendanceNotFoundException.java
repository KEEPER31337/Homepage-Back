package keeper.project.homepage.clerk.exception;

public class CustomSeminarAttendanceNotFoundException extends RuntimeException {

  public CustomSeminarAttendanceNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSeminarAttendanceNotFoundException(String msg) {
    super(msg);
  }

  public CustomSeminarAttendanceNotFoundException() {
    super();
  }

}
