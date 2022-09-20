package keeper.project.homepage.posting.exception;

public class CustomPostingNotFoundException extends RuntimeException {

  public CustomPostingNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomPostingNotFoundException(String msg) {
    super(msg);
  }

  public CustomPostingNotFoundException() {
    super();
  }

}
