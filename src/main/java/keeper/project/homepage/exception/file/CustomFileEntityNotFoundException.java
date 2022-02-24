package keeper.project.homepage.exception.file;

public class CustomFileEntityNotFoundException extends RuntimeException {

  public CustomFileEntityNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomFileEntityNotFoundException(String msg) {
    super(msg);
  }

  public CustomFileEntityNotFoundException() {
    super();
  }

}
