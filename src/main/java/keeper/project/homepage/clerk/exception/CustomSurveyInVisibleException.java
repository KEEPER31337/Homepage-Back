package keeper.project.homepage.clerk.exception;

public class CustomSurveyInVisibleException extends RuntimeException {

  public CustomSurveyInVisibleException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSurveyInVisibleException(String msg) {
    super(msg);
  }

  public CustomSurveyInVisibleException() {
    super();
  }
}
