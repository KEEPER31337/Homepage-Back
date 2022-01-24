package keeper.project.homepage.service;

import java.io.File;
import java.util.UUID;
import keeper.project.homepage.entity.OriginalImageEntity;
import keeper.project.homepage.repository.OriginalImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OriginalImageService {

  private final OriginalImageRepository originalImageRepository;

  public OriginalImageEntity save(MultipartFile multipartFile, UUID uuid) {
    if (multipartFile == null) {
      // 나중에 default 이미지로 설정
      return null;
    }

    String fileName = uuid.toString() + "_" + multipartFile.getOriginalFilename();
    String relFilePath = "keeper_files\\thumbnail";
    String absFilePath = System.getProperty("user.dir") + "\\" + relFilePath;
    if (!new File(absFilePath).exists()) {
      new File(absFilePath).mkdir();
    }
    try {
      absFilePath = absFilePath + "\\" + fileName;
      File thumbnailFile = new File(absFilePath);
      multipartFile.transferTo(thumbnailFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return originalImageRepository.save(
        OriginalImageEntity.builder().path(relFilePath).build());

  }

  public boolean deleteById(Integer deleteId) {
    if (originalImageRepository.findById(deleteId).isPresent()) {
      return false;
    }
    originalImageRepository.deleteById(deleteId);
    return true;
  }
}
