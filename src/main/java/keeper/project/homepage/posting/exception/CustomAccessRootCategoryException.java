package keeper.project.homepage.posting.exception;

public class CustomAccessRootCategoryException extends RuntimeException {
  public CustomAccessRootCategoryException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomAccessRootCategoryException(String msg) {
    super(msg);
  }

  public CustomAccessRootCategoryException() {
    super();
  }

}
