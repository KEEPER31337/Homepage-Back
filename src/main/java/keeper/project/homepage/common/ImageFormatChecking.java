package keeper.project.homepage.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageFormatChecking {

  private final String[] enableImageFormat = {"jpg", "jpeg", "png", "gif"};

  public boolean isImageFile(MultipartFile multipartFile) {
    String contentType = multipartFile.getContentType();
    if (contentType.startsWith("image")) {
      for (String format : enableImageFormat) {
        if (contentType.endsWith(format)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isImageFile(String fileName) {
    String[] fileNameSplitArray = fileName.split("\\.");
    String fileFormat = fileNameSplitArray[fileNameSplitArray.length - 1];
    for (String format : enableImageFormat) {
      if (fileFormat.equals(format)) {
        return true;
      }
    }
    return false;
  }

  public boolean isNormalImageFile(MultipartFile multipartFile) throws IOException {
    if (isImageFile(multipartFile) == false) {
      return false;
    }
    BufferedImage bo_image = ImageIO.read(multipartFile.getInputStream());
    if (bo_image == null) {
      return false;
    }
    return true;
  }
}
