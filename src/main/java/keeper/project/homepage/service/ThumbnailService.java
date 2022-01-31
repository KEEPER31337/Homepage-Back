package keeper.project.homepage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.exception.CustomFileNotFoundException;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.service.image.ImageProcessing;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

  private final ThumbnailRepository thumbnailRepository;
  private final FileService fileService;
  private final String relDirPath = "keeper_files" + File.separator + "thumbnail";
  private final String defaultImageName = "thumb_default.jpg"; // thumbnail delete시 파일명 비교

  public byte[] getThumbnail(Long thumbnailId) throws IOException {
    ThumbnailEntity thumbnail = thumbnailRepository.findById(thumbnailId).orElseThrow(
        () -> new CustomFileNotFoundException("썸네일 파일을 찾을 수 없습니다")
    );
    String thumbnailPath = System.getProperty("user.dir") + File.separator + thumbnail.getPath();
    File file = new File(thumbnailPath);
    InputStream in = new FileInputStream(file);
    return IOUtils.toByteArray(in);
  }

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
        ThumbnailEntity.builder().path(this.relDirPath + File.separator + fileName).file(fileEntity)
            .build());
  }

  public ThumbnailEntity findById(Long findId) {
    return thumbnailRepository.findById(findId).orElse(null);
  }

  public boolean deleteById(Long deleteId) throws RuntimeException {
    // issue : 각 예외사항에서 return 대신 custom exception으로 수정
    // original thumbnail file을 가지고 있으면 서버에 있는 이미지는 삭제 X
    ThumbnailEntity deleted = thumbnailRepository.findById(deleteId).orElse(null);
    if (deleted == null) {
      return false;
    }
    File thumbnailFile = new File(System.getProperty("user.dir") + "/" + deleted.getPath());
    String thumbnailFileName = thumbnailFile.getName();
    if (thumbnailFileName.equals(defaultImageName) == false) {
      if (thumbnailFile.exists() == false) {
        throw new RuntimeException("썸네일 파일이 이미 존재하지 않습니다.");
      }
      else if (thumbnailFile.delete() == false) {
        throw new RuntimeException("썸네일 파일 삭제를 실패하였습니다.");
      }
    }
    thumbnailRepository.deleteById(deleteId);
    return true;
  }
}
