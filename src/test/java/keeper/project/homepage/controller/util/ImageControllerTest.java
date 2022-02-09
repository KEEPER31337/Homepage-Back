package keeper.project.homepage.controller.util;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.File;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.common.FileConversion;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ImageControllerTest extends ApiControllerTestSetUp {

  final private String ipAddress = "127.0.0.1";
  private ThumbnailEntity thumbnailEntity;
  private FileEntity fileEntity;

  @BeforeAll
  public static void createFile() {
    final String keeperFilesDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files";
    final String thumbnailDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "thumbnail";
    final String imageFilePath = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "test_file.jpg";

    File keeperFilesDir = new File(keeperFilesDirectoryPath);
    File thumbnailDir = new File(thumbnailDirectoryPath);

    if (!keeperFilesDir.exists()) {
      keeperFilesDir.mkdir();
    }

    if (!thumbnailDir.exists()) {
      thumbnailDir.mkdir();
    }

    createImageForTest(imageFilePath);
  }

  private static void createImageForTest(String filePath) {
    FileConversion fileConversion = new FileConversion();
    fileConversion.makeSampleJPEGImage(filePath);
  }

  @BeforeEach
  public void setUp() throws Exception {

    fileEntity = FileEntity.builder()
        .fileName("test_file.jpg")
        .filePath("keeper_files" + File.separator + "test_file.jpg")
        .fileSize(0L)
        .ipAddress(ipAddress)
        .build();
    fileRepository.save(fileEntity);

    thumbnailEntity = ThumbnailEntity.builder()
        .path("keeper_files/thumbnail/thumb_default.jpg")
        .file(fileEntity)
        .build();
    thumbnailRepository.save(thumbnailEntity);

  }

  @Test
  @DisplayName("이미지 로딩 성공 테스트")
  public void getImageTest() throws Exception {
    mockMvc.perform(get("/v1/util/image/{fileId}",
            fileEntity.getId().toString()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("get-image",
            pathParameters(
                parameterWithName("fileId").description("파일 ID")
            )
        ));
  }

  @Test
  @DisplayName("썸네일 로딩 성공 테스트")
  public void getThumbnailTest() throws Exception {
    mockMvc.perform(get("/v1/util/thumbnail/{thumbnailId}",
            thumbnailEntity.getId().toString()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("get-thumbnail",
            pathParameters(
                parameterWithName("thumbnailId").description("썸네일 ID")
            )
        ));
  }
}