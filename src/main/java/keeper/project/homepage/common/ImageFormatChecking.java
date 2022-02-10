package keeper.project.homepage.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import keeper.project.homepage.exception.file.CustomImageFormatException;
import keeper.project.homepage.exception.file.CustomImageIOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageFormatChecking {

  private final String[] enableImageFormat = {"jpg", "jpeg", "png", "gif"};

  public void isImageFile(MultipartFile multipartFile) {
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

  public void isImageFile(String fileName) {
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

  public void isNormalImageFile(MultipartFile multipartFile) {
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
