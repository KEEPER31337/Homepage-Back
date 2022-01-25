package keeper.project.homepage.service;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import keeper.project.homepage.dto.FileDto;
import keeper.project.homepage.dto.PostingDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileService {

  // @Autowired안하면 fileService가 repository를 불러오지 못한다 .. 왜 ??
  @Autowired
  private FileRepository fileRepository;

  @Transactional
  public void saveFiles(List<MultipartFile> files, PostingDto dto, PostingEntity postingEntity) {
    if (files == null) {
      return;
    }

    String fileName;
    String filePath;
    Timestamp timeStamp;
    for (MultipartFile file : files) {
      fileName = file.getOriginalFilename();
      timeStamp = new Timestamp(System.nanoTime());
      fileName += timeStamp.toString(); // 파일명 중복 제거
      fileName = encryptSHA256(fileName); // SHA-256 암호화
      filePath = System.getProperty("user.dir") + "\\keeper_files"; //working directory + \\files
      if (!new File(filePath).exists()) {
        new File(filePath).mkdir();
      }
      try {
        filePath = filePath + "\\" + fileName;
        file.transferTo(new File(filePath));
      } catch (Exception e) {
        e.printStackTrace();
      }
      FileDto fileDto = new FileDto();
      fileDto.setFileName(fileName);
      // DB엔 상대경로로 저장
      fileDto.setFilePath("keeper_files\\" + fileName);
      fileDto.setFileSize(file.getSize());
      fileDto.setUploadTime(dto.getUpdateTime());
      fileDto.setIpAddress(dto.getIpAddress());
      fileRepository.save(fileDto.toEntity(postingEntity)).getId();
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

  public FileEntity saveThumbnail(MultipartFile originalImageFile, UUID uuid, String ipAddress) {
    if (originalImageFile == null) {
      // 나중에 default 이미지로 설정
      return null;
    }
    String fileName = uuid.toString() + "_" + originalImageFile.getOriginalFilename();
    String relFilePath = "keeper_files";
    String absFilePath = System.getProperty("user.dir") + "\\" + relFilePath;
    if (!new File(absFilePath).exists()) {
      new File(absFilePath).mkdir();
    }
    try {
      absFilePath = absFilePath + "\\" + fileName;
      File thumbnailFile = new File(absFilePath);
      originalImageFile.transferTo(thumbnailFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return fileRepository.save(
        FileEntity.builder()
            .fileName(fileName)
            .filePath(absFilePath)
            .fileSize(originalImageFile.getSize())
            .ipAddress(ipAddress)
            .build());
  }

  public boolean deleteById(Long deleteId) {
    if (fileRepository.findById(deleteId).isPresent()) {
      return false;
    }
    fileRepository.deleteById(deleteId);
    return true;
  }
}
