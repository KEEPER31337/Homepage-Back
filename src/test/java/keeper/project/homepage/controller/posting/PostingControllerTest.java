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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.common.FileConversion;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
@Log4j2
public class PostingControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";
  final private String ipAddress1 = "127.0.0.1";
  final private String ipAddress2 = "127.0.0.2";

  private String userToken;

  private MemberEntity memberEntity;
  private CategoryEntity categoryEntity;
  private PostingEntity postingGeneralTest;
  private PostingEntity postingDeleteTest;
  private PostingEntity postingModifyTest;

  private ThumbnailEntity generalThumbnail;
  private FileEntity generalImageFile;
  private ThumbnailEntity deleteThumbnail;
  private FileEntity deleteImageFile;
  private ThumbnailEntity modifyThumbnail;
  private FileEntity modifyImageFile;

  private final String userDirectory = System.getProperty("user.dir");
  private final String generalTestImage = "keeper_files" + File.separator + "image.jpg";
  private final String generalThumbnailImage =
      "keeper_files" + File.separator + "thumbnail" + File.separator + "t_image.jpg";
  private final String deleteTestImage = "keeper_files" + File.separator + "image2.jpg";
  private final String deleteThumbnailImage =
      "keeper_files" + File.separator + "thumbnail" + File.separator + "t_image2.jpg";
  private final String createTestImage = "keeper_files" + File.separator + "createTest.jpg";
  private final String modifyBefTestImage = "keeper_files" + File.separator + "modifyBefTest.jpg";
  private final String modifyBefThumbnailImage =
      "keeper_files" + File.separator + "thumbnail" + File.separator + "modifyBefTest.jpg";
  private final String modifyAftTestImage = "keeper_files" + File.separator + "modifyAftTest.jpg";

  private String getFileName(String filePath) {
    File file = new File(filePath);
    return file.getName();
  }

  @BeforeAll
  public static void createFile() {
    final String keeperFilesDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files";
    final String thumbnailDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "thumbnail";
    final String generalImagePath = keeperFilesDirectoryPath + File.separator + "image.jpg";
    final String generalThumbnail = thumbnailDirectoryPath + File.separator + "t_image.jpg";
    final String deleteTestImage = keeperFilesDirectoryPath + File.separator + "image2.jpg";
    final String deleteThumbnail = thumbnailDirectoryPath + File.separator + "t_image2.jpg";
    final String createTestImage = keeperFilesDirectoryPath + File.separator + "createTest.jpg";
    final String modifyBefTestImage =
        keeperFilesDirectoryPath + File.separator + "modifyBefTest.jpg";
    final String modifyBefThumbnail =
        thumbnailDirectoryPath + File.separator + "modifyBefTest.jpg";
    final String modifyAftTestImage =
        keeperFilesDirectoryPath + File.separator + "modifyAftTest.jpg";

    File keeperFilesDir = new File(keeperFilesDirectoryPath);
    File thumbnailDir = new File(thumbnailDirectoryPath);

    if (!keeperFilesDir.exists()) {
      keeperFilesDir.mkdir();
    }

    if (!thumbnailDir.exists()) {
      thumbnailDir.mkdir();
    }

    createImageForTest(generalImagePath);
    createImageForTest(generalThumbnail);
    createImageForTest(deleteTestImage);
    createImageForTest(deleteThumbnail);
    createImageForTest(createTestImage);
    createImageForTest(modifyBefTestImage);
    createImageForTest(modifyBefThumbnail);
    createImageForTest(modifyAftTestImage);
  }

  private static void createImageForTest(String filePath) {
    FileConversion fileConversion = new FileConversion();
    fileConversion.makeSampleJPEGImage(filePath);
  }

  @BeforeEach
  public void setUp() throws Exception {

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

    String content = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
        + "    \"password\": \"" + password + "\"\n"
        + "}";
    MvcResult result = mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    String resultString = result.getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    SingleResult<SignInDto> sign = mapper.readValue(resultString, new TypeReference<>() {
    });
    userToken = sign.getData().getToken();

    categoryEntity = CategoryEntity.builder()
        .name("테스트 게시판").build();
    categoryRepository.save(categoryEntity);

    generalImageFile = FileEntity.builder()
        .fileName(getFileName(generalTestImage))
        .filePath(generalTestImage)
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(generalImageFile);

    generalThumbnail = ThumbnailEntity.builder()
        .path(generalThumbnailImage)
        .file(generalImageFile).build();
    thumbnailRepository.save(generalThumbnail);

    deleteImageFile = FileEntity.builder()
        .fileName(getFileName(deleteTestImage))
        .filePath(deleteTestImage)
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(deleteImageFile);

    deleteThumbnail = ThumbnailEntity.builder()
        .path(deleteThumbnailImage)
        .file(deleteImageFile).build();
    thumbnailRepository.save(deleteThumbnail);

    modifyImageFile = FileEntity.builder()
        .fileName(getFileName(modifyBefTestImage))
        .filePath(modifyBefTestImage)
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(modifyImageFile);

    modifyThumbnail = ThumbnailEntity.builder()
        .path(modifyBefThumbnailImage)
        .file(modifyImageFile).build();
    thumbnailRepository.save(modifyThumbnail);

    postingGeneralTest = postingRepository.save(PostingEntity.builder()
        .title("test 게시판 제목")
        .content("test 게시판 제목 내용")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnailId(generalThumbnail)
        .ipAddress("192.11.222.333")
        .allowComment(0)
        .isNotice(0)
        .isTemp(0)
        .isSecret(1)
        .likeCount(0)
        .dislikeCount(0)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asd")
        .build());
    memberEntity.getPosting().add(postingGeneralTest);

    postingModifyTest = postingRepository.save(PostingEntity.builder()
        .title("test 게시판 수정용 제목")
        .content("test 게시판 수정용 내용")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnailId(modifyThumbnail)
        .ipAddress("192.11.223")
        .allowComment(0)
        .isNotice(0)
        .isSecret(0)
        .isTemp(0)
        .likeCount(0)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asd2")
        .build());
    memberEntity.getPosting().add(postingModifyTest);

    postingDeleteTest = postingRepository.save(PostingEntity.builder()
        .title("test 게시판 제목2")
        .content("test 게시판 제목 내용2")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnailId(deleteThumbnail)
        .ipAddress("192.11.223")
        .allowComment(0)
        .isNotice(0)
        .isSecret(0)
        .isTemp(0)
        .likeCount(0)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asd2")
        .build());
    memberEntity.getPosting().add(postingDeleteTest);

    PostingEntity postingTempTest = postingRepository.save(PostingEntity.builder()
        .title("임시 게시글 제목")
        .content("임시 게시글 내용")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnailId(deleteThumbnail)
        .ipAddress("192.11.223")
        .allowComment(0)
        .isNotice(0)
        .isSecret(0)
        .isTemp(1)
        .likeCount(0)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asd2")
        .build());
    memberEntity.getPosting().add(postingTempTest);

    fileRepository.save(FileEntity.builder()
        .postingId(postingGeneralTest)
        .fileName("test file")
        .filePath("test/file.txt")
        .fileSize(12345L)
        .uploadTime(new Date())
        .ipAddress(postingGeneralTest.getIpAddress())
        .build());

    fileRepository.save(FileEntity.builder()
        .postingId(postingModifyTest)
        .fileName("test file")
        .filePath("test/file.txt")
        .fileSize(12345L)
        .uploadTime(new Date())
        .ipAddress(postingModifyTest.getIpAddress())
        .build());
  }

  @Test
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
                fieldWithPath("list[].content").description("게시물 내용"),
                fieldWithPath("list[].writer").optional().description("작성자 (비밀 게시글일 경우 익명)"),
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
                fieldWithPath("list[].isTemp").description("임시저장?")
            )
        ));
  }

  @Test
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
                fieldWithPath("list[].content").description("게시물 내용"),
                fieldWithPath("list[].writer").description("작성자  (비밀 게시글일 경우 익명)"),
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
                fieldWithPath("list[].isTemp").description("임시저장?")
            )
        ));
  }

  @Test
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
                fieldWithPath("data.content").description("게시물 내용"),
                fieldWithPath("data.writer").description("작성자  (비밀 게시글일 경우 익명)"),
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
                subsectionWithPath("files").description("첨부파일 정보"),
                fieldWithPath("files[].id").description("첨부파일 ID").optional(),
                fieldWithPath("files[].fileName").description("첨부파일 이름").optional(),
                fieldWithPath("files[].filePath").description("첨부파일 경로").optional(),
                fieldWithPath("files[].fileSize").description("첨부파일 크기").optional(),
                fieldWithPath("files[].uploadTime").description("업로드 시간").optional(),
                fieldWithPath("files[].ipAddress").description("IP 주소").optional(),
                fieldWithPath("thumbnail.id").description("썸네일 ID").optional(),
                fieldWithPath("thumbnail.path").description("썸네일 경로(상대경로)").optional()
            )
        ));
  }

  @Test
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
                fieldWithPath("data.content").description("게시물 내용"),
                fieldWithPath("data.writer").description("작성자  (비밀 게시글일 경우 익명)"),
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
                subsectionWithPath("files").description("첨부파일 정보"),
                fieldWithPath("files[].id").description("첨부파일 ID").optional(),
                fieldWithPath("files[].fileName").description("첨부파일 이름").optional(),
                fieldWithPath("files[].filePath").description("첨부파일 경로").optional(),
                fieldWithPath("files[].fileSize").description("첨부파일 크기").optional(),
                fieldWithPath("files[].uploadTime").description("업로드 시간").optional(),
                fieldWithPath("files[].ipAddress").description("IP 주소").optional(),
                fieldWithPath("thumbnail.id").description("썸네일 ID").optional(),
                fieldWithPath("thumbnail.path").description("썸네일 경로(상대경로)").optional()
            )
        ));
  }

  @Test
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
  public void modifyPosting() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile file = new MockMultipartFile("file", "modifyImage.png", "image/png",
        "<<png data>>".getBytes());
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail",
        getFileName(modifyAftTestImage), "image/jpg",
        new FileInputStream(userDirectory + File.separator + modifyAftTestImage));
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
                parameterWithName("password").optional().description("비밀번호").optional(),
                parameterWithName("isTemp").description("임시저장?")
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
  public void searchPosting() throws Exception {
    ResultActions result = mockMvc.perform(get("/v1/post/search")
        .param("type", "T")
        .param("keyword", "2")
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
                fieldWithPath("list[].content").description("게시물 내용"),
                fieldWithPath("list[].writer").description("작성자  (비밀 게시글일 경우 익명)"),
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
                fieldWithPath("list[].isTemp").description("임시저장?")
            )
        ));
  }

  @Test
  @Transactional
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
