package keeper.project.homepage.member.exception;

public class CustomAccessVirtualMemberException extends RuntimeException {

  public CustomAccessVirtualMemberException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomAccessVirtualMemberException(String msg) {
    super(msg);
  }

  public CustomAccessVirtualMemberException() {
    super();
  }
}
