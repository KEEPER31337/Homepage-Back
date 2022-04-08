package keeper.project.homepage.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import keeper.project.homepage.exception.file.CustomImageIOException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ImageResize implements ImageProcessing {

  public void imageProcessing(File image, int width, int height, String fileFormat) {
    BufferedImage bo_image;
    try {
      bo_image = ImageIO.read(image);
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomImageIOException();
    }
    BufferedImage bt_image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

    Graphics2D graphic = bt_image.createGraphics();
    graphic.drawImage(bo_image, 0, 0, width, height, null);
    try {
      ImageIO.write(bt_image, fileFormat, image);
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomImageIOException();
    }
  }
}
