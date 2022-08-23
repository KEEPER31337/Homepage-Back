package keeper.project.homepage.exception.clerk;

public class CustomAttendanceAbsenceExcuseIsNullException extends RuntimeException {

  public CustomAttendanceAbsenceExcuseIsNullException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomAttendanceAbsenceExcuseIsNullException(String msg) {
    super(msg);
  }

  public CustomAttendanceAbsenceExcuseIsNullException() {
    super();
  }

}
