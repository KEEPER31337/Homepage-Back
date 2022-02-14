package keeper.project.homepage.exception.posting;

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
