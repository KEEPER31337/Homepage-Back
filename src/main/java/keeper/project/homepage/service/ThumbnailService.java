package keeper.project.homepage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import keeper.project.homepage.common.ImageFormatChecking;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.exception.file.CustomFileNotFoundException;
import keeper.project.homepage.exception.file.CustomFileDeleteFailedException;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.common.ImageProcessing;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

  private final static String THUMBNAIL_FORMAT = "jpg";
  private final static Integer SMALL_WIDTH = 30;
  private final static Integer SMALL_HEIGHT = 30;
  private final static Integer LARGE_WIDTH = 100;
  private final static Integer LARGE_HEIGHT = 100;

  private final String relDirPath = "keeper_files" + File.separator + "thumbnail";
  private final String defaultImageName = "thumb_default.jpg"; // thumbnail delete시 파일명 비교

  private final ImageFormatChecking imageFormatChecking;
  private final ThumbnailRepository thumbnailRepository;
  private final FileService fileService;

  private Integer[] getThumbnailSize(String type) {
    switch (type) {
      case "small":
        return new Integer[]{SMALL_WIDTH, SMALL_HEIGHT};
      case "large":
      default:
        return new Integer[]{LARGE_WIDTH, LARGE_HEIGHT};
    }
  }

  public byte[] getThumbnail(Long thumbnailId) throws IOException {
    ThumbnailEntity thumbnail = findById(thumbnailId);
    String thumbnailPath = System.getProperty("user.dir") + File.separator + thumbnail.getPath();
    File file = new File(thumbnailPath);
    InputStream in = new FileInputStream(file);
    return IOUtils.toByteArray(in);
  }

  public ThumbnailEntity saveThumbnail(ImageProcessing imageProcessing, MultipartFile multipartFile,
      FileEntity fileEntity, String sizeType) {
    String fileName = "";
    if (multipartFile == null || multipartFile.isEmpty()) {
      fileName = this.defaultImageName;
    } else {
      imageFormatChecking.checkNormalImageFile(multipartFile);

      File thumbnailImage = fileService.saveFileInServer(multipartFile, this.relDirPath);
      Integer width = getThumbnailSize(sizeType.toLowerCase(Locale.ROOT))[0];
      Integer height = getThumbnailSize(sizeType.toLowerCase(Locale.ROOT))[1];
      imageProcessing.imageProcessing(thumbnailImage, width, height, THUMBNAIL_FORMAT);
      fileName = thumbnailImage.getName();
    }
    return thumbnailRepository.save(
        ThumbnailEntity.builder()
            .path(this.relDirPath + File.separator + fileName)
            .file(fileEntity)
            .build());
  }

  public ThumbnailEntity findById(Long findId) {
    return thumbnailRepository.findById(findId)
        .orElseThrow(CustomThumbnailEntityNotFoundException::new);
  }

  public void deleteById(Long deleteId) {
    ThumbnailEntity deleted = findById(deleteId);
    // 기본 썸네일이면 삭제 X
    if (deleted.getPath().equals(relDirPath + File.separator + defaultImageName) == false) {
      File thumbnailFile = new File(
          System.getProperty("user.dir") + File.separator + deleted.getPath());
      if (thumbnailFile.exists() == false) {
        throw new CustomFileNotFoundException();
      } else if (thumbnailFile.delete() == false) {
        throw new CustomFileDeleteFailedException();
      }
    }
    thumbnailRepository.deleteById(deleteId);
  }
}