package keeper.project.homepage.exception.posting;

public class CustomCommentEmptyFieldException extends RuntimeException {

  public CustomCommentEmptyFieldException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomCommentEmptyFieldException(String msg) {
    super(msg);
  }

  public CustomCommentEmptyFieldException() {
    super();
  }

}
