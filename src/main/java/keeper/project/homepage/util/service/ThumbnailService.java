package keeper.project.homepage.util.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import keeper.project.homepage.exception.file.CustomInvalidImageFileException;
import keeper.project.homepage.util.ImageFormatChecking;
import keeper.project.homepage.util.MultipartFileWrapper;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.exception.file.CustomFileNotFoundException;
import keeper.project.homepage.exception.file.CustomFileDeleteFailedException;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.util.ImageProcessing;
import keeper.project.homepage.util.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

  private final static String THUMBNAIL_FORMAT = "jpg";

  private final static String relDirPath = "keeper_files" + File.separator + "thumbnail";
  private final static String relBadgeDirPath =
      "keeper_files" + File.separator + "thumbnail" + File.separator + "badge";

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

  public byte[] getByteArrayForThumbnailImage(Long thumbnailId, ImageProcessing imageProcessing,
      Integer width, Integer height) throws IOException {
    /**
     * @return byte array for preprocessed thumbnail image
     */
    File file = getThumbnailFile(thumbnailId);
    imageProcessing.imageProcessing(file, width, height, "jpg");
    InputStream in = new FileInputStream(file);
    return IOUtils.toByteArray(in);
  }

  public byte[] getByteArrayForThumbnailImage(Long thumbnailId) throws IOException {
    /**
     * @return byte array for original thumbnail image
     */
    File file = getThumbnailFile(thumbnailId);
    InputStream in = new FileInputStream(file);
    return IOUtils.toByteArray(in);
  }

  private File getThumbnailFile(Long thumbnailId) {
    ThumbnailEntity thumbnail = findById(thumbnailId);
    String thumbnailPath = System.getProperty("user.dir") + File.separator + thumbnail.getPath();
    File imageFile = new File(thumbnailPath);
    if (imageFile.exists() == false) {
      throw new CustomFileNotFoundException();
    }
    return imageFile;
  }

  private ThumbnailEntity getDefaultThumbnailEntity(ThumbnailSize size) {
    ThumbnailEntity defaultThumbnail = null;
    // TODO : 목적에 따라 바꾸기
    if (size.equals(ThumbnailSize.LARGE)) { // -> rectangle
      fileService.findFileEntityById(DefaultThumbnailInfo.ThumbPosting.getFileId());
      defaultThumbnail = findById(DefaultThumbnailInfo.ThumbPosting.getThumbnailId());
    } else { // -> square
      fileService.findFileEntityById(DefaultThumbnailInfo.ThumbMember.getFileId());
      defaultThumbnail = findById(DefaultThumbnailInfo.ThumbMember.getThumbnailId());
    }
    return defaultThumbnail;
  }

  public ThumbnailEntity saveThumbnail(ImageProcessing imageProcessing, MultipartFile multipartFile,
      ThumbnailSize size, String ipAddress) {

    File originalFile = null;
    File thumbnailImage = null;
    // default 이미지 반환
    if (multipartFile == null || multipartFile.isEmpty()) {
      return getDefaultThumbnailEntity(size);
    }

    MultipartFileWrapper multipartFileWrapper = new MultipartFileWrapper(multipartFile);
    try {
      imageFormatChecking.checkNormalImageFile(multipartFileWrapper);
      // 원본 파일 저장
      originalFile = fileService.saveFileInServer(multipartFileWrapper, FileService.fileRelDirPath);

      // 썸네일 파일 저장
      thumbnailImage = fileService.saveFileInServer(multipartFileWrapper, this.relDirPath);
      if (size != null) {
        imageProcessing.imageProcessing(thumbnailImage, size.getWidth(), size.getHeight(),
            THUMBNAIL_FORMAT);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // 업로드 임시 파일 삭제
      multipartFileWrapper.transferFinish();
    }

    FileEntity fileEntity = fileService.saveFileEntity(originalFile, FileService.fileRelDirPath,
        ipAddress, multipartFile.getOriginalFilename(), null);
    return thumbnailRepository.save(
        ThumbnailEntity.builder()
            .path(this.relDirPath + File.separator + thumbnailImage.getName())
            .file(fileEntity)
            .build());

  }


  public ThumbnailEntity saveBadge(ImageProcessing imageProcessing, MultipartFile multipartFile,
      ThumbnailSize size, String ipAddress) {

    File badgeImage = saveBadgeImage(imageProcessing, multipartFile, size);

    FileEntity fileEntity = fileService.saveFileEntity(badgeImage, this.relBadgeDirPath, ipAddress,
        multipartFile.getOriginalFilename(), null);

    return thumbnailRepository.save(
        ThumbnailEntity.builder()
            .path(this.relBadgeDirPath + File.separator + badgeImage.getName())
            .file(fileEntity)
            .build());
  }

  private File saveBadgeImage(ImageProcessing imageProcessing, MultipartFile multipartFile,
      ThumbnailSize size) {
    if (multipartFile == null || multipartFile.isEmpty()) {
      throw new CustomInvalidImageFileException();
    }
    MultipartFileWrapper multipartFileWrapper = new MultipartFileWrapper(multipartFile);
    File badgeImage = null;
    try {
      imageFormatChecking.checkNormalImageFile(multipartFileWrapper);
      badgeImage = fileService.saveFileInServer(multipartFileWrapper, this.relBadgeDirPath);
      if (size != null) {
        imageProcessing.imageProcessing(badgeImage, size.getWidth(), size.getHeight(),
            THUMBNAIL_FORMAT);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      multipartFileWrapper.transferFinish();
    }
    return badgeImage;
  }

  public ThumbnailEntity findById(Long findId) {
    return thumbnailRepository.findById(findId)
        .orElseThrow(CustomThumbnailEntityNotFoundException::new);
  }

  private boolean isDefaultThumbnail(Long deleteId) {
    List<Long> defaultIdList = Stream.of(DefaultThumbnailInfo.values())
        .map(t -> t.getThumbnailId())
        .collect(Collectors.toList());
    return defaultIdList.contains(deleteId);
  }

  private void deleteThumbnailFile(ThumbnailEntity deleted) {
    File thumbnailFile = new File(
        System.getProperty("user.dir") + File.separator + deleted.getPath());
    if (thumbnailFile.exists() == false) {
      throw new CustomFileNotFoundException(
          "썸네일 파일이 존재하지 않습니다." + " (file path : " + thumbnailFile.getPath() + ")");
    } else if (thumbnailFile.delete() == false) {
      throw new CustomFileDeleteFailedException(
          "썸네일 파일 삭제를 실패하였습니다." + " (file path : " + thumbnailFile.getPath() + ")");
    }
  }

  public void deleteById(Long deleteId) {
    if (isDefaultThumbnail(deleteId)) {
      return;
    }

    ThumbnailEntity deleted = findById(deleteId);
    deleteThumbnailFile(deleted);
    thumbnailRepository.deleteById(deleteId);
  }

  public void deleteBadge(Long deleteId) {
    ThumbnailEntity deleted = findById(deleteId);
    deleteThumbnailFile(deleted);
    thumbnailRepository.deleteById(deleteId);
    fileService.deleteFileEntityById(deleted.getFile().getId());
  }

  public ThumbnailEntity updateBadge(Long badgeId, ImageProcessing imageProcessing,
      MultipartFile multipartFile, ThumbnailSize size, String ipAddress) {
    ThumbnailEntity prevBadge = findById(badgeId);
    FileEntity prevBadgeFile = prevBadge.getFile();
    // 서버에 있는 파일 삭제
    deleteThumbnailFile(prevBadge);
    // 파일 새로 저장
    File newBadgeImage = saveBadgeImage(imageProcessing, multipartFile, size);
    fileService.saveFileInServer(multipartFile, this.relBadgeDirPath);
    String fileName = multipartFile.getOriginalFilename();
    String fileRelPath = this.relBadgeDirPath + File.separator + fileName;
    // entity 수정
    FileDto fileDto = FileDto.toDto(prevBadgeFile);
    fileDto.setFileName(fileName);
    fileDto.setFilePath(fileRelPath);
    fileDto.setFileSize(newBadgeImage.length());
    fileDto.setUploadTime(LocalDateTime.now());
    fileDto.setIpAddress(ipAddress);
    FileEntity aftBadgeFile = fileService.updateFileEntity(prevBadgeFile.getId(), fileDto);

    return thumbnailRepository.save(
        ThumbnailEntity.builder()
            .id(prevBadge.getId())
            .path(fileRelPath)
            .file(aftBadgeFile)
            .build());
  }
}