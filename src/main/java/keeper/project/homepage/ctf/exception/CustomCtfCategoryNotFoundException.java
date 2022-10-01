package keeper.project.homepage.ctf.exception;

public class CustomCtfCategoryNotFoundException extends RuntimeException {

  public CustomCtfCategoryNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomCtfCategoryNotFoundException(String msg) {
    super(msg);
  }

  public CustomCtfCategoryNotFoundException() {
    super();
  }

}
