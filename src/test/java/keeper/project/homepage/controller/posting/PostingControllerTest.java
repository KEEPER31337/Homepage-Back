package keeper.project.homepage.controller.posting;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.exception.posting.CustomCategoryNotFoundException;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.util.FileConversion;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.service.posting.PostingService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
@Log4j2
public class PostingControllerTest extends ApiControllerTestHelper {

  final private String adminLoginId = "hyeonmoAdmin";
  final private String adminPassword = "keeper2";
  final private String adminRealName = "JeongHyeonMo2";
  final private String adminNickName = "JeongHyeonMo2";
  final private String adminEmailAddress = "test2@k33p3r.com";
  final private String adminStudentId = "201724580";

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";
  final private String ipAddress1 = "127.0.0.1";
  final private String ipAddress2 = "127.0.0.2";

  private String userToken;
  private String adminToken;
  private String freshManToken;
  private String freshManHasAccessToken;

  private MemberEntity memberEntity;
  private MemberEntity adminEntity;
  private MemberEntity freshMan;
  private MemberEntity freshManHasAccess;
  private CategoryEntity categoryEntity;
  private CategoryEntity notAccessCategoryTestEntity;
  private PostingEntity postingGeneralTest;
  private PostingEntity postingDeleteTest;
  private PostingEntity postingDeleteTest2;
  private PostingEntity postingModifyTest;
  private PostingEntity postingNoticeTest;
  private PostingEntity postingNoticeTest2;
  private PostingEntity notAccessPostingTestEntity;
  private PostingEntity accessNoticePostingTestEntity;

  private ThumbnailEntity generalThumbnail;
  private FileEntity generalImageFile;
  private ThumbnailEntity deleteThumbnail;
  private ThumbnailEntity deleteThumbnail2;
  private FileEntity deleteImageFile;
  private FileEntity deleteImageFile2;
  private ThumbnailEntity modifyThumbnail;
  private FileEntity modifyImageFile;

  private final String userDirectory = System.getProperty("user.dir");
  private final String createTestImage = testFileRelDir + File.separator + "createTest.jpg";
  private final String modifyAftTestImage = testFileRelDir + File.separator + "modifyAftTest.jpg";

  private String getFileName(String filePath) {
    File file = new File(filePath);
    return file.getName();
  }

  @BeforeEach
  public void setUp() throws Exception {
    createFileForTest(usrDir + createTestImage);
    memberEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    adminEntity = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(memberEntity);
    adminToken = generateJWTToken(adminEntity);

    categoryEntity = generateCategoryEntity();
    generalThumbnail = generateThumbnailEntity();
    deleteThumbnail = generateThumbnailEntity();
    deleteThumbnail2 = generateThumbnailEntity();
    modifyThumbnail = generateThumbnailEntity();
    generalImageFile = generateFileEntity();

    postingGeneralTest = generatePostingEntity(memberEntity, categoryEntity, 0, 1, 0);
    postingModifyTest = generatePostingEntity(memberEntity, categoryEntity, 0, 0, 0);
    postingDeleteTest = generatePostingEntity(memberEntity, categoryEntity, 0, 0, 0);
    postingDeleteTest2 = generatePostingEntity(memberEntity, categoryEntity, 0, 0, 0);

    PostingEntity postingTempTest = generatePostingEntity(memberEntity, categoryEntity, 0, 0, 1);
    postingNoticeTest = generatePostingEntity(memberEntity, categoryEntity, 1, 0, 0);
    postingNoticeTest2 = generatePostingEntity(memberEntity, categoryEntity, 1, 0, 0);
    FileEntity generalTestFile = FileEntity.builder()
        .postingId(postingGeneralTest)
        .fileName("test file")
        .filePath("test/file.txt")
        .fileSize(12345L)
        .uploadTime(LocalDateTime.now())
        .ipAddress(postingGeneralTest.getIpAddress())
        .build();
    fileRepository.save(generalTestFile);
    postingGeneralTest.getFiles().add(generalTestFile);

    fileRepository.save(FileEntity.builder()
        .postingId(postingModifyTest)
        .fileName("test file")
        .filePath("test/file.txt")
        .fileSize(12345L)
        .uploadTime(LocalDateTime.now())
        .ipAddress(postingModifyTest.getIpAddress())
        .build());

    freshMan = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
        MemberRankName.일반회원);
    freshMan.changeGeneration(13);
    freshManToken = generateJWTToken(freshMan);

