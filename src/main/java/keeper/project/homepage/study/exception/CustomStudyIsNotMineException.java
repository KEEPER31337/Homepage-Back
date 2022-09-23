package keeper.project.homepage.study.exception;

public class CustomStudyIsNotMineException extends RuntimeException {

  public CustomStudyIsNotMineException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomStudyIsNotMineException(String msg) {
    super(msg);
  }

  public CustomStudyIsNotMineException() {
    super();
  }
}