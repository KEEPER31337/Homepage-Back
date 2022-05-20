package keeper.project.homepage.exception.ctf;

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
