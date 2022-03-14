package keeper.project.homepage.util;

public class EnvironmentProperty {

  private static final String THUMBNAIL_PATH = "/v1/util/thumbnail/";

  private enum URL_TYPE {
    Local("127.0.0.1", "8080"),
    Dev("13.125.14.242", "3000"),
    Prod("13.125.14.242", "8080");

    String ip;
    String port;

    URL_TYPE(String ip, String port) {
      this.ip = ip;
      this.port = port;
    }

    public String getIp() {
      return ip;
    }

    public String getPort() {
      return port;
    }
  }

  private static String getURL(URL_TYPE type) {
    return "http://" + type.getIp() + ":" + type.getPort();
  }

  public static String getThumbnailPath(Long thumbnailId) {
    return getURL(URL_TYPE.Prod) + THUMBNAIL_PATH + thumbnailId;
  }
}