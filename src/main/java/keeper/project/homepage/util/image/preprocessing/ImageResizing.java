package keeper.project.homepage.util.image.preprocessing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import keeper.project.homepage.exception.file.CustomImageIOException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ImageResizing implements ImageProcessing {

  public enum RESIZE_OPTION {
    DEFAULT,
    KEEP_RATIO,
    KEEP_RATIO_IN_OUTER_BOUNDARY;
  }

  private RESIZE_OPTION resize_option;

  public ImageResizing(RESIZE_OPTION resize_option) {
    /**
     * @param resize_option imageProcessing(.., dest_width, dest_height, ..)의 resize option
     *                      DEFAULT : dest_width X dest_height 로 크기 변경
     *                      KEEP_RATIO : 원본 이미지의 크기 비율을 유지, dest_width와 dest_height 중 크기 변화가 작은 길이에 맞추어 변경
     *                      KEEP_RATIO_IN_OUTER_BOUNDARY : KEEP_RATIO 에서 원본 이미지의 너비와 높이가 dest_width, dest_height 보다 작다면 이미지 처리하지 않음
     */
    this.resize_option = resize_option;
  }

  private class Size {

    private Integer width;
    private Integer height;

    Size(Integer width, Integer height) {
      this.width = width;
      this.height = height;
    }

    Integer getWidth() {
      return this.width;
    }

    Integer getHeight() {
      return this.height;
    }
  }

  private Size getScaledSize(
      Size image_size,
      Size boundary_size) {
    double widthRatio = boundary_size.getWidth() / (double) image_size.getWidth();
    double heightRatio = boundary_size.getHeight() / (double) image_size.getHeight();
    double ratio = Math.min(widthRatio, heightRatio);

    return new Size((int) (image_size.getWidth() * ratio),
        (int) (image_size.getHeight() * ratio));
  }

  public void imageProcessing(File image, int dest_width, int dest_height, String fileFormat) {
    BufferedImage bo_image;
    try {
      bo_image = ImageIO.read(image);
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomImageIOException();
    }
    Size destSize;
    switch (resize_option) {
      case KEEP_RATIO_IN_OUTER_BOUNDARY:
        if (bo_image.getWidth() <= dest_width && bo_image.getHeight() <= dest_height) {
          return;
        }
        destSize = getScaledSize(
            new Size(bo_image.getWidth(), bo_image.getHeight()),
            new Size(dest_width, dest_height));
        break;
      case KEEP_RATIO:
        destSize = getScaledSize(
            new Size(bo_image.getWidth(), bo_image.getHeight()),
            new Size(dest_width, dest_height));
        break;
      default:
        destSize = new Size(dest_width, dest_height);
        break;
    }
    BufferedImage bt_image = new BufferedImage(destSize.getWidth(), destSize.getHeight(),
        BufferedImage.TYPE_3BYTE_BGR);

    Graphics2D graphic = bt_image.createGraphics();
    graphic.drawImage(bo_image, 0, 0, destSize.getWidth(), destSize.getHeight(), null);
    try {
      ImageIO.write(bt_image, fileFormat, image);
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomImageIOException();
    }

  }
}
