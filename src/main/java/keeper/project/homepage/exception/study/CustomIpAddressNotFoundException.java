package keeper.project.homepage.exception.study;

public class CustomIpAddressNotFoundException extends RuntimeException {

  public CustomIpAddressNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomIpAddressNotFoundException(String msg) {
    super(msg);
  }

  public CustomIpAddressNotFoundException() {
    super();
  }
}