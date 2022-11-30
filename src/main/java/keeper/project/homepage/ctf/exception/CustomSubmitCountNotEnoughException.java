package keeper.project.homepage.ctf.exception;

public class CustomSubmitCountNotEnoughException extends RuntimeException {

  public CustomSubmitCountNotEnoughException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomSubmitCountNotEnoughException(String msg) {
    super(msg);
  }

  public CustomSubmitCountNotEnoughException() {
    super();
  }

}
