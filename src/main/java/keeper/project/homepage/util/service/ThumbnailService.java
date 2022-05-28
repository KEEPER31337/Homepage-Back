package keeper.project.homepage.util.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import keeper.project.homepage.exception.file.CustomInvalidImageFileException;
import keeper.project.homepage.util.ImageFormatChecking;
import keeper.project.homepage.util.MultipartFileWrapper;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.util.ImageProcessing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

  private final static String THUMBNAIL_FORMAT = "jpg";

  private final ImageFormatChecking imageFormatChecking;
  private final ThumbnailRepository thumbnailRepository;
  private final FileService fileService;

  public enum Type {
    Thumbnail("keeper_files" + File.separator + "thumbnail"),
    Badge("keeper_files" + File.separator + "thumbnail" + File.separator + "badge");

    private final String saveDirPath;

    Type(String saveDirPath) {
      this.saveDirPath = saveDirPath;
    }

    public String getSaveDirPath() {
      return saveDirPath;
    }
  }

  // FIXME: 없애기
  public enum DefaultThumbnailInfo {
    ThumbMember(1L, 1L),
    ThumbPosting(2L, 2L),
    BadgeGradeFirst(3L, 3L),
    BadgeGradeSecond(4L, 4L),
    BadgeGraduate(5L, 5L),
    BadgeQuit(6L, 6L),
    BadgeSleep(7L, 7L),
    BadgeRegular(8L, 8L),
    ThumbInfo(9L, 9L),
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

  private class FilePair {

    private File originalFile;
    private File thumbnailFile;

    FilePair(File originalFile, File thumbnailFile) {
      this.originalFile = originalFile;
      this.thumbnailFile = thumbnailFile;
    }

    public File getOriginalFile() {
      return originalFile;
    }

    public File getThumbnailFile() {
      return thumbnailFile;
    }
  }

  private File getFileInServer(ThumbnailEntity thumbnailEntity) {
    String thumbnailPath =
        System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath();
    return fileService.getFileInServer(thumbnailPath);
  }

  private File getFileInServer(Long thumbnailId) {
    ThumbnailEntity thumbnail = findById(thumbnailId);
    return getFileInServer(thumbnail);
  }

  // FIXME: width, height 없애기
  public byte[] getByteArrayFromImage(Long thumbnailId, ImageProcessing imageProcessing,
      Integer width, Integer height) throws IOException {
    File file = getFileInServer(thumbnailId);
    return fileService.getByteArrayFromImage(file, imageProcessing, width, height);
  }

  public byte[] getByteArrayFromImage(Long thumbnailId) throws IOException {
    File file = getFileInServer(thumbnailId);
    return fileService.getByteArrayFromImage(file);
  }

  private ThumbnailEntity getDefaultThumbnailEntity(ThumbnailSize size) {
    ThumbnailEntity defaultThumbnail;
    // TODO : 목적에 따라 바꾸기
    if (size.equals(ThumbnailSize.LARGE)) { // -> rectangle
      fileService.find(DefaultThumbnailInfo.ThumbPosting.getFileId());
      defaultThumbnail = findById(DefaultThumbnailInfo.ThumbPosting.getThumbnailId());
    } else { // -> square
      fileService.find(DefaultThumbnailInfo.ThumbMember.getFileId());
      defaultThumbnail = findById(DefaultThumbnailInfo.ThumbMember.getThumbnailId());
    }
    return defaultThumbnail;
  }

  public ThumbnailEntity saveThumbnail(ImageProcessing imageProcessing, MultipartFile multipartFile,
      ThumbnailSize size, String ipAddress) {
    // TODO: 함수 없애기
    return save(Type.Thumbnail, imageProcessing, multipartFile, size, ipAddress);
  }

  public ThumbnailEntity save(Type type, ImageProcessing imageProcessing,
      MultipartFile multipartFile, ThumbnailSize size, String ipAddress) {

    if (isNullMultipartFile(type, multipartFile)) {
      return getDefaultThumbnailEntity(size); // 이 부분 badge랑 다름
    }

    FilePair saveFile = saveFilesInServer(type, imageProcessing, multipartFile, size);

    FileEntity fileEntity = fileService.saveFileEntity(saveFile.getOriginalFile(),
        FileService.fileRelDirPath,
        ipAddress, multipartFile.getOriginalFilename(), null);
    return thumbnailRepository.save(
        ThumbnailEntity.builder()
            .path(type.getSaveDirPath() + File.separator + saveFile.getThumbnailFile().getName())
            .file(fileEntity)
            .build());
  }

  private FilePair saveFilesInServer(Type type, ImageProcessing imageProcessing,
      MultipartFile multipartFile, ThumbnailSize size) {
    FilePair filePair;
    MultipartFileWrapper multipartFileWrapper = new MultipartFileWrapper(multipartFile);
    try {
      imageFormatChecking.checkNormalImageFile(multipartFileWrapper);
      File originalFile = fileService.saveFileInServer(multipartFileWrapper,
          FileService.fileRelDirPath);
      File thumbnailFile = fileService.saveFileInServer(multipartFileWrapper,
          type.getSaveDirPath());
      filePair = new FilePair(originalFile, thumbnailFile);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      multipartFileWrapper.transferFinish();
    }
    if (size != null) {
      imageProcessing.imageProcessing(filePair.getThumbnailFile(), size.getWidth(),
          size.getHeight(),
          THUMBNAIL_FORMAT);
    }
    return filePair;
  }

  private boolean isNullMultipartFile(Type type, MultipartFile multipartFile) {
    if (multipartFile == null || multipartFile.isEmpty()) {
      switch (type) {
        case Thumbnail:
          return true;
        case Badge:
          throw new CustomInvalidImageFileException();
      }
    }
    return false;
  }

  // FIXME: rename to "find"
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

  // FIXME: rename to "delete"
  public void deleteById(Long deleteId) {
    if (isDefaultThumbnail(deleteId)) {
      return;
    }

    ThumbnailEntity thumbnailEntity = findById(deleteId);
    FileEntity fileEntity = thumbnailEntity.getFile();
    String thumbnailPath = thumbnailEntity.getPath();
    fileService.deleteFile(fileEntity);
    fileService.deleteFileInServer(thumbnailPath);
    thumbnailRepository.deleteById(deleteId);
  }

  // FIXME: rename to "update"
  public ThumbnailEntity updateById(Long thumbnailId, Type type,
      ImageProcessing imageProcessing,
      MultipartFile multipartFile, ThumbnailSize size, String ipAddress) {
    ThumbnailEntity prevThumbnail = findById(thumbnailId);
    FileEntity prevFile = prevThumbnail.getFile();

    // 서버에 있는 파일 삭제
    fileService.deleteFileInServer(prevThumbnail.getPath());
    fileService.deleteFileInServer(prevFile.getFilePath());

    // 파일 새로 저장
    if (isNullMultipartFile(type, multipartFile)) {
      return getDefaultThumbnailEntity(size);
    }
    FilePair savedFile = saveFilesInServer(type, imageProcessing, multipartFile, size);

    // entity 수정
    FileEntity aftFile = fileService.updateFileEntity(prevFile.getId(),
        savedFile.getOriginalFile(),
        ipAddress);
    return thumbnailRepository.save(
        ThumbnailEntity.builder()
            .id(prevThumbnail.getId())
            .path(type.getSaveDirPath() + File.separator + savedFile.getThumbnailFile().getName())
            .file(aftFile)
            .build());
  }
}