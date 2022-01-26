package keeper.project.homepage.service;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.dto.FileDto;
import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

@RequiredArgsConstructor
@Service
public class FileService {

  private final FileRepository fileRepository;
  private final String defaultImageName = "default.jpg";
  private final String[] enableImageFormat = {"jpg", "jpeg", "png", "gif"};

  public boolean isImageFile(MultipartFile multipartFile) {
    String contentType = multipartFile.getContentType();
    if (contentType.startsWith("image")) {
      for (String format : enableImageFormat) {
        if (contentType.endsWith(format)) {
          return true;
        }
      }
    }
    return false;
  }

  public File saveFileInServer(MultipartFile multipartFile, String relDirPath) throws Exception {
    if (multipartFile.isEmpty()) {
      return null;
    }
    String fileName = multipartFile.getOriginalFilename();
    Timestamp timestamp = new Timestamp(System.nanoTime());
    fileName += timestamp.toString();
    fileName = encryptSHA256(fileName);
    String absDirPath = System.getProperty("user.dir") + "\\" + relDirPath;
    if (!new File(absDirPath).exists()) {
      new File(absDirPath).mkdir();
    }
    String filePath = absDirPath + "\\" + fileName;
    File file = new File(filePath);
    multipartFile.transferTo(file);
    return file;
  }


  public FileEntity saveFileEntity(File file, String relDirPath, String ipAddress,
      @Nullable PostingEntity postingEntity) {
    FileDto fileDto = new FileDto();
    fileDto.setFileName(file.getName());
    // DB엔 상대경로로 저장
    fileDto.setFilePath(relDirPath + "\\" + file.getName());
    fileDto.setFileSize(file.length());
    fileDto.setUploadTime(new Date());
    fileDto.setIpAddress(ipAddress);
    return fileRepository.save(fileDto.toEntity(postingEntity));
  }

  public FileEntity saveOriginalImage(MultipartFile imageFile, String ipAddress) throws Exception {
    if (imageFile == null) {
      File defaultFile = new File("keeper_files\\" + defaultImageName);
      return saveFileEntity(defaultFile, "keeper_files", ipAddress, null);
    }
    if (isImageFile(imageFile) == false) {
      throw new Exception("썸네일 용 이미지는 image 타입이어야 합니다.");
    }
    File file = saveFileInServer(imageFile, "keeper_files");
    return saveFileEntity(file, "keeper_files", ipAddress, null);
  }

  @Transactional
  public void saveFiles(List<MultipartFile> multipartFiles, String ipAddress,
      @Nullable PostingEntity postingEntity) {
    if (multipartFiles == null) {
      return;
    }

    for (MultipartFile multipartFile : multipartFiles) {
      try {
        File file = saveFileInServer(multipartFile, "keeper_files");
        saveFileEntity(file, "keeper_files", ipAddress, postingEntity);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Transactional
  public FileEntity getFileById(Long id) {
    return fileRepository.findById(id).get();
  }

  @Transactional
  public List<FileEntity> getFilesByPostingId(PostingEntity postingEntity) {

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

  public boolean deleteById(Long deleteId) {
    if (fileRepository.findById(deleteId).isPresent()) {
      return false;
    }
    fileRepository.deleteById(deleteId);
    return true;
  }
}
