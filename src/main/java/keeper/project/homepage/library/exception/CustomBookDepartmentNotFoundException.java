package keeper.project.homepage.library.exception;

public class CustomBookDepartmentNotFoundException extends RuntimeException {

  public CustomBookDepartmentNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomBookDepartmentNotFoundException(String msg) {
    super(msg);
  }

  public CustomBookDepartmentNotFoundException() {
    super();
  }
}
