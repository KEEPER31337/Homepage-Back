package keeper.project.homepage.study.exception;

public class CustomSeasonInvalidException extends RuntimeException {

  public CustomSeasonInvalidException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSeasonInvalidException(String msg) {
    super(msg);
  }

  public CustomSeasonInvalidException() {
    super();
  }
}