package keeper.project.homepage.about.exception;

public class CustomStaticWriteSubtitleImageNotFoundException extends RuntimeException {

  public CustomStaticWriteSubtitleImageNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomStaticWriteSubtitleImageNotFoundException(String msg) {
    super(msg);
  }

  public CustomStaticWriteSubtitleImageNotFoundException() { super(); }
}
