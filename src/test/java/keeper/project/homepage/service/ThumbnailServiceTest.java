package keeper.project.homepage.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.common.ImageCenterCrop;
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
public class ThumbnailServiceTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";
  final private String ipAddress1 = "127.0.0.1";
  final private String ipAddress2 = "127.0.0.2";

  @Autowired
  private ThumbnailService thumbnailService;

  @Autowired
  private FileService fileService;

  // test 시 "{작업경로}/keeper_files/"에 jpg 이미지 파일을 넣어야 함.
  private final String originalFilePath =
      System.getProperty("user.dir") + File.separator + "keeper_files" + File.separator
          + "test_file.jpg";
  private final String defaultOriginalFilePath =
      System.getProperty("user.dir") + File.separator + "keeper_files" + File.separator
          + "default.jpg";
  private final String defaultThumbnailFilePath =
      System.getProperty("user.dir") + File.separator + "keeper_files" + File.separator
          + "thumbnail" + File.separator + "thumb_detail.jpg";

  private String ipAddress = "127.0.0.1";

  private MultipartFile originalImage;
  private MultipartFile defaultOriginalImage;

  private MemberEntity memberEntity;
  private CategoryEntity categoryEntity;
  private PostingEntity postingEntity;
  private FileEntity fileEntity;
  private ThumbnailEntity thumbnailEntity;
  private FileEntity defaultFileEntity;
  private ThumbnailEntity defaultThumbnailEntity;


  @BeforeAll
  public static void createFile() throws IOException {
    final String keeperFilesDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files";
    final String thumbnailDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "thumbnail";
    final String testImageForTest = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "test_file.jpg";
    final String testThumbnailForTest = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "thumbnail" + File.separator + "thumb_test_file.jpg";
    final String defaultImageForTest = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "default.jpg";
    final String defaultThumbnailForTest = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "thumbnail" + File.separator + "thumb_default.jpg";

    File keeperFilesDir = new File(keeperFilesDirectoryPath);
    File thumbnailDir = new File(thumbnailDirectoryPath);

    if (!keeperFilesDir.exists()) {
      keeperFilesDir.mkdir();
    }

    if (!thumbnailDir.exists()) {
      thumbnailDir.mkdir();
    }

    createFileForTest(testImageForTest);
    createFileForTest(testThumbnailForTest);
    createFileForTest(defaultImageForTest);
    createFileForTest(defaultThumbnailForTest);
  }

  private static void createFileForTest(String filePath) throws IOException {
    String str = "keeper is best dong-a-ri";
    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
    writer.write(str);
    writer.close();
  }

  @BeforeEach
  public void setup() throws IOException {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberJobEntity)
        .build();
    memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);

    categoryEntity = CategoryEntity.builder()
        .name("테스트 게시판").build();
    categoryRepository.save(categoryEntity);

    fileEntity = FileEntity.builder()
        .fileName("image_1.jpg")
        .filePath("keeper_files" + File.separator + "test_file.jpg")
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(fileEntity);

    thumbnailEntity = ThumbnailEntity.builder()
        .path(
            "keeper_files" + File.separator + "thumbnail" + File.separator + "thumb_test_file.jpg")
        .file(fileEntity).build();
    thumbnailRepository.save(thumbnailEntity);

    defaultFileEntity = FileEntity.builder()
        .fileName("image_1.jpg")
        .filePath("keeper_files" + File.separator + "default.jpg")
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(defaultFileEntity);

    defaultThumbnailEntity = ThumbnailEntity.builder()
        .path("keeper_files" + File.separator + "thumbnail" + File.separator + "thumb_default.jpg")
        .file(defaultFileEntity).build();
    thumbnailRepository.save(defaultThumbnailEntity);

    postingEntity = PostingEntity.builder()
        .title("test 게시판 제목")
        .content("test 게시판 제목 내용")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnail(thumbnailEntity)
        .ipAddress("192.11.222.333")
        .allowComment(0)
        .isNotice(0)
        .isSecret(1)
        .isTemp(0)
        .likeCount(0)
        .dislikeCount(0)
        .commentCount(0)
        .visitCount(0)
        .registerTime(LocalDateTime.now())
        .updateTime(LocalDateTime.now())
        .password("asd")
        .build();

    postingRepository.save(postingEntity);
    postingRepository.save(PostingEntity.builder()
        .title("test 게시판 제목2")
        .content("test 게시판 제목 내용2")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnail(defaultThumbnailEntity)
        .ipAddress("192.11.223")
        .allowComment(0)
        .isNotice(0)
        .isSecret(1)
        .isTemp(0)
        .likeCount(0)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(LocalDateTime.now())
        .updateTime(LocalDateTime.now())
        .password("asd2")
        .build());

    fileEntity = fileRepository.save(FileEntity.builder()
        .postingId(postingEntity)
        .fileName("test file")
        .filePath("test/file.txt")
        .fileSize(12345L)
        .uploadTime(LocalDateTime.now())
        .ipAddress(postingEntity.getIpAddress())
        .build());

    originalImage = new MockMultipartFile("test", "image_1.jpg", "image/jpg",
        new FileInputStream(new File(originalFilePath)));
    defaultOriginalImage = new MockMultipartFile("default", "default.jpg", "image/jpg",
        new FileInputStream(new File(defaultOriginalFilePath)));
  }

  @Test
  public void createTest() {
    Assertions.assertTrue(new File(originalFilePath).exists(), "test할 이미지 파일이 없습니다.");
    try {
//    FileEntity fileEntity = fileService.saveOriginalImage(originalImage, ipAddress);
      ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
          originalImage, fileEntity, "large");

//    Assertions.assertTrue(
//        new File(System.getProperty("user.dir") + File.separator + fileEntity.getFilePath()).exists(),
//        "original file이 저장되지 않았습니다.");
      Assertions.assertTrue(
          new File(
              System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath()).exists(),
          "thumbnail file이 저장되지 않았습니다.");
      Assertions.assertNotNull(thumbnailService.findById(thumbnailEntity.getId()),
          "thumbnail Entity가 저장되지 않았습니다.");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void createDefaultTest() {
    try {
      Assertions.assertTrue(new File(defaultOriginalFilePath).exists(), "test할 이미지 파일이 없습니다.");
//    FileEntity fileEntity = fileService.saveOriginalImage(defaultOriginalImage, ipAddress);
      ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(), null,
          fileEntity, "large");

//    Assertions.assertTrue(
//        new File(System.getProperty("user.dir") + File.separator + fileEntity.getFilePath()).exists(),
//        "original file이 저장되지 않았습니다.");
      log.info(System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath());
      Assertions.assertTrue(
          new File(
              System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath()).exists(),
          "thumbnail file이 저장되지 않았습니다.");
      Assertions.assertNotNull(thumbnailService.findById(thumbnailEntity.getId()),
          "thumbnail Entity가 저장되지 않았습니다.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("썸네일 삭제")
  public void deleteTest() {
    String thumbnailPath =
        System.getProperty("user.dir") + File.separator + thumbnailEntity.getPath();
    String originalThumbnailPath =
        System.getProperty("user.dir") + File.separator + thumbnailEntity.getFile().getFilePath();
    thumbnailService.deleteById(thumbnailEntity.getId());
    fileService.deleteOriginalThumbnail(thumbnailEntity);

    Assertions.assertTrue(thumbnailRepository.findById(thumbnailEntity.getId()).isEmpty());
    Assertions.assertTrue(fileRepository.findById(thumbnailEntity.getFile().getId()).isEmpty());
    Assertions.assertFalse(new File(thumbnailPath).exists());
    Assertions.assertFalse(new File(originalThumbnailPath).exists());
  }

  @Test
  @DisplayName("기본 이미지로 생성한 썸네일 삭제")
  public void deleteDefaultTest() {
    String thumbnailPath =
        System.getProperty("user.dir") + File.separator + defaultThumbnailEntity.getPath();
    String originalThumbnailPath =
        System.getProperty("user.dir") + File.separator + defaultThumbnailEntity.getFile()
            .getFilePath();
    thumbnailService.deleteById(defaultThumbnailEntity.getId());
    fileService.deleteOriginalThumbnail(defaultThumbnailEntity);

    Assertions.assertTrue(thumbnailRepository.findById(defaultThumbnailEntity.getId()).isEmpty());
    Assertions.assertTrue(
        fileRepository.findById(defaultThumbnailEntity.getFile().getId()).isEmpty());
    Assertions.assertTrue(new File(thumbnailPath).exists());
    Assertions.assertTrue(new File(originalThumbnailPath).exists());
  }
}
