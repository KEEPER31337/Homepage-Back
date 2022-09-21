package keeper.project.homepage.util.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.util.image.preprocessing.ImageCenterCropping;
import keeper.project.homepage.util.image.preprocessing.ImageSize;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@Log4j2
public class ThumbnailServiceTest extends ApiControllerTestHelper {

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  public void createTest() throws IOException {
    //given
    String ipAddress = "127.0.0.1";
    FileEntity fileEntity = generateFileEntity();
    String imageFilePath = usrDir + fileEntity.getFilePath();
    MultipartFile originalImage = new MockMultipartFile("test", fileEntity.getFileName(),
        "image/jpg", new FileInputStream(new File(imageFilePath)));

    //test
    Assertions.assertTrue(new File(imageFilePath).exists(), "test할 이미지 파일이 없습니다.");
    ThumbnailEntity thumbnailEntity = thumbnailService.save(ThumbType.PostThumbnail,
        new ImageCenterCropping(ImageSize.LARGE), originalImage, ipAddress);
    Assertions.assertTrue(
        new File(
            System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath()).exists(),
        "thumbnail file이 저장되지 않았습니다.");
    Assertions.assertNotNull(thumbnailService.find(thumbnailEntity.getId()),
        "thumbnail Entity가 저장되지 않았습니다.");

    // clear
    deleteTestThumbnailFile(thumbnailEntity);
  }

  @Test
  public void createDefaultTest() {
    //given
    String ipAddress = "127.0.0.1";
    Long defaultFileId = ThumbType.PostThumbnail.getDefaultFileId();
    FileEntity defaultFileEntity = fileService.find(defaultFileId);
    String defaultFilePath = usrDir + defaultFileEntity.getFilePath();
    Boolean isFileCreated = false;
    if (!(new File(defaultFilePath).exists())) {
      createFileForTest(defaultFilePath);
      isFileCreated = true;
    }

    //test
    Assertions.assertTrue(new File(defaultFilePath).exists(), "test할 이미지 파일이 없습니다.");
    ThumbnailEntity thumbnailEntity = thumbnailService.save(ThumbType.PostThumbnail,
        new ImageCenterCropping(ImageSize.LARGE), null, ipAddress);

    log.info(System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath());
    Assertions.assertTrue(
        new File(
            System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath()).exists(),
        "thumbnail file이 저장되지 않았습니다.");
    Assertions.assertNotNull(thumbnailService.find(thumbnailEntity.getId()),
        "thumbnail Entity가 저장되지 않았습니다.");

    // clear
    if (isFileCreated) {
      deleteTestFile(defaultFileEntity);
    }
  }

  @Test
  @DisplayName("썸네일 삭제")
  public void deleteTest() {
    //given
    ThumbnailEntity thumbnailEntity = generateThumbnailEntity();
    String thumbnailPath =
        System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath();
    String filePath =
        System.getProperty("user.dir") + File.separator + thumbnailEntity.getFile().getFilePath();

    // test
    thumbnailService.delete(thumbnailEntity.getId());

    Assertions.assertTrue(thumbnailRepository.findById(thumbnailEntity.getId()).isEmpty());
    Assertions.assertTrue(fileRepository.findById(thumbnailEntity.getFile().getId()).isEmpty());
    Assertions.assertFalse(new File(thumbnailPath).exists());
    Assertions.assertFalse(new File(filePath).exists());
  }

  // TODO : 이거 현재 기본 이미지 삭제하는 지 테스트 수정해야 함.
  @Test
  @DisplayName("기본 이미지로 생성한 썸네일 삭제")
  public void deleteDefaultTest() {
    //given
    Long defaultThumbnailId = ThumbType.PostThumbnail.getDefaultThumbnailId();
    ThumbnailEntity defaultThumbnailEntity = thumbnailService.find(defaultThumbnailId);
    String defaultThumbnailPath = usrDir + defaultThumbnailEntity.getPath();
    Boolean isFileCreated = false;
    if (!(new File(defaultThumbnailPath).exists())) {
      createFileForTest(defaultThumbnailPath);
      isFileCreated = true;
    }

    // test
    thumbnailService.delete(defaultThumbnailId);

    Assertions.assertTrue(thumbnailRepository.findById(defaultThumbnailEntity.getId()).isPresent());
    Assertions.assertTrue(
        fileRepository.findById(defaultThumbnailEntity.getFile().getId()).isPresent());
    Assertions.assertTrue(new File(defaultThumbnailPath).exists());

    // clear
    if (isFileCreated) {
      deleteTestThumbnailFile(defaultThumbnailEntity);
    }
  }
}
