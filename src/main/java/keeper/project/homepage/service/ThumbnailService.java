package keeper.project.homepage.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
import keeper.project.homepage.entity.OriginalImageEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.repository.ThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

  private final ThumbnailRepository thumbnailRepository;

  private void resizeImage(File thumbnailFile, int width, int height) throws IOException {
    BufferedImage bo_image = ImageIO.read(thumbnailFile);
    BufferedImage bt_image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

    Graphics2D graphic = bt_image.createGraphics();
    graphic.drawImage(bo_image, 0, 0, width, height, null);
    ImageIO.write(bt_image, "jpg", thumbnailFile);
  }

  private void cropCenterImage(File thumbnailFile, int thumbWidth, int thumbHeight)
      throws IOException {
    BufferedImage bo_image = ImageIO.read(thumbnailFile);
    BufferedImage bt_image = new BufferedImage(thumbWidth, thumbHeight,
        BufferedImage.TYPE_3BYTE_BGR);

    int originalWidth = bo_image.getWidth();
    int originalHeight = bo_image.getHeight();
    // 원본 이미지에서 잘라낼 부분의 크기 설정
    int newWidth = originalWidth;
    int newHeight = (originalWidth * thumbHeight) / thumbWidth;
    if (newHeight > originalHeight) {
      newWidth = (originalHeight * thumbWidth) / thumbHeight;
      newHeight = originalHeight;
      newWidth = newWidth > originalWidth ? originalWidth : newWidth;
    }
    // 원본 이미지에서 잘라낼 부분
    int srcSrcx = (originalWidth - newWidth) / 2;
    int srcSrcy = (originalHeight - newHeight) / 2;
    int srcDestx = (originalWidth + newWidth) / 2;
    int srcDesty = (originalHeight + newHeight) / 2;
    Graphics2D graphic = bt_image.createGraphics();
    graphic.drawImage(bo_image, 0, 0, thumbWidth, thumbHeight,
        srcSrcx, srcSrcy, srcDestx, srcDesty, null);
    ImageIO.write(bt_image, "jpg", thumbnailFile);
  }

  public ThumbnailEntity save(MultipartFile multipartFile,
      OriginalImageEntity originalImageEntity, UUID uuid, Integer setWidth, Integer setHeight) {
    if (multipartFile == null) {
      // 나중에 default 이미지로 설정
      return null;
    }

    String fileName = "thumb_" + uuid.toString() + "_" + multipartFile.getOriginalFilename();
    String relFilePath = "keeper_files\\thumbnail";
    String absFilePath = System.getProperty("user.dir") + "\\" + relFilePath;
    if (!new File(absFilePath).exists()) {
      new File(absFilePath).mkdir();
    }
    try {
      absFilePath = absFilePath + "\\" + fileName;
      File thumbnailFile = new File(absFilePath);
      multipartFile.transferTo(thumbnailFile);
      // 이미지 전체를 setWidth x setHeight 로 비율 조정하고 싶을 때
//      resizeImage(thumbnailFile, setWidth, setHeight);
      // 이미지의 중심을 기준으로 setWidth x setWidth 만큼 잘라내고 싶을 때
      cropCenterImage(thumbnailFile, setWidth, setHeight);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return thumbnailRepository.save(
        ThumbnailEntity.builder().path(relFilePath).originalImage(originalImageEntity).build());
  }

  public ThumbnailEntity findById(Integer findId) {
    return thumbnailRepository.findById(findId).orElse(null);
  }

  public boolean deleteById(Integer deleteId) {
    if (thumbnailRepository.findById(deleteId).isPresent()) {
      return false;
    }
    thumbnailRepository.deleteById(deleteId);
    return true;
  }
}
