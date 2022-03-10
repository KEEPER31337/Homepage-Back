package keeper.project.homepage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.util.ImageFormatChecking;
import keeper.project.homepage.dto.FileDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.common.entity.posting.PostingEntity;
import keeper.project.homepage.exception.file.CustomFileNotFoundException;
import keeper.project.homepage.exception.file.CustomFileDeleteFailedException;
import keeper.project.homepage.exception.file.CustomFileEntityNotFoundException;
import keeper.project.homepage.exception.file.CustomFileTransferFailedException;
import keeper.project.homepage.repository.FileRepository;
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
  public static final String defaultImageFileName = "default.jpg";

  private final FileRepository fileRepository;
  private final ImageFormatChecking imageFormatChecking;

  private String encodeFileName(String fileName) {
    String[] fileFormatSplitArray = fileName.split("\\.");
    String fileFormat = fileFormatSplitArray[fileFormatSplitArray.length - 1];
    Timestamp timestamp = new Timestamp(System.nanoTime());
    fileName += timestamp.toString();
    fileName = encryptSHA256(fileName) + "." + fileFormat;
    return fileName;
  }

  public byte[] getImage(Long fileId) throws IOException {
    FileEntity fileEntity = fileRepository.findById(fileId)
        .orElseThrow(CustomFileEntityNotFoundException::new);
    imageFormatChecking.checkImageFile(fileEntity.getFileName());
    String filePath = System.getProperty("user.dir") + File.separator + fileEntity.getFilePath();
    File file = new File(filePath);
    InputStream in = new FileInputStream(file);

    return IOUtils.toByteArray(in);
  }

  public File saveFileInServer(MultipartFile multipartFile, String relDirPath) {
    if (multipartFile.isEmpty()) {
      return null;
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

  public FileEntity saveFileEntity(File file, String relDirPath, String ipAddress, String origName,
      @Nullable PostingEntity postingEntity) {
    FileDto fileDto = new FileDto();
    fileDto.setFileName(origName);
    // DB엔 상대경로로 저장
    fileDto.setFilePath(relDirPath + File.separator + file.getName());
    fileDto.setFileSize(file.length());
    fileDto.setUploadTime(LocalDateTime.now());
    fileDto.setIpAddress(ipAddress);
    return fileRepository.save(fileDto.toEntity(postingEntity));
  }

  @Transactional
  public void saveFiles(List<MultipartFile> multipartFiles, String ipAddress,
      @Nullable PostingEntity postingEntity) {
    if (multipartFiles == null) {
      return;
    }

    for (MultipartFile multipartFile : multipartFiles) {
      File file = saveFileInServer(multipartFile, fileRelDirPath);
      saveFileEntity(file, fileRelDirPath, ipAddress, multipartFile.getOriginalFilename(),
          postingEntity);
    }
  }

  @Transactional
  public FileEntity findFileEntityById(Long id) {
    return fileRepository.findById(id).orElseThrow(CustomFileEntityNotFoundException::new);
  }

  @Transactional
  public List<FileEntity> findFileEntitiesByPostingId(PostingEntity postingEntity) {
    return fileRepository.findAllByPostingId(postingEntity);
  }

  @Transactional
  public void deleteFiles(List<FileEntity> fileEntities) {
    try {
      for (FileEntity fileEntity : fileEntities) {
        new File(System.getProperty("user.dir") + fileEntity.getFilePath()).delete();
        fileRepository.delete(fileEntity);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void deleteFileEntityById(Long deleteId) {
    fileRepository.findById(deleteId).orElseThrow(CustomFileEntityNotFoundException::new);
    fileRepository.deleteById(deleteId);
  }

  public void deleteOriginalThumbnail(ThumbnailEntity deleteThumbnail) {
    Long deleteId = deleteThumbnail.getFile().getId();
    FileEntity deleted = fileRepository.findById(deleteId)
        .orElseThrow(CustomFileEntityNotFoundException::new);
    if (!(fileRelDirPath + File.separator + defaultImageFileName).equals(
        deleted.getFilePath())) { // 기본 이미지면 삭제 X
      File originalImageFile = new File(
          System.getProperty("user.dir") + File.separator + deleted.getFilePath());
      String originalImageFileName = originalImageFile.getName();
      if (originalImageFileName.equals(defaultImageFileName) == false) {
        if (originalImageFile.exists() == false) {
          throw new CustomFileNotFoundException();
        }
        if (originalImageFile.delete() == false) {
          throw new CustomFileDeleteFailedException();
        }
      }
    }
    deleteFileEntityById(deleteId);
  }

  public String encryptSHA256(String str) {
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
