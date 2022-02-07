package keeper.project.homepage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import keeper.project.homepage.common.ImageFormatChecking;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.exception.CustomFileNotFoundException;
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
    ThumbnailEntity thumbnail = thumbnailRepository.findById(thumbnailId).orElseThrow(
        () -> new CustomFileNotFoundException("썸네일 파일을 찾을 수 없습니다")
    );
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
      if (imageFormatChecking.isImageFile(multipartFile) == false) {
        throw new RuntimeException("썸네일 용 파일은 이미지 파일이어야 합니다.");
      }
      try {
        if (imageFormatChecking.isNormalImageFile(multipartFile) == false) {
          throw new RuntimeException("이미지 파일을 BufferedImage로 읽어들일 수 없습니다.");
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("이미지 파일을 읽는 것을 실패했습니다.");
      }
      File thumbnailImage = fileService.saveFileInServer(multipartFile, this.relDirPath);
      try {
        Integer width = getThumbnailSize(sizeType.toLowerCase(Locale.ROOT))[0];
        Integer height = getThumbnailSize(sizeType.toLowerCase(Locale.ROOT))[1];
        imageProcessing.imageProcessing(thumbnailImage, width, height, THUMBNAIL_FORMAT);
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("썸네일 이미지용 후처리를 실패했습니다.");
      }
      fileName = thumbnailImage.getName();
    }
    return thumbnailRepository.save(
        ThumbnailEntity.builder().path(this.relDirPath + File.separator + fileName).file(fileEntity)
            .build());
  }

  public ThumbnailEntity findById(Long findId) {
    return thumbnailRepository.findById(findId).orElse(null);
  }

  public boolean deleteById(Long deleteId) throws RuntimeException {
    // issue : 각 예외사항에서 return 대신 custom exception으로 수정
    // original thumbnail file을 가지고 있으면 서버에 있는 이미지는 삭제 X
    ThumbnailEntity deleted = thumbnailRepository.findById(deleteId).orElse(null);
    if (deleted == null) {
      return false;
    }
    File thumbnailFile = new File(
        System.getProperty("user.dir") + File.separator + deleted.getPath());
    String thumbnailFileName = thumbnailFile.getName();
    if (thumbnailFileName.equals(defaultImageName) == false) {
      if (thumbnailFile.exists() == false) {
        throw new RuntimeException("썸네일 파일이 이미 존재하지 않습니다.");
      } else if (thumbnailFile.delete() == false) {
        throw new RuntimeException("썸네일 파일 삭제를 실패하였습니다.");
      }
    }
    thumbnailRepository.deleteById(deleteId);
    return true;
  }
}
