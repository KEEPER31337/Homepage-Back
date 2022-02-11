package keeper.project.homepage.exception.posting;

public class CustomCommentNotFoundException extends RuntimeException {
  public CustomCommentNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomCommentNotFoundException(String msg) {
    super(msg);
  }

  public CustomCommentNotFoundException() {
    super();
  }
}
