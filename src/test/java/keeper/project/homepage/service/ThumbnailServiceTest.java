package keeper.project.homepage.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import keeper.project.homepage.service.image.ImageCenterCrop;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

  // test 시 "{작업경로}/keeper_files/"에 jpg 이미지 파일을 넣어야 함.
  private final String originalFilePath =
      System.getProperty("user.dir") + File.separator + "keeper_files" + File.separator
          + "test_file.jpg";
  private final String defaultOriginalFilePath =
      System.getProperty("user.dir") + File.separator + "keeper_files" + File.separator
          + "default.jpg";

  private String ipAddress = "127.0.0.1";

  private MultipartFile originalImage;
  private MultipartFile defaultOriginalImage;

  private MemberEntity memberEntity;
  private CategoryEntity categoryEntity;
  private PostingEntity postingEntity;
  private ThumbnailEntity thumbnailEntity1;
  private FileEntity fileEntity1;
  private ThumbnailEntity thumbnailEntity2;
  private FileEntity fileEntity2;


  @BeforeAll
  public static void createFile() throws IOException {
    final String keeperFilesDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files";
    final String thumbnailDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "thumbnail";
    final String testImageForTest = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "test_file.jpg";
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

    fileEntity1 = FileEntity.builder()
        .fileName("image_1.jpg")
        .filePath("keeper_files" + File.separator + "image_1.jpg")
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(fileEntity1);

    thumbnailEntity1 = ThumbnailEntity.builder()
        .path("keeper_files" + File.separator + "t_image_1.jpg")
        .file(fileEntity1).build();
    thumbnailRepository.save(thumbnailEntity1);

    fileEntity2 = FileEntity.builder()
        .fileName("image_2.jpg")
        .filePath("keeper_files" + File.separator + "image_2.jpg")
        .fileSize(0L)
        .ipAddress(ipAddress2)
        .build();
    fileRepository.save(fileEntity2);

    thumbnailEntity2 = ThumbnailEntity.builder()
        .path("keeper_files" + File.separator + "t_image_2.jpg")
        .file(fileEntity2).build();
    thumbnailRepository.save(thumbnailEntity2);

    postingEntity = PostingEntity.builder()
        .title("test 게시판 제목")
        .content("test 게시판 제목 내용")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnailId(thumbnailEntity1)
        .ipAddress("192.11.222.333")
        .allowComment(0)
        .isNotice(0)
        .isSecret(1)
        .isTemp(0)
        .likeCount(0)
        .dislikeCount(0)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asd")
        .build();

    postingRepository.save(postingEntity);
    postingRepository.save(PostingEntity.builder()
        .title("test 게시판 제목2")
        .content("test 게시판 제목 내용2")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnailId(thumbnailEntity2)
        .ipAddress("192.11.223")
        .allowComment(0)
        .isNotice(0)
        .isSecret(1)
        .isTemp(0)
        .likeCount(0)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asd2")
        .build());

    fileEntity1 = fileRepository.save(FileEntity.builder()
        .postingId(postingEntity)
        .fileName("test file")
        .filePath("test/file.txt")
        .fileSize(12345L)
        .uploadTime(new Date())
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
          originalImage, fileEntity1, 100, 100);

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
          fileEntity1, 100, 100);

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
}