    freshManHasAccess = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
        MemberRankName.일반회원);
    freshManHasAccess.changeGeneration(13);
    freshManHasAccess.updatePoint(PostingService.EXAM_BOARD_ACCESS_POINT);
    for (int i = 0; i < PostingService.EXAM_BOARD_ACCESS_COMMENT_COUNT; i++) {
      generateCommentEntity(postingGeneralTest, freshManHasAccess, 0L);
    }
    for (int i = 0; i < PostingService.EXAM_BOARD_ACCESS_ATTEND_COUNT; i++) {
      generateNewAttendanceWithTime(LocalDateTime.now().minusDays(i), freshManHasAccess);
    }
    freshManHasAccessToken = generateJWTToken(freshManHasAccess);

    notAccessCategoryTestEntity =
        categoryRepository.findById(PostingService.EXAM_CATEGORY_ID)
            .orElseThrow(CustomCategoryNotFoundException::new);
    notAccessPostingTestEntity =
        generatePostingEntity(memberEntity, notAccessCategoryTestEntity, 0, 0, 0);
    accessNoticePostingTestEntity =
        generatePostingEntity(memberEntity, notAccessCategoryTestEntity, 1, 0, 0);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("최신 글 목록 불러오기")
  public void findAllPosting() throws Exception {

    ResultActions result = mockMvc.perform(
        get("/v1/post/latest")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.list.length()", lessThanOrEqualTo(10)))
        .andDo(document("post-getLatest",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1"),
                fieldWithPath("list[].id").description("게시물 ID"),
                fieldWithPath("list[].title").description("게시물 제목"),
                fieldWithPath("list[].content").description("게시물 내용 (하나 조회 아닐경우 빈 문자열)"),
                fieldWithPath("list[].category").description("카테고리 이름"),
                fieldWithPath("list[].categoryId").description("카테고리 ID"),
                fieldWithPath("list[].writer").optional().description("작성자 (비밀 게시글일 경우 익명)"),
                fieldWithPath("list[].writerId").optional().description("작성자 (비밀 게시글일 경우 null)"),
                fieldWithPath("list[].writerThumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("작성자 썸네일 경로(비밀 게시글일 경우 / 썸네일을 등록하지 않았을 경우 null)"),
                fieldWithPath("list[].thumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("게시물 썸네일 경로 / 썸네일 등록하지 않았을 경우 null"),
                fieldWithPath("list[].visitCount").description("조회 수"),
                fieldWithPath("list[].likeCount").description("좋아요 수"),
                fieldWithPath("list[].dislikeCount").description("싫어요 수"),
                fieldWithPath("list[].commentCount").description("댓글 수"),
                fieldWithPath("list[].registerTime").description("작성 시간"),
                fieldWithPath("list[].updateTime").description("수정 시간"),
                fieldWithPath("list[].ipAddress").description("IP 주소"),
                fieldWithPath("list[].allowComment").description("댓글 허용?"),
                fieldWithPath("list[].isNotice").description("공지글?"),
                fieldWithPath("list[].isSecret").description("비밀글?"),
                fieldWithPath("list[].isTemp").description("임시저장?"),
                fieldWithPath("list[].size").description("총 게시물 수")
            )
        ));
  }

  @Test
  @DisplayName("카테고리별 글 목록 불러오기")
  public void findAllPostingByCategoryId() throws Exception {

    ResultActions result = mockMvc.perform(get("/v1/post/lists")
        .param("page", "0")
        .param("size", "5")
        .param("category", categoryEntity.getId().toString())
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.list.length()", lessThanOrEqualTo(10)))
        .andDo(document("post-getList",
            requestParameters(
                parameterWithName("category").description("게시판 종류 ID"),
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1"),
                fieldWithPath("list[].id").description("게시물 ID"),
                fieldWithPath("list[].title").description("게시물 제목"),
                fieldWithPath("list[].content").description("게시물 내용 (하나 조회 아닐경우 빈 문자열)"),
                fieldWithPath("list[].category").description("카테고리 이름"),
                fieldWithPath("list[].categoryId").description("카테고리 ID"),
                fieldWithPath("list[].writer").description("작성자  (비밀 게시글일 경우 익명)"),
                fieldWithPath("list[].writerId").optional().description("작성자 (비밀 게시글일 경우 null)"),
                fieldWithPath("list[].writerThumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("작성자 썸네일 경로(비밀 게시글일 경우 / 썸네일을 등록하지 않았을 경우 null)"),
                fieldWithPath("list[].thumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("게시물 썸네일 경로 / 썸네일 등록하지 않았을 경우 null"),
                fieldWithPath("list[].visitCount").description("조회 수"),
                fieldWithPath("list[].likeCount").description("좋아요 수"),
                fieldWithPath("list[].dislikeCount").description("싫어요 수"),
                fieldWithPath("list[].commentCount").description("댓글 수"),
                fieldWithPath("list[].registerTime").description("작성 시간"),
                fieldWithPath("list[].updateTime").description("수정 시간"),
                fieldWithPath("list[].ipAddress").description("IP 주소"),
                fieldWithPath("list[].allowComment").description("댓글 허용?"),
                fieldWithPath("list[].isNotice").description("공지글?"),
                fieldWithPath("list[].isSecret").description("비밀글?"),
                fieldWithPath("list[].isTemp").description("임시저장?"),
                fieldWithPath("list[].size").description("총 게시물 수")
            )
        ));
  }

  @Test
  @DisplayName("공지글 목록 불러오기(카테고리별 or 전부)")
  public void findAllNoticePostingByCategoryId() throws Exception {

    ResultActions result = mockMvc.perform(get("/v1/post/notice")
        .param("category", categoryEntity.getId().toString())
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getNotice",
            requestParameters(
                parameterWithName("category").description("게시판 종류 ID / 주지 않을시 전체 카테고리 공지글 불러옴")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1"),
                fieldWithPath("list[].id").description("게시물 ID"),
                fieldWithPath("list[].title").description("게시물 제목"),
                fieldWithPath("list[].content").description("게시물 내용 (하나 조회 아닐경우 빈 문자열)"),
                fieldWithPath("list[].category").description("카테고리 이름"),
                fieldWithPath("list[].categoryId").description("카테고리 ID"),
                fieldWithPath("list[].writer").description("작성자  (비밀 게시글일 경우 익명)"),
                fieldWithPath("list[].writerId").optional().description("작성자 (비밀 게시글일 경우 null)"),
                fieldWithPath("list[].writerThumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("작성자 썸네일 경로(비밀 게시글일 경우 / 썸네일을 등록하지 않았을 경우 null)"),
                fieldWithPath("list[].thumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("게시물 썸네일 경로 / 썸네일 등록하지 않았을 경우 null"),
                fieldWithPath("list[].visitCount").description("조회 수"),
                fieldWithPath("list[].likeCount").description("좋아요 수"),
                fieldWithPath("list[].dislikeCount").description("싫어요 수"),
                fieldWithPath("list[].commentCount").description("댓글 수"),
                fieldWithPath("list[].registerTime").description("작성 시간"),
                fieldWithPath("list[].updateTime").description("수정 시간"),
                fieldWithPath("list[].ipAddress").description("IP 주소"),
                fieldWithPath("list[].allowComment").description("댓글 허용?"),
                fieldWithPath("list[].isNotice").description("공지글?"),
                fieldWithPath("list[].isSecret").description("비밀글?"),
                fieldWithPath("list[].isTemp").description("임시저장?"),
                fieldWithPath("list[].size").description("총 게시물 수")
            )
        ));
  }

  @Test
  @DisplayName("메인페이지 글 목록 불러오기")
  public void findAllBestPosting() throws Exception {

    ResultActions result = mockMvc.perform(get("/v1/post/best")
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.list.length()", lessThanOrEqualTo(PostingService.bestPostingCount)))
        .andDo(print())
        .andDo(document("post-best",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1"),
                fieldWithPath("list[].id").description("게시물 ID"),
                fieldWithPath("list[].title").description("제목"),
                fieldWithPath("list[].userThumbnailPath").description("작성자 썸네일 이미지 조회 api path")
                    .optional(),
                fieldWithPath("list[].user").description("작성자"),
                fieldWithPath("list[].dateTime").description("작성 시간"),
                fieldWithPath("list[].watch").description("조회 수"),
                fieldWithPath("list[].commentN").description("댓글 개수"),
                fieldWithPath("list[].categoryId").description("카테고리 ID"),
                fieldWithPath("list[].category").description("카테고리명"),
                fieldWithPath("list[].thumbnailPath").description("게시글 썸네일 이미지 조회 api path")
                    .optional()
            )
        ));
  }

  @Test
  @DisplayName("게시글 하나 불러오기")
  public void getPosting() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/{pid}", postingModifyTest.getId())
            .header("Authorization", userToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getOne",
            requestParameters(
                parameterWithName("password").description("비밀번호(비밀글인 경우 검사)").optional()
            ),
            pathParameters(
                parameterWithName("pid").description("게시물 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1"),
                fieldWithPath("data.id").description("게시물 ID"),
                fieldWithPath("data.title").description("게시물 제목"),
                fieldWithPath("data.content").description("게시물 내용 (하나 조회 아닐경우 빈 문자열)"),
                fieldWithPath("data.category").description("카테고리 이름"),
                fieldWithPath("data.categoryId").description("카테고리 ID"),
                fieldWithPath("data.writer").description("작성자  (비밀 게시글일 경우 익명)"),
                fieldWithPath("data.writerId").optional().description("작성자 (비밀 게시글일 경우 null)"),
                fieldWithPath("data.writerThumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("작성자 썸네일 경로(비밀 게시글일 경우 / 썸네일을 등록하지 않았을 경우 null)"),
                fieldWithPath("data.thumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("게시물 썸네일 경로 / 썸네일 등록하지 않았을 경우 null"),
                fieldWithPath("data.visitCount").description("조회 수"),
                fieldWithPath("data.likeCount").description("좋아요 수"),
                fieldWithPath("data.dislikeCount").description("싫어요 수"),
                fieldWithPath("data.commentCount").description("댓글 수"),
                fieldWithPath("data.registerTime").description("작성 시간"),
                fieldWithPath("data.updateTime").description("수정 시간"),
                fieldWithPath("data.ipAddress").description("IP 주소"),
                fieldWithPath("data.allowComment").description("댓글 허용?"),
                fieldWithPath("data.isNotice").description("공지글?"),
                fieldWithPath("data.isSecret").description("비밀글?"),
                fieldWithPath("data.isTemp").description("임시저장?"),
                fieldWithPath("data.size").description("총 게시물 수(getPosting 에선 1개)"),
                subsectionWithPath("data.files").description(
                        "첨부파일 정보 (.id, .fileName, .filePath, .fileSize, .uploadTime, .ipAddress)")
                    .optional()
            )
        ));
  }

  @Test
  @DisplayName("시험 게시판 조건 충족 함")
  public void getPostingExamAccess() throws Exception {

    ResultActions result2 = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/{pid}", notAccessPostingTestEntity.getId())
            .header("Authorization", freshManHasAccessToken));

    result2.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.data.title").value(notAccessPostingTestEntity.getTitle()))
        .andExpect(jsonPath("$.data.content").value(notAccessPostingTestEntity.getContent()))
        .andDo(print());

  }

  @Test
  @DisplayName("시험 게시판 조건 충족 안함")
  public void getPostingAccessDeniedExamPosting() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/{pid}", notAccessPostingTestEntity.getId())
            .header("Authorization", freshManToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.data.title").value(PostingService.EXAM_ACCESS_DENIED_TITLE))
        .andExpect(jsonPath("$.data.content").value(PostingService.EXAM_ACCESS_DENIED_CONTENT))
        .andDo(print());
  }

  @Test
  @DisplayName("공지글은 조건 충족 안하더라도 열람 가능")
  public void getPostingAccessSuccessNoticePosting() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/{pid}",
                accessNoticePostingTestEntity.getId())
            .header("Authorization", freshManToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.data.title").value(accessNoticePostingTestEntity.getTitle()))
        .andExpect(jsonPath("$.data.content").value(accessNoticePostingTestEntity.getContent()))
        .andDo(print());
  }

  @Test
  @DisplayName("게시글 하나 불러오기 - 비밀글")
  public void getPostingWithSecret() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/{pid}", postingGeneralTest.getId())
            .header("Authorization", userToken)
            .param("password", postingGeneralTest.getPassword()));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getOneWithSecret",
            requestParameters(
                parameterWithName("password").description("비밀번호(비밀글인 경우 검사)").optional()
            ),
            pathParameters(
                parameterWithName("pid").description("게시물 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1"),
                fieldWithPath("data.id").description("게시물 ID"),
                fieldWithPath("data.title").description("게시물 제목"),
                fieldWithPath("data.content").description("게시물 내용 (하나 조회 아닐경우 빈 문자열)"),
                fieldWithPath("data.category").description("카테고리 이름"),
                fieldWithPath("data.categoryId").description("카테고리 ID"),
                fieldWithPath("data.writer").description("작성자  (비밀 게시글일 경우 익명)"),
                fieldWithPath("data.writerId").optional().description("작성자 (비밀 게시글일 경우 null)"),
                fieldWithPath("data.writerThumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("작성자 썸네일 경로(비밀 게시글일 경우 / 썸네일을 등록하지 않았을 경우 null)"),
                fieldWithPath("data.thumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("게시물 썸네일 경로 / 썸네일 등록하지 않았을 경우 null"),
                fieldWithPath("data.visitCount").description("조회 수"),
                fieldWithPath("data.likeCount").description("좋아요 수"),
                fieldWithPath("data.dislikeCount").description("싫어요 수"),
                fieldWithPath("data.commentCount").description("댓글 수"),
                fieldWithPath("data.registerTime").description("작성 시간"),
                fieldWithPath("data.updateTime").description("수정 시간"),
                fieldWithPath("data.ipAddress").description("IP 주소"),
                fieldWithPath("data.allowComment").description("댓글 허용?"),
                fieldWithPath("data.isNotice").description("공지글?"),
                fieldWithPath("data.isSecret").description("비밀글?"),
                fieldWithPath("data.isTemp").description("임시저장?"),
                fieldWithPath("data.size").description("총 게시물 수(getPosting 에선 1개)"),
                subsectionWithPath("data.files").description(
                        "첨부파일 정보 (.id, .fileName, .filePath, .fileSize, .uploadTime, .ipAddress)")
                    .optional()
            )
        ));
  }

  @Test
  @DisplayName("글의 첨부파일 목록 불러오기")
  public void getAttachList() throws Exception {

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/attach/{pid}",
            postingGeneralTest.getId().toString()));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getAttachList",
            pathParameters(
                parameterWithName("pid").description("게시물 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1"),
                fieldWithPath("list[].id").description("첨부파일 ID"),
                fieldWithPath("list[].fileName").description("첨부파일 이름"),
                fieldWithPath("list[].filePath").description("첨부파일 경로(상대경로)"),
                fieldWithPath("list[].fileSize").description("첨부파일 크기"),
                fieldWithPath("list[].uploadTime").description("업로드 시간"),
                fieldWithPath("list[].ipAddress").description("IP 주소")
            )
        ));
  }


  @Test
  @DisplayName("파일 다운로드 테스트")
  public void downloadFile() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/download/{fileId}",
            generalImageFile.getId().toString()));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-downloadFile",
            pathParameters(
                parameterWithName("fileId").description("파일 ID")
            )
        ));
  }

  @Test
  @DisplayName("게시글 생성")
  public void createPosting() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", getFileName(createTestImage),
        "image/jpg", new FileInputStream(userDirectory + File.separator + createTestImage));

    params.add("title", "mvc제목");
    params.add("content", "mvc내용");
    params.add("categoryId", categoryEntity.getId().toString());
    params.add("ipAddress", "192.111.222");
    params.add("allowComment", "0");
    params.add("isNotice", "0");
    params.add("isSecret", "1");
    params.add("isTemp", "0");
    params.add("password", "asd");

    ResultActions result = mockMvc.perform(
        multipart("/v1/post/new")
            .file(file)
            .file(thumbnail)
            .header("Authorization", userToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("POST");
              return request;
            }));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-create",
            requestParameters(
                parameterWithName("title").description("제목"),
                parameterWithName("content").description("내용"),
                parameterWithName("categoryId").description("게시판 종류 ID"),
                parameterWithName("ipAddress").description("IP 주소"),
                parameterWithName("allowComment").description("댓글 허용?"),
                parameterWithName("isNotice").description("공지글?"),
                parameterWithName("isSecret").description("비밀글?"),
                parameterWithName("isTemp").description("임시저장?"),
                parameterWithName("password").optional().description("비밀번호").optional()
            ),
            requestParts(
                partWithName("file").description("첨부 파일들 (form-data 에서 file= parameter 부분)"),
                partWithName("thumbnail").description(
                    "썸네일 용 이미지 (form-data 에서 thumbnail= parameter 부분)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1")
            )
        ));
  }


  @Test
  @DisplayName("게시글 수정")
  public void modifyPosting() throws Exception {
    createFileForTest(usrDir + modifyAftTestImage);
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile file = new MockMultipartFile("file", "modifyImage.png", "image/png",
        "<<png data>>".getBytes());
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail",
        getFileName(modifyAftTestImage), "image/jpg",
        new FileInputStream(usrDir + modifyAftTestImage));
    params.add("title", "수정 mvc제목");
    params.add("content", "수정 mvc내용");
    params.add("categoryId", categoryEntity.getId().toString());
    params.add("thumbnailId", modifyThumbnail.getId().toString());
    params.add("ipAddress", "192.111.222");
    params.add("allowComment", "0");
    params.add("isNotice", "0");
    params.add("isSecret", "0");
    params.add("password", "asd");
    params.add("isTemp", "0");

    log.info("mockMVc 시작");
    ResultActions result = mockMvc.perform(
        multipart("/v1/post/{pid}", postingModifyTest.getId().toString())
            .file(file)
            .file(thumbnail)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header("Authorization", userToken)
            .params(params)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }));

    log.info("mockMVc 결과");
    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-modify",
