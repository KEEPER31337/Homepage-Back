package keeper.project.homepage.library.exception;

public class CustomBookBorrowNotFoundException extends RuntimeException {

  public CustomBookBorrowNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomBookBorrowNotFoundException(String msg) {
    super(msg);
  }

  public CustomBookBorrowNotFoundException() {
    super();
  }
}
