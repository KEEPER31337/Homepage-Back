package keeper.project.homepage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import keeper.project.homepage.entity.OriginalImageEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class ThumbnailServiceTest {

  @Autowired
  private OriginalImageService originalImageService;

  @Autowired
  private ThumbnailService thumbnailService;

  private UUID uuid = UUID.randomUUID();
  // test 시 "{작업경로}/files/"에 jpg 이미지 파일을 넣어야 함.
  private String originalFilePath =
      System.getProperty("user.dir") + "\\" + "files/long_w.jpg";
  private String resultDirPath = System.getProperty("user.dir") + "\\" + "keeper_files/thumbnail";

  private MultipartFile multipartFile;

  @BeforeEach
  public void setup() throws IOException {
    multipartFile = new MockMultipartFile("test", "image_1.jpg", "image/jpg",
        new FileInputStream(new File(originalFilePath)));
  }

  @Test
  public void createTest() {
    Assertions.assertTrue(new File(originalFilePath).exists(), "test할 이미지 파일이 없습니다.");
    OriginalImageEntity originalImageEntity = originalImageService.save(multipartFile, uuid);
    thumbnailService.save(multipartFile, originalImageEntity, uuid, 100, 100);

    String originalFileName = uuid.toString() + "_" + multipartFile.getOriginalFilename();
    String thumbFileName = "thumb_" + uuid.toString() + "_" + multipartFile.getOriginalFilename();
    Assertions.assertTrue(new File(resultDirPath + "\\" + originalFileName).exists(),
        "original file이 저장되지 않았습니다.");
    Assertions.assertTrue(new File(resultDirPath + "\\" + thumbFileName).exists(),
        "thumbnail file이 저장되지 않았습니다.");

  }
}
