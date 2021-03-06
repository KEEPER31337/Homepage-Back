package keeper.project.homepage.admin.controller.util;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.File;
import java.io.FileInputStream;
import javax.transaction.Transactional;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Transactional
public class AdminBadgeControllerTest extends ApiControllerTestHelper {

  private MemberEntity admin;
  private MemberEntity member;
  private String adminToken;
  private String memberToken;

  @BeforeEach
  public void setUp() throws Exception {
    admin = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????, MemberRankName.????????????);
    member = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????, MemberRankName.????????????);
    adminToken = generateJWTToken(admin);
    memberToken = generateJWTToken(member);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ???????????? ?????? jpeg ?????????]")
  public void createBadgeSuccess() throws Exception {
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(System.getProperty("user.dir") + File.separator + testImagePath);
    MockMultipartFile testMockImage = new MockMultipartFile("badge",
        getFileName(testImagePath), MediaType.IMAGE_JPEG_VALUE,
        new FileInputStream(System.getProperty("user.dir") + File.separator + testImagePath));

    ResultActions resultActions = mockMvc.perform(multipart("/v1/admin/badge/")
        .file(testMockImage)
        .header("Authorization", adminToken)
        .header("X-FORWARDED-FOR", "127.0.0.1")
        .with(request -> {
          request.setMethod("POST");
          return request;
        }));

    String docMsg = "????????? ????????? ???????????? ??? ?????? ?????? ???????????????.";
    String docCode =
        "???????????? ??????????????? ?????? ??????: " + exceptionAdvice.getMessage("invalidImageFile.code") + " or "
            + exceptionAdvice.getMessage("invalidImageFile.code")
            + " +\n" + "??? ??? ????????? ????????? ??????: " + exceptionAdvice.getMessage("unKnown.code");
    resultActions.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("badge-post",
            requestParts(
                partWithName("badge").description("?????? ????????? (form-data ?????? badge= parameter ??????)")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", docCode, docMsg)
            )
        ));
  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ?????? ??????]")
  public void createBadgeFailed_AccessDenied() throws Exception {
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(System.getProperty("user.dir") + File.separator + testImagePath);
    MockMultipartFile testMockImage = new MockMultipartFile("badge",
        getFileName(testImagePath), "image/jpg",
        new FileInputStream(System.getProperty("user.dir") + File.separator + testImagePath));

    ResultActions resultActions = mockMvc.perform(multipart("/v1/admin/badge/")
        .file(testMockImage)
        .header("Authorization", memberToken)
        .header("X-FORWARDED-FOR", "127.0.0.1")
        .with(request -> {
          request.setMethod("POST");
          return request;
        }));

    resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andDo(print());

  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ???????????? jpeg ?????????]")
  public void createBadgeFailed_InputEmptyImage() throws Exception {

    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    MockMultipartFile testMockImage = new MockMultipartFile("badge",
        getFileName(testImagePath), "image/jpg", "".getBytes());

    ResultActions resultActions = mockMvc.perform(multipart("/v1/admin/badge/")
        .file(testMockImage)
        .header("Authorization", adminToken)
        .header("X-FORWARDED-FOR", "127.0.0.1")
        .with(request -> {
          request.setMethod("POST");
          return request;
        }));

    resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andDo(print());
  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ???????????? ?????? jpeg ?????????]")
  public void updateBadgeSuccess() throws Exception {
    ThumbnailEntity prevBadgeEntity = generateThumbnailEntity();

    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(System.getProperty("user.dir") + File.separator + testImagePath);
    MockMultipartFile testMockImage = new MockMultipartFile("badge",
        getFileName(testImagePath), MediaType.IMAGE_JPEG_VALUE,
        new FileInputStream(System.getProperty("user.dir") + File.separator + testImagePath));

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.fileUpload("/v1/admin/badge/{badgeId}",
                prevBadgeEntity.getId())
            .file(testMockImage)
            .header("Authorization", adminToken)
            .header("X-FORWARDED-FOR", "127.0.0.1")
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }));

    String docMsg = "????????? ???????????? id??? ???????????? ?????? ?????? ???????????????.";
    String docCode =
        "????????? ???????????? ???????????? ?????? ??????: " + exceptionAdvice.getMessage("thumbnailEntityNotFoundFailed.code")
            + " +\n" + "??? ??? ????????? ????????? ??????: " + exceptionAdvice.getMessage("unKnown.code");
    resultActions.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("badge-update",
            pathParameters(
                parameterWithName("badgeId").description("????????? ????????? id")
            ),
            requestParts(
                partWithName("badge").description("?????? ????????? (form-data ?????? badge= parameter ??????)")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", docCode, docMsg)
            )));

  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ???????????? jpeg ?????????]")
  public void updateBadgeFailed_InputEmptyImage() throws Exception {
    ThumbnailEntity prevBadgeEntity = generateThumbnailEntity();

    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(System.getProperty("user.dir") + File.separator + testImagePath);
    MockMultipartFile testMockImage = new MockMultipartFile("badge",
        getFileName(testImagePath), MediaType.IMAGE_JPEG_VALUE, "".getBytes());

    ResultActions resultActions = mockMvc.perform(
        multipart("/v1/admin/badge/{badgeId}", prevBadgeEntity.getId())
            .file(testMockImage)
            .header("Authorization", adminToken)
            .header("X-FORWARDED-FOR", "127.0.0.1")
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }));

    resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andDo(print());
  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ???????????? ?????? id]")
  public void updateBadgeFailed_IdNotFound() throws Exception {
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(System.getProperty("user.dir") + File.separator + testImagePath);
    MockMultipartFile testMockImage = new MockMultipartFile("badge",
        getFileName(testImagePath), MediaType.IMAGE_JPEG_VALUE, "".getBytes());

    ResultActions resultActions = mockMvc.perform(
        multipart("/v1/admin/badge/{badgeId}", 0)
            .file(testMockImage)
            .header("Authorization", adminToken)
            .header("X-FORWARDED-FOR", "127.0.0.1")
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }));

    resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andDo(print());

  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ?????? id]")
  public void getBadgeSuccess() throws Exception {
    Long badgeId = ThumbType.PostThumbnail.getDefaultThumbnailId();

    ResultActions resultActions = mockMvc.perform(get("/v1/admin/badge/{badgeId}", badgeId)
        .header("Authorization", adminToken));

    resultActions.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("badge-get",
            pathParameters(
                parameterWithName("badgeId").description("????????? ????????? id")
            )));
  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ???????????? ?????? id]")
  public void getBadgeFailed_IdNotFound() throws Exception {
    ResultActions resultActions = mockMvc.perform(
        get("/v1/admin/badge/{badgeId}", 0)
            .header("Authorization", adminToken));

    resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andDo(print());

  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ???????????? id]")
  public void deleteBadgeSuccess() throws Exception {
    ThumbnailEntity deletedEntity = generateThumbnailEntity();

    ResultActions resultActions = mockMvc.perform(
        delete("/v1/admin/badge/{badgeId}", deletedEntity.getId())
            .header("Authorization", adminToken));

    String docMsg = "????????? ????????? id??? ???????????? ?????? ?????? ???????????????.";
    String docCode =
        "????????? ????????? ???????????? ?????? ??????: " + exceptionAdvice.getMessage("thumbnailEntityNotFoundFailed.code")
            + " +\n" + "??? ??? ????????? ????????? ??????: " + exceptionAdvice.getMessage("unKnown.code");
    resultActions.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("badge-delete",
            pathParameters(
                parameterWithName("badgeId").description("????????? ????????? id")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", docCode, docMsg)
            )

        ));

  }

  @Test
  @DisplayName("?????? ?????? - ?????? [input: ???????????? ?????? id]")
  public void deleteBadgeFailed_IdNotFound() throws Exception {
    ResultActions resultActions = mockMvc.perform(
        delete("/v1/admin/badge/{badgeId}", 0)
            .header("Authorization", adminToken));

    resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andDo(print());

  }
}
