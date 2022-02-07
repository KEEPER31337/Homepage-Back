package keeper.project.homepage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.common.ImageFormatChecking;
import keeper.project.homepage.dto.FileDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.exception.CustomFileNotFoundException;
import keeper.project.homepage.repository.FileRepository;
import keeper.project.homepage.repository.ThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

@RequiredArgsConstructor
@Service
public class FileService {

  private final FileRepository fileRepository;
  private final String defaultImageName = "default.jpg";
  private final ImageFormatChecking imageFormatChecking;

  public byte[] getImage(Long fileId) throws IOException {
    FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(
        () -> new CustomFileNotFoundException("이미지 파일을 찾을 수 없습니다.")
    );
    if (!imageFormatChecking.isImageFile(fileEntity.getFileName())) {
      throw new CustomFileNotFoundException("이미지 파일이 아닙니다.");
    }
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
    Timestamp timestamp = new Timestamp(System.nanoTime());
    fileName += timestamp.toString();
    fileName = encryptSHA256(fileName);
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
      throw new RuntimeException("파일을 서버에 저장하는 것을 실패하였습니다.");
    }
    return file;
  }


  public FileEntity saveFileEntity(File file, String relDirPath, String ipAddress,
      @Nullable PostingEntity postingEntity) {
    FileDto fileDto = new FileDto();
    fileDto.setFileName(file.getName());
    // DB엔 상대경로로 저장
    fileDto.setFilePath(relDirPath + File.separator + file.getName());
    fileDto.setFileSize(file.length());
    fileDto.setUploadTime(new Date());
    fileDto.setIpAddress(ipAddress);
    return fileRepository.save(fileDto.toEntity(postingEntity));
  }

  public FileEntity saveOriginalImage(MultipartFile multipartFile, String ipAddress) {
    if (multipartFile == null || multipartFile.isEmpty()) {
      File defaultFile = new File("keeper_files" + File.separator + defaultImageName);
      return saveFileEntity(defaultFile, "keeper_files", ipAddress, null);
    }
    if (imageFormatChecking.isImageFile(multipartFile) == false) {
      throw new RuntimeException("썸네일 용 이미지는 image 타입이어야 합니다.");
    }
    try {
      if (imageFormatChecking.isNormalImageFile(multipartFile) == false) {
        throw new RuntimeException("이미지 파일을 BufferedImage로 읽어들일 수 없습니다.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("이미지 파일을 읽는 것을 실패했습니다.");
    }
    File file = saveFileInServer(multipartFile, "keeper_files");
    return saveFileEntity(file, "keeper_files", ipAddress, null);
  }

  @Transactional
  public void saveFiles(List<MultipartFile> multipartFiles, String ipAddress,
      @Nullable PostingEntity postingEntity) {
    if (multipartFiles == null) {
      return;
    }

    for (MultipartFile multipartFile : multipartFiles) {
      File file = saveFileInServer(multipartFile, "keeper_files");
      saveFileEntity(file, "keeper_files", ipAddress, postingEntity);
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
    if (!fileRepository.findById(deleteId).isPresent()) {
      return false;
    }
    fileRepository.deleteById(deleteId);
    return true;
  }

  public boolean deleteOriginalImageById(Long deleteId) throws RuntimeException {
    // issue : 각 예외사항에서 return 대신 custom exception으로 수정
    // original image file을 가지고 있으면 서버에 있는 이미지는 삭제 X
    FileEntity deleted = fileRepository.findById(deleteId).orElse(null);
    if (deleted == null) {
      return false;
    }
    File originalImageFile = new File(
        System.getProperty("user.dir") + File.separator + deleted.getFilePath());
    String originalImageFileName = originalImageFile.getName();
    if (originalImageFileName.equals(defaultImageName) == false) {
      if (originalImageFile.exists() == false) {
        throw new RuntimeException("썸네일 원본 이미지가 이미 존재하지 않습니다.");
      } else if (originalImageFile.delete() == false) {
        throw new RuntimeException("썸네일 원본 이미지 삭제를 실패하였습니다.");
      }
    }
    fileRepository.deleteById(deleteId);
    return true;
  }
}
