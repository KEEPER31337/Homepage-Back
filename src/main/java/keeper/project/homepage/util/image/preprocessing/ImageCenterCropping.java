package keeper.project.homepage.util.image.preprocessing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import keeper.project.homepage.exception.file.CustomImageIOException;

public class ImageCenterCropping implements ImagePreprocessing {

  private Integer width;
  private Integer height;

  public ImageCenterCropping(Integer width, Integer height) {
    this.width = width;
    this.height = height;
  }

  public ImageCenterCropping(ImageSize size) {
    this.width = size.getWidth();
    this.height = size.getHeight();
  }

  public void imageProcessing(File imageFile, String fileFormat) {
    BufferedImage bo_image;
    try {
      bo_image = ImageIO.read(imageFile);
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomImageIOException();
    }
    BufferedImage bt_image = new BufferedImage(width, height,
        BufferedImage.TYPE_3BYTE_BGR);

    int originalWidth = bo_image.getWidth();
    int originalHeight = bo_image.getHeight();
    // 원본 이미지에서 잘라낼 부분의 크기 설정
    int newWidth = originalWidth;
    int newHeight = (originalWidth * height) / width;
    if (newHeight > originalHeight) {
      newWidth = (originalHeight * width) / height;
      newHeight = originalHeight;
      newWidth = newWidth > originalWidth ? originalWidth : newWidth;
    }
    // 원본 이미지에서 잘라낼 부분
    int srcSrcx = (originalWidth - newWidth) / 2;
    int srcSrcy = (originalHeight - newHeight) / 2;
    int srcDestx = (originalWidth + newWidth) / 2;
    int srcDesty = (originalHeight + newHeight) / 2;
    Graphics2D graphic = bt_image.createGraphics();
    graphic.drawImage(bo_image, 0, 0, width, height,
        srcSrcx, srcSrcy, srcDestx, srcDesty, null);
    try {
      ImageIO.write(bt_image, fileFormat, imageFile);
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomImageIOException();
    }
  }
}
