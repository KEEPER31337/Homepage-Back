package keeper.project.homepage.ctf.exception;

public class CustomCtfChallengeNotFoundException extends RuntimeException {

  public CustomCtfChallengeNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomCtfChallengeNotFoundException(String msg) {
    super(msg);
  }

  public CustomCtfChallengeNotFoundException() {
    super();
  }

}
