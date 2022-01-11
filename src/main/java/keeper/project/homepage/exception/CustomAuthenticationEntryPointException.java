package keeper.project.homepage.exception;

public class CustomAuthenticationEntryPointException extends RuntimeException {

  public CustomAuthenticationEntryPointException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomAuthenticationEntryPointException(String msg) {
    super(msg);
  }

  public CustomAuthenticationEntryPointException() {
    super();
  }
}