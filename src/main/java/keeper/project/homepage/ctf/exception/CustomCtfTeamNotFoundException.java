package keeper.project.homepage.ctf.exception;

public class CustomCtfTeamNotFoundException extends RuntimeException {

  public CustomCtfTeamNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomCtfTeamNotFoundException(String msg) {
    super(msg);
  }

  public CustomCtfTeamNotFoundException() {
    super();
  }

}
