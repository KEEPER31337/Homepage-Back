package keeper.project.homepage.util.image.preprocessing;

public enum ImageSize {
  SMALL(100, 100), LARGE(500, 500), STUDY(500, 500);
  private final Integer width;
  private final Integer height;

  ImageSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public Integer getWidth() {
    return this.width;
  }

  public Integer getHeight() {
    return this.height;
  }
}
