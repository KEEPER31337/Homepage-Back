package keeper.project.homepage.ctf.exception;

public class CustomCtfTypeNotFoundException extends RuntimeException {

  public CustomCtfTypeNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomCtfTypeNotFoundException(String msg) {
    super(msg);
  }

  public CustomCtfTypeNotFoundException() {
    super();
  }

}
