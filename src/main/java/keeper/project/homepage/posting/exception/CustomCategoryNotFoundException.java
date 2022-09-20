package keeper.project.homepage.posting.exception;

public class CustomCategoryNotFoundException extends RuntimeException {
  public CustomCategoryNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomCategoryNotFoundException(String msg) {
    super(msg);
  }

  public CustomCategoryNotFoundException() {
    super();
  }

}
