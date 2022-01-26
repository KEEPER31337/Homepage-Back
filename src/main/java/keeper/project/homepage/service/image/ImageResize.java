package keeper.project.homepage.service.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ImageResize implements ImageProcessing {

  public void imageProcessing(File image, int width, int height, String fileFormat)
      throws IOException {
    BufferedImage bo_image = ImageIO.read(image);
    BufferedImage bt_image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

    Graphics2D graphic = bt_image.createGraphics();
    graphic.drawImage(bo_image, 0, 0, width, height, null);
    ImageIO.write(bt_image, fileFormat, image);
  }
}
