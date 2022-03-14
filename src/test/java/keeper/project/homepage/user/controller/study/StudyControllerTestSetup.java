package keeper.project.homepage.user.controller.study;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.study.StudyEntity;
import keeper.project.homepage.entity.study.StudyHasMemberEntity;
import keeper.project.homepage.util.FileConversion;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.restdocs.payload.FieldDescriptor;

public class StudyControllerTestSetup extends ApiControllerTestHelper {

  private static final int HEAD_MEMBER_COUNT = 1;
  protected static final String userDirectory = System.getProperty("user.dir");
  protected static final String createTestImage =
      "keeper_files" + File.separator + "createTest.jpg";
  protected static final int VALID_YEAR = 2022;
  protected static final int VALID_SEASON = 3;
  protected static final int MODIFY_VALID_YEAR = 2021;
  protected static final int MODIFY_VALID_SEASON = 2;
  protected static final int INVALID_SEASON = 5;
  protected static final int INVALID_MEMBER_ID = -9999;
  protected static final String NEW_TITLE = "새로운 스터디 제목";
  protected static final String NEW_INFORMATION = "새로운 스터디 내용";
  protected static final String MODIFY_TITLE = "수정된 스터디 제목";
  protected static final String MODIFY_INFORMATION = "수정된 스터디 내용";

  @BeforeAll
  protected static void createFile() {
    final String keeperFilesDirectoryPath = userDirectory + File.separator + "keeper_files";
    final String thumbnailDirectoryPath = keeperFilesDirectoryPath + File.separator + "thumbnail";
    final String createTestImage = keeperFilesDirectoryPath + File.separator + "createTest.jpg";

    File keeperFilesDir = new File(keeperFilesDirectoryPath);
    File thumbnailDir = new File(thumbnailDirectoryPath);

    if (!keeperFilesDir.exists()) {
      keeperFilesDir.mkdir();
    }

    if (!thumbnailDir.exists()) {
      thumbnailDir.mkdir();
    }

    createImageForTest(createTestImage);
  }

  protected static void createImageForTest(String filePath) {
    FileConversion fileConversion = new FileConversion();
    fileConversion.makeSampleJPEGImage(filePath);
  }

  protected String getFileName(String filePath) {
    File file = new File(filePath);
    return file.getName();
  }

  protected StudyEntity generateStudyEntity(MemberEntity headMember) {

    final String epochTime = Long.toHexString(System.nanoTime());
    ThumbnailEntity thumbnailEntity = generateThumbnailEntity();

    StudyEntity studyEntity = studyRepository.save(StudyEntity.builder()
        .title("제목" + epochTime)
        .information("세부내용")
        .headMember(headMember)
        .memberNumber(HEAD_MEMBER_COUNT)
        .registerTime(LocalDateTime.now())
        .year(VALID_YEAR)
        .season(VALID_SEASON)
        .gitLink("https://github.com/gusah009") // 나를 잊지 말아요~
        .noteLink(
            "https://enormous-button-c5d.notion.site/KEEPER-NEW-HOMEPAGE-PROJECT-c4fd631881d84e4daa6fa14404ac6173")
        .etcLink("")
        .thumbnail(thumbnailEntity)
        .build());

    addMember(studyEntity, headMember);
    return studyEntity;
  }

  protected void addMember(StudyEntity studyEntity, MemberEntity memberEntity) {

    StudyHasMemberEntity studyHasMemberEntity = studyHasMemberRepository.save(
        StudyHasMemberEntity.builder()
            .member(memberEntity)
            .study(studyEntity)
            .build());
    memberEntity.getStudyHasMemberEntities().add(studyHasMemberEntity);
    studyEntity.getStudyHasMemberEntities().add(studyHasMemberEntity);
  }

  protected List<FieldDescriptor> generateStudyDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("스터디 아이디"),
        fieldWithPath(prefix + ".title").description("스터디 제목"),
        fieldWithPath(prefix + ".information").description("스터디 소개"),
        subsectionWithPath(prefix + ".headMember").description("스터디장"),
        fieldWithPath(prefix + ".memberNumber").description("스터디원 수"),
        fieldWithPath(prefix + ".registerTime").description("스터디 생성 시간"),
        fieldWithPath(prefix + ".year").description("스터디 년도"),
        fieldWithPath(prefix + ".season").description("스터디 시즌\n"
            + "1: 1학기" + "\n"
            + "2: 여름학기" + "\n"
            + "3: 2학기" + "\n"
            + "4: 겨울학기"),
        fieldWithPath(prefix + ".gitLink").description("github 주소"),
        fieldWithPath(prefix + ".noteLink").description("notion 주소"),
        fieldWithPath(prefix + ".etcLink").description("그 외 스터디 링크"),
        fieldWithPath(prefix + ".thumbnailPath").description("스터디 썸네일 주소"),
        subsectionWithPath(prefix + ".memberList[]").description("스터디원 목록 (스터디장 포함)"))
    );
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateStudyYearSeasonDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".year").description("스터디 년도"),
        fieldWithPath(prefix + ".season[]").description("스터디 시즌\n"
            + "1: 1학기" + "\n"
            + "2: 여름학기" + "\n"
            + "3: 2학기" + "\n"
            + "4: 겨울학기")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }
}
