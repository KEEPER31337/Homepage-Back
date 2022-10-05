package keeper.project.homepage.systemadmin.exception;

public class CustomClerkInaccessibleJobException extends RuntimeException {

  public CustomClerkInaccessibleJobException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomClerkInaccessibleJobException(String msg) {
    super(msg);
  }

  public CustomClerkInaccessibleJobException() {
    super();
  }

}
