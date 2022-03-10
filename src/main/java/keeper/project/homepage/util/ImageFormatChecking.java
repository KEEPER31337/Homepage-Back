package keeper.project.homepage.util;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import keeper.project.homepage.exception.file.CustomImageFormatException;
import keeper.project.homepage.exception.file.CustomImageIOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageFormatChecking {

  private final String[] enableImageFormat = {"jpg", "jpeg", "png", "gif"};

  public void checkImageFile(MultipartFile multipartFile) {
    String contentType = multipartFile.getContentType();
    boolean result = false;
    if (contentType.startsWith("image")) {
      for (String format : enableImageFormat) {
        if (contentType.endsWith(format)) {
          result = true;
          break;
        }
      }
    }
    if (!result) {
      throw new CustomImageFormatException();
    }
  }

  public void checkImageFile(String fileName) {
    String[] fileNameSplitArray = fileName.split("\\.");
    String fileFormat = fileNameSplitArray[fileNameSplitArray.length - 1];
    boolean result = false;
    for (String format : enableImageFormat) {
      if (fileFormat.equals(format)) {
        result = true;
        break;
      }
    }
    if (!result) {
      throw new CustomImageFormatException();
    }
  }

  public void checkNormalImageFile(MultipartFile multipartFile) {
    BufferedImage bo_image;
    try {
      bo_image = ImageIO.read(multipartFile.getInputStream());
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomImageIOException();
    }
    if (bo_image == null) {
      throw new CustomImageFormatException();
    }
  }
}
