package keeper.project.homepage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.util.FileConversion;
import keeper.project.homepage.util.ImageCenterCrop;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
import keeper.project.homepage.util.service.ThumbnailService.ThumbnailSize;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private ThumbnailService thumbnailService;

  @Autowired
  private FileService fileService;

  private final DefaultThumbnailInfo defaultThumbnail = DefaultThumbnailInfo.ThumbPosting;
  private String defaultFilePath;
  private String imageFilePath;
  private String ipAddress = "127.0.0.1";

  private MultipartFile originalImage;

  private FileEntity fileEntity;
  private ThumbnailEntity thumbnailEntity;
  private ThumbnailEntity defaultThumbnailEntity;


  private static void createDefaultFile(String filePath) {
    FileConversion fileConversion = new FileConversion();
    fileConversion.makeSampleJPEGImage(filePath);
  }

  @BeforeEach
  public void setup() throws IOException {
    // fileService를 static으로 돌릴 수 없어서 BeforeEach에 넣어둠
    List<String> defaultFilePathList = Stream.of(DefaultThumbnailInfo.values())
        .map(d -> System.getProperty("user.dir") + File.separator
            + fileService.findFileEntityById(d.getFileId()).getFilePath())
        .collect(Collectors.toList());
    for (String filePath : defaultFilePathList) {
      File defaultFile = new File(filePath);
      if (defaultFile.exists()) {
        continue;
      }
      createDefaultFile(filePath);
    }

    fileEntity = generateFileEntity();
    imageFilePath = System.getProperty("user.dir") + File.separator + fileEntity.getFilePath();
    thumbnailEntity = generateThumbnailEntity();
    defaultThumbnailEntity = thumbnailService.findById(defaultThumbnail.getThumbnailId());
    defaultFilePath = System.getProperty("user.dir") + File.separator
        + fileService.findFileEntityById(defaultThumbnail.getFileId()).getFilePath();

    originalImage = new MockMultipartFile("test", fileEntity.getFileName(),
        "image/jpg", new FileInputStream(new File(imageFilePath)));
  }

  @Test
  public void createTest() {
    Assertions.assertTrue(new File(imageFilePath).exists(), "test할 이미지 파일이 없습니다.");
    ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
        originalImage, ThumbnailSize.LARGE, ipAddress);
    Assertions.assertTrue(
        new File(
            System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath()).exists(),
        "thumbnail file이 저장되지 않았습니다.");
    Assertions.assertNotNull(thumbnailService.findById(thumbnailEntity.getId()),
        "thumbnail Entity가 저장되지 않았습니다.");
  }

  @Test
  public void createDefaultTest() {
    Assertions.assertTrue(new File(defaultFilePath).exists(), "test할 이미지 파일이 없습니다.");
    ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
        null, ThumbnailSize.LARGE, ipAddress);

    log.info(System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath());
    Assertions.assertTrue(
        new File(
            System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath()).exists(),
        "thumbnail file이 저장되지 않았습니다.");
    Assertions.assertNotNull(thumbnailService.findById(thumbnailEntity.getId()),
        "thumbnail Entity가 저장되지 않았습니다.");
  }

  @Test
  @DisplayName("썸네일 삭제")
  public void deleteTest() {
    String thumbnailPath =
        System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath();
    String filePath =
        System.getProperty("user.dir") + File.separator + thumbnailEntity.getFile().getFilePath();
    thumbnailService.deleteById(thumbnailEntity.getId());
    fileService.deleteOriginalThumbnail(thumbnailEntity);

    Assertions.assertTrue(thumbnailRepository.findById(thumbnailEntity.getId()).isEmpty());
    Assertions.assertTrue(fileRepository.findById(thumbnailEntity.getFile().getId()).isEmpty());
    Assertions.assertFalse(new File(thumbnailPath).exists());
    Assertions.assertFalse(new File(filePath).exists());
  }

  // TODO : 이거 현재 기본 이미지 삭제하는 지 테스트 수정해야 함.
  @Test
  @DisplayName("기본 이미지로 생성한 썸네일 삭제")
  public void deleteDefaultTest() {
    String thumbnailPath = System.getProperty("user.dir") + File.separator
        + thumbnailService.findById(defaultThumbnail.getThumbnailId()).getPath();
    thumbnailService.deleteById(1L);
    fileService.deleteOriginalThumbnail(defaultThumbnailEntity);

    Assertions.assertTrue(thumbnailRepository.findById(defaultThumbnailEntity.getId()).isPresent());
    Assertions.assertTrue(
        fileRepository.findById(defaultThumbnailEntity.getFile().getId()).isPresent());
    Assertions.assertTrue(new File(thumbnailPath).exists());
  }
}
