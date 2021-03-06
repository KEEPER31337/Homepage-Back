package keeper.project.homepage.controller.posting;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.exception.posting.CustomCategoryNotFoundException;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.service.posting.PostingService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
@Log4j2
public class PostingControllerTest extends ApiControllerTestHelper {

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
  private ThumbnailEntity modifyThumbnail;

  private final String userDirectory = System.getProperty("user.dir");
  private final String createTestImage = testFileRelDir + File.separator + "createTest.jpg";
  private final String modifyAftTestImage = testFileRelDir + File.separator + "modifyAftTest.jpg";

  @BeforeEach
  public void setUp() throws Exception {
    createFileForTest(usrDir + createTestImage);
    memberEntity = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????, MemberRankName.????????????);
    adminEntity = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????, MemberRankName.????????????);
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
    postingNoticeTest = generatePostingEntity(memberEntity, categoryEntity, 1, 0, 0);
    postingNoticeTest2 = generatePostingEntity(memberEntity, categoryEntity, 1, 0, 0);

    FileEntity generalTestFile = generateFileEntity();
    generalTestFile.setPostingId(postingGeneralTest);
    fileRepository.save(generalTestFile);
    postingGeneralTest.getFiles().add(generalTestFile);
    FileEntity generalModifyFile = generateFileEntity();
    generalModifyFile.setPostingId(postingModifyTest);
    fileRepository.save(generalModifyFile);

    freshMan = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????,
        MemberRankName.????????????);
    freshMan.changeGeneration(13);
    freshManToken = generateJWTToken(freshMan);

    freshManHasAccess = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????,
        MemberRankName.????????????);
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
  @DisplayName("?????? ??? ?????? ????????????")
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
                generateCommonPagingParameters("??? ???????????? ?????? ???(default = 10)")
            ),
            responseFields(
                generatePostingResponseFields(ResponseType.LIST, "?????? : true + \n?????? : false",
                    "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("??????????????? ??? ?????? ????????????")
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
                generateCommonPagingParameters("??? ???????????? ?????? ???(default = 10)",
                    parameterWithName("category").description("????????? ?????? ID"))
            ),
            responseFields(
                generatePostingResponseFields(ResponseType.LIST, "?????? : true + \n?????? : false",
                    "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("????????? ?????? ????????????(??????????????? or ??????)")
  public void findAllNoticePostingByCategoryId() throws Exception {

    ResultActions result = mockMvc.perform(get("/v1/post/notice")
        .param("category", categoryEntity.getId().toString())
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getNotice",
            requestParameters(
                parameterWithName("category").description("????????? ?????? ID / ?????? ????????? ?????? ???????????? ????????? ?????????")
            ),
            responseFields(
                generatePostingResponseFields(ResponseType.LIST, "?????? : true + \n?????? : false",
                    "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("??????????????? ??? ?????? ????????????")
  public void findAllBestPosting() throws Exception {

    ResultActions result = mockMvc.perform(get("/v1/post/best")
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.list.length()", lessThanOrEqualTo(PostingService.bestPostingCount)))
        .andDo(print())
        .andDo(document("post-best",
            responseFields(
                fieldWithPath("success").description("??????: true +\n??????: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("?????? : 0, ?????? ??? : -1"),
                fieldWithPath("list[].id").description("????????? ID"),
                fieldWithPath("list[].title").description("??????"),
                fieldWithPath("list[].userThumbnailPath").description("????????? ????????? ????????? ?????? api path")
                    .optional(),
                fieldWithPath("list[].user").description("?????????"),
                fieldWithPath("list[].dateTime").description("?????? ??????"),
                fieldWithPath("list[].watch").description("?????? ???"),
                fieldWithPath("list[].commentN").description("?????? ??????"),
                fieldWithPath("list[].categoryId").description("???????????? ID"),
                fieldWithPath("list[].category").description("???????????????"),
                fieldWithPath("list[].thumbnailPath").description("????????? ????????? ????????? ?????? api path")
                    .optional()
            )
        ));
  }

  @Test
  @DisplayName("????????? ?????? ????????????")
  public void getPosting() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/{pid}", postingModifyTest.getId())
            .header("Authorization", userToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getOne",
            requestParameters(
                parameterWithName("password").description("????????????(???????????? ?????? ??????)").optional()
            ),
            pathParameters(
                parameterWithName("pid").description("????????? ID")
            ),
            responseFields(
                generatePostingResponseFields(ResponseType.SINGLE, "?????? : true + \n?????? : false",
                    "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("?????? ????????? ?????? ?????? ???")
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
  @DisplayName("?????? ????????? ?????? ?????? ??????")
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
  @DisplayName("???????????? ?????? ?????? ??????????????? ?????? ??????")
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
  @DisplayName("????????? ?????? ???????????? - ?????????")
  public void getPostingWithSecret() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/{pid}", postingGeneralTest.getId())
            .header("Authorization", userToken)
            .param("password", postingGeneralTest.getPassword()));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getOneWithSecret",
            requestParameters(
                parameterWithName("password").description("????????????(???????????? ?????? ??????)").optional()
            ),
            pathParameters(
                parameterWithName("pid").description("????????? ID")
            ),
            responseFields(
                generatePostingResponseFields(ResponseType.SINGLE, "?????? : true + \n?????? : false",
                    "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("?????? ???????????? ?????? ????????????")
  public void getAttachList() throws Exception {

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/attach/{pid}",
            postingGeneralTest.getId().toString()));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getAttachList",
            pathParameters(
                parameterWithName("pid").description("????????? ID")
            ),
            responseFields(
                fieldWithPath("success").description("??????: true +\n??????: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("?????? : 0, ?????? ??? : -1"),
                fieldWithPath("list[].id").description("???????????? ID"),
                fieldWithPath("list[].fileName").description("???????????? ??????"),
                fieldWithPath("list[].filePath").description("???????????? ??????(????????????)"),
                fieldWithPath("list[].fileSize").description("???????????? ??????"),
                fieldWithPath("list[].uploadTime").description("????????? ??????"),
                fieldWithPath("list[].ipAddress").description("IP ??????")
            )
        ));
  }


  @Test
  @DisplayName("?????? ???????????? ?????????")
  public void downloadFile() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/download/{fileId}",
            generalImageFile.getId().toString()));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-downloadFile",
            pathParameters(
                parameterWithName("fileId").description("?????? ID")
            )
        ));
  }

  public MultiValueMap<String, String> generatePostingParams(boolean isModify) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", "mvc??????");
    params.add("content", "mvc??????");
    params.add("categoryId", categoryEntity.getId().toString());
    params.add("allowComment", "0");
    params.add("isNotice", "0");
    params.add("isSecret", "1");
    params.add("isTemp", "0");
    params.add("password", "asd");
    if (isModify) {
      params.add("thumbnailId", modifyThumbnail.getId().toString());
    }

    return params;
  }

  public List<ParameterDescriptor> generateCommonPostingParameters(boolean isModify) {
    List<ParameterDescriptor> parameterDescriptors = new ArrayList<>(Arrays.asList(
        parameterWithName("title").description("??????"),
        parameterWithName("content").description("??????"),
        parameterWithName("categoryId").description("????????? ?????? ID"),
        parameterWithName("allowComment").description("?????? ???????"),
        parameterWithName("isNotice").description("??????????"),
        parameterWithName("isSecret").description("??????????"),
        parameterWithName("isTemp").description("?????????????"),
        parameterWithName("password").optional().description("????????????").optional()
    ));
    if (isModify) {
      parameterDescriptors.add(parameterWithName("thumbnailId").description("????????? ID"));
    }

    return parameterDescriptors;
  }

  @Test
  @DisplayName("????????? ??????")
  public void createPosting() throws Exception {
    MultiValueMap<String, String> params = generatePostingParams(false);
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", getFileName(createTestImage),
        "image/jpg", new FileInputStream(userDirectory + File.separator + createTestImage));

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
                generateCommonPostingParameters(false)
            ),
            requestParts(
                partWithName("file").description("?????? ????????? (form-data ?????? file= parameter ??????)"),
                partWithName("thumbnail").description(
                    "????????? ??? ????????? (form-data ?????? thumbnail= parameter ??????)")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("????????? ??????")
  public void modifyPosting() throws Exception {
    createFileForTest(usrDir + modifyAftTestImage);
    MultiValueMap<String, String> params = generatePostingParams(true);
    MockMultipartFile file = new MockMultipartFile("file", "modifyImage.png", "image/png",
        "<<png data>>".getBytes());
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail",
        getFileName(modifyAftTestImage), "image/jpg",
        new FileInputStream(usrDir + modifyAftTestImage));

    log.info("mockMVc ??????");
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

    log.info("mockMVc ??????");
    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-modify",
            requestParameters(
                generateCommonPostingParameters(true)
            ),
            requestParts(
                partWithName("file").description("?????? ????????? (form-data ?????? file= parameter ??????)"),
                partWithName("thumbnail").description(
                    "????????? ??? ????????? (form-data ?????? thumbnail= parameter ??????)")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("?????? ??????")
  public void deleteFile() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/delete/{fileId}",
                generalImageFile.getId().toString())
            .header("Authorization", userToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-file-delete",
            pathParameters(
                parameterWithName("fileId").description("????????? ?????? ID")
            ), responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", "?????? : 0, ?????? ??? : -11000", "")
            )
        ));

    Assertions.assertTrue(fileRepository.findById(generalImageFile.getId()).isEmpty());
  }

  @Test
  @DisplayName("?????? ID ???????????? ?????? ????????? ??????")
  public void deleteFiles() throws Exception {
    FileEntity generalImageFile2 = generateFileEntity();
    FileEntity generalImageFile3 = generateFileEntity();
    String params = generalImageFile.getId().toString() + "," +
        generalImageFile2.getId().toString() + "," +
        generalImageFile3.getId().toString();

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.delete("/v1/post/files")
            .param("fileIdList", params)
            .header("Authorization", userToken));
    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-delete-files",
            requestParameters(
                parameterWithName("fileIdList").description("????????? ?????? ID ?????????")
            ), responseFields(
                fieldWithPath("success").description("??????: true +\n??????: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("?????? : 0")
            )
        ));

    Assertions.assertTrue(fileRepository.findById(generalImageFile.getId()).isEmpty());
    Assertions.assertTrue(fileRepository.findById(generalImageFile2.getId()).isEmpty());
    Assertions.assertTrue(fileRepository.findById(generalImageFile3.getId()).isEmpty());

  }

  @Test
  @DisplayName("????????? ??????")
  public void deletePosting() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.delete("/v1/post/{pid}",
                postingDeleteTest.getId().toString())
            .header("Authorization", userToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-delete",
            pathParameters(
                parameterWithName("pid").description("????????? ID")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("????????? ?????? ????????? ??????")
  public void adminDeletePosting() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.delete("/v1/admin/post/{pid}",
                postingDeleteTest2.getId().toString())
            .header("Authorization", adminToken));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-admin-delete",
            pathParameters(
                parameterWithName("pid").description("????????? ID")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("??????????????? ????????? ??????")
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
                generateCommonPagingParameters("??? ???????????? ?????? ???(default = 10)",
                    parameterWithName("type").description(
                        "?????? ?????? (T : ??????, C: ??????, TC: ?????? ?????? ??????, W : ?????????)"),
                    parameterWithName("keyword").description("?????????"),
                    parameterWithName("category").description("????????? ?????? ID"))
            ),
            responseFields(
                generatePostingResponseFields(ResponseType.LIST, "?????? : true + \n?????? : false",
                    "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("????????? ?????????")
  public void likePosting() throws Exception {

    ResultActions result = mockMvc.perform(get("/v1/post/like")
        .param("postingId", postingGeneralTest.getId().toString())
        .param("type", "INC")
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-like",
            requestParameters(
                parameterWithName("type").description("?????? (INC : ????????? +, DEC : ????????? -)"),
                parameterWithName("postingId").description("????????? ID")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("????????? ?????????")
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
                parameterWithName("type").description("?????? (INC : ????????? +, DEC : ????????? -"),
                parameterWithName("postingId").description("????????? ID")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", "?????? : 0, ?????? ??? : -11000", "")
            )
        ));
  }

  @Test
  @DisplayName("????????? ?????????/????????? ?????? ??????")
  public void checkMemberLikedAndDisliked() throws Exception {
    ResultActions result = mockMvc.perform(get("/v1/post/check")
        .param("postingId", postingGeneralTest.getId().toString())
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-check",
            requestParameters(
                parameterWithName("postingId").description("????????? ID")
            ),
            responseFields(
                fieldWithPath("success").description("??????: true +\n??????: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("?????? : 0, ?????? ??? : -1"),
                fieldWithPath("data.disliked").description("????????? ????????? true, ????????? false"),
                fieldWithPath("data.liked").description("????????? ????????? true, ????????? false")
            )
        ));
  }
}
