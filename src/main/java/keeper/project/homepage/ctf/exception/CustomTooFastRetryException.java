package keeper.project.homepage.ctf.exception;

import lombok.Getter;

@Getter
public class CustomTooFastRetryException extends RuntimeException {

  private long retrySeconds;

  public CustomTooFastRetryException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomTooFastRetryException(String msg) {
    super(msg);
  }

  public CustomTooFastRetryException(long retrySeconds) {
    super();
    this.retrySeconds = retrySeconds;
  }

}
