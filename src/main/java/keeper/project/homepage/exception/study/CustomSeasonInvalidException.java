package keeper.project.homepage.exception.study;

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