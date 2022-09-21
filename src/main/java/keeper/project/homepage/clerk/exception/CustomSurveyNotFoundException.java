package keeper.project.homepage.clerk.exception;

public class CustomSurveyNotFoundException extends RuntimeException {

  public CustomSurveyNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSurveyNotFoundException(String msg) {
    super(msg);
  }

  public CustomSurveyNotFoundException() {
    super();
  }
}
