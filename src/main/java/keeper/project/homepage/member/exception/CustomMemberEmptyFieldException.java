package keeper.project.homepage.member.exception;

public class CustomMemberEmptyFieldException extends RuntimeException {

  public CustomMemberEmptyFieldException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomMemberEmptyFieldException(String msg) {
    super(msg);
  }

  public CustomMemberEmptyFieldException() {
    super();
  }
}
