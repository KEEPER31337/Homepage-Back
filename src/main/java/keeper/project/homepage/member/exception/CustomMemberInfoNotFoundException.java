package keeper.project.homepage.member.exception;

public class CustomMemberInfoNotFoundException extends RuntimeException {

  public CustomMemberInfoNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomMemberInfoNotFoundException(String msg) {
    super(msg);
  }

  public CustomMemberInfoNotFoundException() {
    super();
  }

}
