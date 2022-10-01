package keeper.project.homepage.posting.exception;

public class CustomPostingTempException extends RuntimeException {

  public CustomPostingTempException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomPostingTempException(String msg) {
    super(msg);
  }

  public CustomPostingTempException() {
    super();
  }

}
