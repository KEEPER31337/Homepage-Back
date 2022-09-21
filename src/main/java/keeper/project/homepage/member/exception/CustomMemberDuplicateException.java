package keeper.project.homepage.member.exception;

public class CustomMemberDuplicateException extends RuntimeException {

  public CustomMemberDuplicateException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomMemberDuplicateException(String msg) {
    super(msg);
  }

  public CustomMemberDuplicateException() {
    super();
  }

}
