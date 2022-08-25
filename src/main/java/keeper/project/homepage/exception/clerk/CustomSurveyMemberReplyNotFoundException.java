package keeper.project.homepage.exception.clerk;

public class CustomSurveyMemberReplyNotFoundException extends RuntimeException{
  public CustomSurveyMemberReplyNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSurveyMemberReplyNotFoundException(String msg) {
    super(msg);
  }

  public CustomSurveyMemberReplyNotFoundException() {
    super();
  }
}