//            multipart() 요청에서는 pathParameter가 요청이 안됨. -> adoc에서 따로 작성
//            pathParameters(
//                parameterWithName("pid").description("게시물 ID")
//            ),
            requestParameters(
                parameterWithName("title").description("제목"),
                parameterWithName("content").description("내용"),
                parameterWithName("categoryId").description("게시판 종류 ID"),
                parameterWithName("thumbnailId").description("썸네일 ID"),
                parameterWithName("ipAddress").description("IP 주소"),
                parameterWithName("allowComment").description("댓글 허용?"),
                parameterWithName("isNotice").description("공지글?"),
                parameterWithName("isSecret").description("비밀글?"),
                parameterWithName("isTemp").description("임시저장?"),
                parameterWithName("password").optional().description("비밀번호").optional()
            ),
            requestParts(
                partWithName("file").description("첨부 파일들 (form-data 에서 file= parameter 부분)"),
                partWithName("thumbnail").description(
                    "썸네일 용 이미지 (form-data 에서 thumbnail= parameter 부분)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1")
            )
        ));
  }

  @Test
  @DisplayName("파일 삭제")
  public void deleteFile() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/delete/{fileId}",
                generalImageFile.getId().toString())
            .header("Authorization", userToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-file-delete",
            pathParameters(
                parameterWithName("fileId").description("삭제할 파일 ID")
            ), responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0")
            )
        ));

    Assertions.assertTrue(fileRepository.findById(generalImageFile.getId()).isEmpty());
  }

  @Test
  @DisplayName("게시글 삭제")
  public void deletePosting() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.delete("/v1/post/{pid}",
                postingDeleteTest.getId().toString())
            .header("Authorization", userToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-delete",
            pathParameters(
                parameterWithName("pid").description("게시물 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1")
            )
        ));
  }

  @Test
  @DisplayName("관리자 권한 게시물 삭제")
  public void adminDeletePosting() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.delete("/v1/admin/post/{pid}",
                postingDeleteTest2.getId().toString())
            .header("Authorization", adminToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-admin-delete",
            pathParameters(
                parameterWithName("pid").description("게시물 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1")
            )
        ));
  }

  @Test
  @DisplayName("카테고리별 게시글 검색")
  public void searchPosting() throws Exception {
    ResultActions result = mockMvc.perform(get("/v1/post/search")
        .param("type", "T")
        .param("keyword", postingGeneralTest.getTitle())
        .param("page", "0")
        .param("size", "5")
        .param("category", categoryEntity.getId().toString())
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-search",
            requestParameters(
                parameterWithName("type").description(
                    "검색 타입 (T : 제목, C: 내용, TC: 제목 또는 내용, W : 작성자)"),
                parameterWithName("keyword").description("검색어"),
                parameterWithName("category").description("게시판 종류 ID"),
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1"),
                fieldWithPath("list[].id").description("게시물 ID"),
                fieldWithPath("list[].title").description("게시물 제목"),
                fieldWithPath("list[].content").description("게시물 내용 (하나 조회 아닐경우 빈 문자열)"),
                fieldWithPath("list[].category").description("카테고리 이름"),
                fieldWithPath("list[].categoryId").description("카테고리 ID"),
                fieldWithPath("list[].writer").description("작성자  (비밀 게시글일 경우 익명)"),
                fieldWithPath("list[].writerId").optional().description("작성자 (비밀 게시글일 경우 null)"),
                fieldWithPath("list[].writerThumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("작성자 썸네일 경로(비밀 게시글일 경우 / 썸네일을 등록하지 않았을 경우 null)"),
                fieldWithPath("list[].thumbnailPath").optional().type(JsonFieldType.STRING)
                    .description("게시물 썸네일 경로 / 썸네일 등록하지 않았을 경우 null"),
                fieldWithPath("list[].visitCount").description("조회 수"),
                fieldWithPath("list[].likeCount").description("좋아요 수"),
                fieldWithPath("list[].dislikeCount").description("싫어요 수"),
                fieldWithPath("list[].commentCount").description("댓글 수"),
                fieldWithPath("list[].registerTime").description("작성 시간"),
                fieldWithPath("list[].updateTime").description("수정 시간"),
                fieldWithPath("list[].ipAddress").description("IP 주소"),
                fieldWithPath("list[].allowComment").description("댓글 허용?"),
                fieldWithPath("list[].isNotice").description("공지글?"),
                fieldWithPath("list[].isSecret").description("비밀글?"),
                fieldWithPath("list[].isTemp").description("임시저장?"),
                fieldWithPath("list[].size").description("총 게시물 수")
            )
        ));
  }

  @Test
  @DisplayName("게시글 좋아요")
  public void likePosting() throws Exception {

    ResultActions result = mockMvc.perform(get("/v1/post/like")
        .param("postingId", postingGeneralTest.getId().toString())
        .param("type", "INC")
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON));

