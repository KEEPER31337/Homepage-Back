package keeper.project.homepage.util.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import keeper.project.homepage.util.image.ImageFormatChecking;
import keeper.project.homepage.util.image.preprocessing.ImageProcessing;
import keeper.project.homepage.util.dto.FileDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.exception.file.CustomFileNotFoundException;
import keeper.project.homepage.exception.file.CustomFileDeleteFailedException;
import keeper.project.homepage.exception.file.CustomFileEntityNotFoundException;
import keeper.project.homepage.exception.file.CustomFileTransferFailedException;
import keeper.project.homepage.repository.FileRepository;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

@RequiredArgsConstructor
@Service
public class FileService {

  public static final String fileRelDirPath = "keeper_files"; // {user.dir}/keeper_files/

  private final FileRepository fileRepository;
  private final ImageFormatChecking imageFormatChecking;

  public byte[] getByteArrayFromImage(Long fileId, ImageProcessing imageProcessing,
      Integer width, Integer height) throws IOException {
    File file = getFileInServer(fileId);
    return getByteArrayFromImage(file, imageProcessing, width, height);
  }

  public byte[] getByteArrayFromImage(Long fileId) throws IOException {
    File file = getFileInServer(fileId);
    return getByteArrayFromImage(file);
  }

  // FIXME: width, height  없애기
  //  imageProcessing이 있는 함수로 하나로 합치기
  public byte[] getByteArrayFromImage(File file, ImageProcessing imageProcessing,
      Integer width, Integer height) throws IOException {
    /**
     * @return byte array for preprocessed image
     */
    imageFormatChecking.checkImageFile(file.getName());
    imageProcessing.imageProcessing(file, width, height, "jpg");
    InputStream in = new FileInputStream(file);
    return IOUtils.toByteArray(in);
  }

  public byte[] getByteArrayFromImage(File file) throws IOException {
    /**
     * @return byte array for original image
     */
    imageFormatChecking.checkImageFile(file.getName());
    InputStream in = new FileInputStream(file);
    return IOUtils.toByteArray(in);
  }

  public File getFileInServer(String filePath) {
    File file = new File(filePath);
    if (file.exists() == false) {
      throw new CustomFileNotFoundException();
    }
    return file;
  }

  private File getFileInServer(FileEntity fileEntity) {
    String filePath = System.getProperty("user.dir") + File.separator + fileEntity.getFilePath();
    return getFileInServer(filePath);
  }

  private File getFileInServer(Long fileId) {
    return getFileInServer(find(fileId));
  }

  public File saveFileInServer(MultipartFile multipartFile, String relDirPath) {
    if (multipartFile.isEmpty()) {
      return null; // FIXME: FileEmpty exception
    }
    String fileName = multipartFile.getOriginalFilename();
    fileName = encodeFileName(fileName);
    String absDirPath = System.getProperty("user.dir") + File.separator + relDirPath;
    if (!new File(absDirPath).exists()) {
      new File(absDirPath).mkdir();
    }
    String filePath = absDirPath + File.separator + fileName;
    File file = new File(filePath);
    try {
      multipartFile.transferTo(file);
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomFileTransferFailedException();
    }
    return file;
  }

  @Transactional
  public FileEntity saveFileEntity(File file, String relDirPath, String ipAddress, String origName,
      @Nullable PostingEntity postingEntity) {
    FileDto fileDto = new FileDto();
    fileDto.setFileName(origName);
    fileDto.setFilePath(relDirPath + File.separator + file.getName());
    fileDto.setFileSize(file.length());
    fileDto.setUploadTime(LocalDateTime.now());
    fileDto.setIpAddress(ipAddress);
    return fileRepository.save(fileDto.toEntity(postingEntity));
  }

  @Transactional
  public void saveFiles(List<MultipartFile> multipartFiles, String ipAddress,
      @Nullable PostingEntity postingEntity) {
    // FIXME: multipartFile이  null일 때, exception을 보내도 되는지 확인하기
    //  posting에서 쓰임
    if (multipartFiles == null) {
      return;
    }

    for (MultipartFile multipartFile : multipartFiles) {
      saveFile(multipartFile, ipAddress, postingEntity);
    }
  }

