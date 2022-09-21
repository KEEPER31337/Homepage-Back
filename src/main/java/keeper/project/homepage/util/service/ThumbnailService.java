package keeper.project.homepage.util.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import keeper.project.homepage.util.exception.file.CustomInvalidImageFileException;
import keeper.project.homepage.util.image.ImageFormatChecking;
import keeper.project.homepage.util.MultipartFileWrapper;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.util.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.util.repository.ThumbnailRepository;
import keeper.project.homepage.util.image.preprocessing.ImagePreprocessing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

  private final static String THUMBNAIL_FORMAT = "jpg";

  private final ImageFormatChecking imageFormatChecking;
  private final ThumbnailRepository thumbnailRepository;
  private final FileService fileService;

  public enum ThumbType {
    MemberThumbnail("keeper_files" + File.separator + "thumbnail",
        DefaultThumbnailInfo.ThumbMember),
    PostThumbnail("keeper_files" + File.separator + "thumbnail", DefaultThumbnailInfo.ThumbPosting),
    BookThumbnail("keeper_files" + File.separator + "thumbnail", DefaultThumbnailInfo.ThumbBook),
    InfoThumbnail("keeper_files" + File.separator + "thumbnail", DefaultThumbnailInfo.ThumbInfo),
    // FIXME: Study용 default 이미지 있는지 확인하기
    StudyThumbnail("keeper_files" + File.separator + "thumbnail",
        DefaultThumbnailInfo.ThumbPosting),
    Badge("keeper_files" + File.separator + "thumbnail" + File.separator + "badge", null);

    private final String saveDirPath;
    private final DefaultThumbnailInfo defaultImage;

    ThumbType(String saveDirPath, DefaultThumbnailInfo defaultImage) {
      this.saveDirPath = saveDirPath;
      this.defaultImage = defaultImage;
    }

    public String getSaveDirPath() {
      return saveDirPath;
    }

    public DefaultThumbnailInfo getDefault() {
      return defaultImage;
    }

    public Long getDefaultThumbnailId() {
      if (defaultImage == null) {
        return null;
      }
      return defaultImage.getThumbnailId();
    }

    public Long getDefaultFileId() {
      if (defaultImage == null) {
        return null;
      }
      return defaultImage.getFileId();
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
    return fileService.getFileInServer(thumbnailEntity.getPath());
  }

  private File getFileInServer(Long thumbnailId) {
    ThumbnailEntity thumbnail = find(thumbnailId);
    return getFileInServer(thumbnail);
  }

  public byte[] getByteArrayFromImage(Long thumbnailId, ImagePreprocessing imagePreprocessing)
      throws IOException {
    File file = getFileInServer(thumbnailId);
    return fileService.getByteArrayFromImage(file, imagePreprocessing);
  }

  private ThumbnailEntity getDefaultThumbnailEntity(ThumbType type) {
    if (type.getDefaultThumbnailId() == null) {
      throw new CustomInvalidImageFileException();
    }
    return find(type.getDefaultThumbnailId());
  }

  @Transactional
  public ThumbnailEntity save(ThumbType type, ImagePreprocessing imagePreprocessing,
      MultipartFile multipartFile, String ipAddress) {

    if (isNullMultipartFile(multipartFile)) {
      return getDefaultThumbnailEntity(type); // 이 부분 badge랑 다름
    }

    FilePair saveFile = saveFilesInServer(type, imagePreprocessing, multipartFile);

    FileEntity fileEntity = fileService.saveFileEntity(saveFile.getOriginalFile(),
        FileService.fileRelDirPath,
        ipAddress, multipartFile.getOriginalFilename(), null);
    return thumbnailRepository.save(
        ThumbnailEntity.builder()
            .path(type.getSaveDirPath() + File.separator + saveFile.getThumbnailFile().getName())
            .file(fileEntity)
            .build());
  }

//  TODO : 타입 분기 고려 / 현재는 overloading
  @Transactional
  public ThumbnailEntity save(ThumbType type, ImagePreprocessing imagePreprocessing,
      MultipartFile multipartFile, String ipAddress, PostingEntity postingEntity) {

    if (isNullMultipartFile(multipartFile)) {
      return getDefaultThumbnailEntity(type);
    }

    FilePair saveFile = saveFilesInServer(type, imagePreprocessing, multipartFile);

    FileEntity fileEntity = fileService.saveFileEntity(
        saveFile.getOriginalFile(),
        FileService.fileRelDirPath,
        ipAddress,
        multipartFile.getOriginalFilename(),
        postingEntity);
    
    return thumbnailRepository.save(
        ThumbnailEntity.builder()
            .path(type.getSaveDirPath() + File.separator + saveFile.getThumbnailFile().getName())
            .file(fileEntity)
            .build());
  }

  private FilePair saveFilesInServer(ThumbType type, ImagePreprocessing imagePreprocessing,
      MultipartFile multipartFile) {
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
    imagePreprocessing.imageProcessing(filePair.getThumbnailFile(), THUMBNAIL_FORMAT);
    return filePair;
  }

  private boolean isNullMultipartFile(MultipartFile multipartFile) {
    if (multipartFile == null || multipartFile.isEmpty()) {
      return true;
    }
    return false;
  }

  public ThumbnailEntity find(Long findId) {
    return thumbnailRepository.findById(findId)
        .orElseThrow(CustomThumbnailEntityNotFoundException::new);
  }

  public boolean isDefaultThumbnail(Long deleteId) {
    List<Long> defaultIdList = Stream.of(DefaultThumbnailInfo.values())
        .map(t -> t.getThumbnailId())
        .collect(Collectors.toList());
    return defaultIdList.contains(deleteId);
  }

  @Transactional
  public void delete(Long deleteId) {
    // 기본 썸네일이면 삭제하지 않는다.
    if (isDefaultThumbnail(deleteId)) {
      return;
    }

    ThumbnailEntity thumbnailEntity = find(deleteId);
    FileEntity fileEntity = thumbnailEntity.getFile();
    String thumbnailPath = thumbnailEntity.getPath();
    fileService.deleteFileInServer(thumbnailPath);
    thumbnailRepository.deleteById(deleteId);
    fileService.deleteFile(fileEntity);
  }

  @Transactional
  public ThumbnailEntity update(Long thumbnailId, ThumbType type,
      ImagePreprocessing imagePreprocessing,
      MultipartFile multipartFile, String ipAddress) {
    ThumbnailEntity prevThumbnail = find(thumbnailId);
    FileEntity prevFile = prevThumbnail.getFile();

    // 서버에 있는 파일 삭제
    fileService.deleteFileInServer(prevThumbnail.getPath());
    fileService.deleteFileInServer(prevFile.getFilePath());

    // 파일 새로 저장
    if (isNullMultipartFile(multipartFile)) {
      return getDefaultThumbnailEntity(type);
    }
    FilePair savedFile = saveFilesInServer(type, imagePreprocessing, multipartFile);

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