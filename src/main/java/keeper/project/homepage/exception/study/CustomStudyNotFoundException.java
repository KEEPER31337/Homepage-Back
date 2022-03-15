package keeper.project.homepage.exception.study;

public class CustomStudyNotFoundException extends RuntimeException {

  public CustomStudyNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomStudyNotFoundException(String msg) {
    super(msg);
  }

  public CustomStudyNotFoundException() {
    super();
  }
}