  @Transactional
  public FileEntity saveFile(MultipartFile multipartFile, String ipAddress,
      @Nullable PostingEntity postingEntity) {
    // FIXME: multipartFile이  null일 때, exception을 보내도 되는지 확인하기
    //  ctf에서 쓰임
    if (multipartFile == null) {
      return null;
    }

    File file = saveFileInServer(multipartFile, fileRelDirPath);
    return saveFileEntity(file, fileRelDirPath, ipAddress, multipartFile.getOriginalFilename(),
        postingEntity);
  }

  public FileEntity find(Long id) {
    return fileRepository.findById(id).orElseThrow(CustomFileEntityNotFoundException::new);
  }

  public List<FileEntity> findAllByPostingId(PostingEntity postingEntity) {
    return fileRepository.findAllByPostingId(postingEntity);
  }

  @Transactional
  public void deleteFiles(List<FileEntity> fileEntities) {
    for (FileEntity fileEntity : fileEntities) {
      deleteFile(fileEntity);
    }
  }

  @Transactional
  public void deleteFilesByIdList(List<Long> fileIdList) {
    for (Long fileId : fileIdList) {
      deleteFile(fileId);
    }
  }

  @Transactional
  public void deleteFile(FileEntity fileEntity) {
    // 기본 썸네일이면 삭제하지 않는다.
    if (isDefaultFileId(fileEntity.getId())) {
      throw new CustomFileDeleteFailedException("삭제할 수 없는 기본 이미지입니다.");
    }
    deleteFileInServer(fileEntity);
    deleteFileEntity(fileEntity.getId());
  }

  @Transactional
  public void deleteFile(Long fileId) {
    deleteFile(find(fileId));
  }

  @Transactional
  public void deleteFileEntity(Long deleteId) {
    find(deleteId);
    fileRepository.deleteById(deleteId);
  }

  public void deleteFileInServer(FileEntity fileEntity) {
    File file = new File(
        System.getProperty("user.dir") + File.separator + fileEntity.getFilePath());
    deleteFileInServer(file);
  }

  public void deleteFileInServer(String filePath) {
    File deleteFile = new File(System.getProperty("user.dir") + File.separator + filePath);
    deleteFileInServer(deleteFile);
  }

  public void deleteFileInServer(File file) {
    if (file.exists() == false) {
      throw new CustomFileNotFoundException();
    }
    if (file.delete() == false) {
      throw new CustomFileDeleteFailedException();
    }
  }

  public boolean isDefaultFileId(Long fileId) {
    List<Long> defaultIdList = Stream.of(DefaultThumbnailInfo.values()).map(t -> t.getFileId())
        .collect(Collectors.toList());
    if (defaultIdList.contains(fileId)) {
      return true;
    } else {
      return false;
    }
  }

  // TODO : thumbnail delete와 합치기
  public void deleteOriginalThumbnail(ThumbnailEntity deleteThumbnail) {
    Long deleteId = deleteThumbnail.getFile().getId();
    deleteFile(deleteId);
  }

  public FileEntity updateFileEntity(Long fileId, File file, String ip) {
    FileEntity prevEntity = find(fileId);
    FileEntity aftEntity = FileEntity.builder().id(fileId)
        .fileName(file.getName())
        .filePath(fileRelDirPath + File.separator + file.getName())
        .fileSize(file.length())
        .uploadTime(LocalDateTime.now())
        .ipAddress(ip)
        .postingId(prevEntity.getPostingId())
        .build();
    return fileRepository.save(aftEntity);
  }

  private String encodeFileName(String fileName) {
    String[] fileFormatSplitArray = fileName.split("\\.");
    String fileFormat = fileFormatSplitArray[fileFormatSplitArray.length - 1];
    Timestamp timestamp = new Timestamp(System.nanoTime());
    fileName += timestamp.toString();
    fileName = encryptSHA256(fileName) + "." + fileFormat;
    return fileName;
  }

  private String encryptSHA256(String str) {
    String sha = "";
    try {
      MessageDigest sh = MessageDigest.getInstance("SHA-256");
      sh.update(str.getBytes());
      byte[] byteData = sh.digest();
      StringBuilder sb = new StringBuilder();
      for (byte byteDatum : byteData) {
        sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
      }
      sha = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      sha = null;
    }
    return sha;
  }

}
