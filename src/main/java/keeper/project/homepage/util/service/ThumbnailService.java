package keeper.project.homepage.util.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

  private final ImageFormatChecking imageFormatChecking;
  private final ThumbnailRepository thumbnailRepository;
  private final FileService fileService;

  // TODO : 뱃지는 계속 추가됨 -> 뱃지 추가하는 controller & service 만들기
  public enum DefaultThumbnailInfo {
    ThumbMember(1L, 1L),
    ThumbPosting(2L, 2L),
    BadgeGradeFirst(3L, 3L),
    BadgeGradeSecond(4L, 4L),
    BadgeGraduate(5L, 5L),
    BadgeQuit(6L, 6L),
    BadgeSleep(7L, 7L),
    BadgeRegular(8L, 8L),
    ThumbBook(10L, 10L);

    private final Long thumbnailId;
    private final Long fileId;

    DefaultThumbnailInfo(Long thumbnailId, Long fileId) {
      this.thumbnailId = thumbnailId;
      this.fileId = fileId;
    }

    public Long getThumbnailId() {
      return this.thumbnailId;
    }

    public Long getFileId() {
      return this.fileId;
    }

  }

  public enum ThumbnailSize {
    SMALL(100, 100), LARGE(500, 500), STUDY(500, 500);
    private final Integer width;
    private final Integer height;

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
    // default 이미지 추가
    if (multipartFile == null || multipartFile.isEmpty()) {
      FileEntity defaultFile = null;
      ThumbnailEntity defaultThumbnail = null;
      // TODO : 목적에 따라 바꾸기
      if (size.equals(ThumbnailSize.LARGE)) { // -> rectangle
        defaultFile = fileService.findFileEntityById(DefaultThumbnailInfo.ThumbPosting.getFileId());
        defaultThumbnail = findById(DefaultThumbnailInfo.ThumbPosting.getThumbnailId());
      } else { // -> square
        defaultFile = fileService.findFileEntityById(DefaultThumbnailInfo.ThumbMember.getFileId());
        defaultThumbnail = findById(DefaultThumbnailInfo.ThumbMember.getThumbnailId());
      }
      return defaultThumbnail;

    }

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

  public ThumbnailEntity findById(Long findId) {
    return thumbnailRepository.findById(findId)
        .orElseThrow(CustomThumbnailEntityNotFoundException::new);
  }

  public void deleteById(Long deleteId) {
    // 기본 썸네일이면 삭제 X
    List<Long> defaultIdList = Stream.of(DefaultThumbnailInfo.values())
        .map(t -> t.getThumbnailId())
        .collect(Collectors.toList());
    if (defaultIdList.contains(deleteId)) {
      return;
    }

    ThumbnailEntity deleted = findById(deleteId);
    File thumbnailFile = new File(
        System.getProperty("user.dir") + File.separator + deleted.getPath());
    if (thumbnailFile.exists() == false) {
      throw new CustomFileNotFoundException(
          "썸네일 파일이 존재하지 않습니다." + " (file path : " + thumbnailFile.getPath() + ")");
    } else if (thumbnailFile.delete() == false) {
      throw new CustomFileDeleteFailedException(
          "썸네일 파일 삭제를 실패하였습니다." + " (file path : " + thumbnailFile.getPath() + ")");
    }
    thumbnailRepository.deleteById(deleteId);
  }
}