//    result.andDo(print());

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-like",
            requestParameters(
                parameterWithName("type").description("타입 (INC : 좋아요 +, DEC : 좋아요 -)"),
                parameterWithName("postingId").description("게시판 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1")
            )
        ));
  }

  @Test
  @DisplayName("게시글 싫어요")
  public void dislikePosting() throws Exception {
    ResultActions result = mockMvc.perform(get("/v1/post/dislike")
        .param("postingId", postingGeneralTest.getId().toString())
        .param("type", "INC")
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-dislike",
            requestParameters(
                parameterWithName("type").description("타입 (INC : 싫어요 +, DEC : 싫어요 -"),
                parameterWithName("postingId").description("게시판 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1")
            )
        ));
  }

  @Test
  @DisplayName("게시글 좋아요/싫어요 여부 확인")
  public void checkMemberLikedAndDisliked() throws Exception {
    ResultActions result = mockMvc.perform(get("/v1/post/check")
        .param("postingId", postingGeneralTest.getId().toString())
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-check",
            requestParameters(
                parameterWithName("postingId").description("게시판 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("성공 : 0, 실패 시 : -1"),
                fieldWithPath("data.disliked").description("싫어요 했을시 true, 아니면 false"),
                fieldWithPath("data.liked").description("좋어요 했을시 true, 아니면 false")
            )
        ));
  }
}
