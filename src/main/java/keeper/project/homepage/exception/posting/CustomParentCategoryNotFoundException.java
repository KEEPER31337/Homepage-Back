package keeper.project.homepage.exception.posting;

public class CustomParentCategoryNotFoundException extends RuntimeException {
  public CustomParentCategoryNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomParentCategoryNotFoundException(String msg) {
    super(msg);
  }

  public CustomParentCategoryNotFoundException() {
    super();
  }

}
