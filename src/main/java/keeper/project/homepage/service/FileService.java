package keeper.project.homepage.service;

import java.io.File;
import java.util.List;
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
    for (MultipartFile file : files) {
      fileName = file.getOriginalFilename();
      filePath = System.getProperty("user.dir") + "\\files"; //working directory + \\files
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
      fileDto.setFilePath(filePath);
      fileDto.setFileSize(file.getSize());
      fileDto.setUploadTime(dto.getRegisterTime());
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
        new File(fileEntity.getFilePath()).delete();
        fileRepository.delete(fileEntity);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
