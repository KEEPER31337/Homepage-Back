package keeper.project.homepage.posting.exception;

public class CustomPostingAccessDeniedException extends RuntimeException {

  public CustomPostingAccessDeniedException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomPostingAccessDeniedException(String msg) {
    super(msg);
  }

  public CustomPostingAccessDeniedException() {
    super();
  }
}
