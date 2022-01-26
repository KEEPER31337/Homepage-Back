package keeper.project.homepage.service;

import java.io.File;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.service.image.ImageProcessing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

  private final ThumbnailRepository thumbnailRepository;
  private final FileService fileService;
  private final String relDirPath = "keeper_files\\thumbnail";
  private final String defaultImageName = "thumb_default.jpg";

  public ThumbnailEntity saveThumbnail(ImageProcessing imageProcessing, MultipartFile multipartFile,
      FileEntity fileEntity, Integer width, Integer height) throws Exception {
    String fileName = "";
    if (multipartFile == null) {
      fileName = this.defaultImageName;
    } else {
      if (fileService.isImageFile(multipartFile) == false) {
        throw new Exception("썸네일 용 파일은 이미지 파일이어야 합니다.");
      }
      try {
        File thumbnailImage = fileService.saveFileInServer(multipartFile, this.relDirPath);
        imageProcessing.imageProcessing(thumbnailImage, width, height, "jpg");
        fileName = thumbnailImage.getName();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return thumbnailRepository.save(
        ThumbnailEntity.builder().path(this.relDirPath + "\\" + fileName).file(fileEntity).build());
  }

  public ThumbnailEntity findById(Long findId) {
    return thumbnailRepository.findById(findId).orElse(null);
  }

  public boolean deleteById(Long deleteId) {
    // original thumbnail file을 가지고 있으면
    // file은 삭제하면 안됨. entity만 삭제하기.
    if (thumbnailRepository.findById(deleteId).isPresent()) {
      return false;
    }
    thumbnailRepository.deleteById(deleteId);
    return true;
  }
}
