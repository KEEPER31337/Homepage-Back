package keeper.project.homepage.util.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import keeper.project.homepage.util.ImageFormatChecking;
import keeper.project.homepage.util.MultipartFileWrapper;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.exception.file.CustomFileNotFoundException;
import keeper.project.homepage.exception.file.CustomFileDeleteFailedException;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.util.ImageProcessing;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

  private final static String THUMBNAIL_FORMAT = "jpg";

  private final static String relDirPath = "keeper_files" + File.separator + "thumbnail";
  private final static String defaultImageName = "thumb_default.jpg"; // thumbnail delete시 파일명 비교
  // DB에는 디렉토리 구분자가 "/"
  public final static String DEFAULT_RELATIVE_DIR_PATH = relDirPath + File.separator + "default";

  // TODO : file과 thumbnail의 default id, name은 같지만, 그래도 각 service에 분리하기
  // TODO : 앞으로 default 더 생길 것이므로, class나 enum으로 정리하기
  public final static String DEFAULT_RECTAGLE_NAME = "default_thumbnail_rectangle.jpg";
  public final static String DEFAULT_SQUARE_NAME = "default_thumbnail_square.jpg";
  public final static Long DEFAULT_RECTANGLE_ID = 1L;
  public final static Long DEFAULT_SQUARE_ID = 2L;

  private final ImageFormatChecking imageFormatChecking;
  private final ThumbnailRepository thumbnailRepository;
  private final FileService fileService;

  public enum ThumbnailSize {
    SMALL(30, 30), LARGE(100, 100), STUDY(300, 300);
    private Integer width;
    private Integer height;

    ThumbnailSize(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public Integer getWidth() {
      return this.width;
    }

    public Integer getHeight() {
      return this.height;
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
      ThumbnailSize size, String ipAddress) {

    FileEntity fileEntity = null;
    String fileName = "";
    if (multipartFile == null || multipartFile.isEmpty()) {
      FileEntity defaultFile = null;
      ThumbnailEntity defaultThumbnail = null;
      if (size.equals(ThumbnailSize.LARGE)) { // -> rectangle
        defaultFile = fileService.findFileEntityById(DEFAULT_RECTANGLE_ID);
        defaultThumbnail = findById(DEFAULT_RECTANGLE_ID);
      } else { // -> square
        defaultFile = fileService.findFileEntityById(DEFAULT_SQUARE_ID);
        defaultThumbnail = findById(DEFAULT_SQUARE_ID);
      }
      return defaultThumbnail;
//      fileName = this.defaultImageName;
//      File defaultFile = new File(
//          FileService.fileRelDirPath + File.separator + FileService.defaultImageFileName);
//      fileEntity = fileService.saveFileEntity(
//          defaultFile, FileService.fileRelDirPath, ipAddress, FileService.defaultImageFileName,
//          null);
    } else {
      MultipartFileWrapper multipartFileWrapper = new MultipartFileWrapper(multipartFile);
      try {
        imageFormatChecking.checkNormalImageFile(multipartFileWrapper);

        // 원본 파일 저장
        File file = fileService.saveFileInServer(multipartFileWrapper, FileService.fileRelDirPath);
        fileEntity = fileService.saveFileEntity(file, FileService.fileRelDirPath, ipAddress,
            multipartFile.getOriginalFilename(), null);

        // 썸네일 파일 저장
        File thumbnailImage = fileService.saveFileInServer(multipartFileWrapper, this.relDirPath);
        if (size != null) {
          imageProcessing.imageProcessing(thumbnailImage, size.getWidth(), size.getHeight(),
              THUMBNAIL_FORMAT);
        }
        fileName = thumbnailImage.getName();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        // 업로드 임시 파일 삭제
        multipartFileWrapper.transferFinish();
      }
      return thumbnailRepository.save(
          ThumbnailEntity.builder()
              .path(this.relDirPath + File.separator + fileName)
              .file(fileEntity)
              .build());
    }
  }

  public ThumbnailEntity findById(Long findId) {
    return thumbnailRepository.findById(findId)
        .orElseThrow(CustomThumbnailEntityNotFoundException::new);
  }

  public void deleteById(Long deleteId) {
    // 기본 썸네일이면 삭제 X
    if (deleteId == DEFAULT_RECTANGLE_ID || deleteId == DEFAULT_SQUARE_ID) {
      return;
    }
    ThumbnailEntity deleted = findById(deleteId);
    File thumbnailFile = new File(
        System.getProperty("user.dir") + File.separator + deleted.getPath());
    if (thumbnailFile.exists() == false) {
      throw new CustomFileNotFoundException();
    } else if (thumbnailFile.delete() == false) {
      throw new CustomFileDeleteFailedException();
    }
    thumbnailRepository.deleteById(deleteId);
  }
}