package keeper.project.homepage.exception.clerk;

public class CustomSeminarNotFoundException extends RuntimeException {

  public CustomSeminarNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSeminarNotFoundException(String msg) {
    super(msg);
  }

  public CustomSeminarNotFoundException() {
    super();
  }
}

