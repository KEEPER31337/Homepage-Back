package keeper.project.homepage.clerk.exception;

public class CustomSeminarAttendanceStatusNotFoundException extends RuntimeException {

  public CustomSeminarAttendanceStatusNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSeminarAttendanceStatusNotFoundException(String msg) {
    super(msg);
  }

  public CustomSeminarAttendanceStatusNotFoundException() {
    super();
  }

}
