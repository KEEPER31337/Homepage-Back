package keeper.project.homepage.service;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import keeper.project.homepage.dto.FileDto;
import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileService {

  private final FileRepository fileRepository;
  private final String defaultImageName = "default.jpg";

  public File saveFile(MultipartFile multipartFile, String relDirPath) throws Exception {
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

  public FileEntity saveFileEntity(PostingDto dto, PostingEntity postingEntity,
      File file, String relDirPath) {
    FileDto fileDto = new FileDto();
    fileDto.setFileName(file.getName());
    // DB엔 상대경로로 저장
    fileDto.setFilePath(relDirPath + "\\" + file.getName());
    fileDto.setFileSize(file.length());
    fileDto.setUploadTime(dto.getUpdateTime());
    fileDto.setIpAddress(dto.getIpAddress());
    return fileRepository.save(fileDto.toEntity(postingEntity));
  }

  public FileEntity saveOriginalImage(File originalImageFile, PostingDto dto) {
    if (originalImageFile == null) {
      File defaultFile = new File("keeper_files\\" + defaultImageName);
      return saveFileEntity(dto, null, defaultFile, "keeper_files");
    }
    return saveFileEntity(dto, null, originalImageFile, "keeper_files");
  }

  @Transactional
  public void saveFiles(List<MultipartFile> multipartFiles, PostingDto dto,
      PostingEntity postingEntity) {
    if (multipartFiles == null) {
      return;
    }

    for (MultipartFile multipartFile : multipartFiles) {
      try {
        File file = saveFile(multipartFile, "keeper_files");
        saveFileEntity(dto, postingEntity, file, "keeper_files");
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
