package keeper.project.homepage.exception.posting;

public class CustomPostingIncorrectException extends RuntimeException {

  public CustomPostingIncorrectException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomPostingIncorrectException(String msg) {
    super(msg);
  }

  public CustomPostingIncorrectException() {
    super();
  }
}
