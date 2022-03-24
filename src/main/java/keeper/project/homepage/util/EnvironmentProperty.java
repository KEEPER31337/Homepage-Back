package keeper.project.homepage.util;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class EnvironmentProperty {

  private static final String THUMBNAIL_PATH = "/v1/util/thumbnail/";

  public static String getThumbnailPath(Long thumbnailId) {
    URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(THUMBNAIL_PATH + "/{id}")
        .buildAndExpand(thumbnailId)
        .toUri();
    return location.toString();
  }
}
