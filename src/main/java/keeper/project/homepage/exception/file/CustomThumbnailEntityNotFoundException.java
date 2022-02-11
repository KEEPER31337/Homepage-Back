package keeper.project.homepage.exception.file;

public class CustomThumbnailEntityNotFoundException extends RuntimeException {

  public CustomThumbnailEntityNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomThumbnailEntityNotFoundException(String msg) {
    super(msg);
  }

  public CustomThumbnailEntityNotFoundException() {
    super();
  }
}
