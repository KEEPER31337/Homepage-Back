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
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    member = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    adminToken = generateJWTToken(admin);
    memberToken = generateJWTToken(member);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("뱃지 추가 - 성공 [input: 비어있지 않은 jpeg 이미지]")
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

    String docMsg = "이미지 파일을 읽어들일 수 없는 경우 실패합니다.";
    String docCode =
        "이미지가 정상적이지 않는 경우: " + exceptionAdvice.getMessage("invalidImageFile.code") + " or "
            + exceptionAdvice.getMessage("invalidImageFile.code")
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    resultActions.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("badge-post",
            requestParts(
                partWithName("badge").description("뱃지 이미지 (form-data 에서 badge= parameter 부분)")
            ),
            responseFields(
                generateCommonResponseFields("성공: true +\n실패: false", docCode, docMsg)
            )
        ));
  }

  @Test
  @DisplayName("뱃지 추가 - 실패 [input: 권한 없음]")
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
  @DisplayName("뱃지 추가 - 실패 [input: 비어있는 jpeg 이미지]")
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
  @DisplayName("뱃지 수정 - 성공 [input: 비어있지 않은 jpeg 이미지]")
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

    String docMsg = "수정할 이미지의 id가 존재하지 않는 경우 실패합니다.";
    String docCode =
        "수정할 이미지가 존재하지 않을 경우: " + exceptionAdvice.getMessage("thumbnailEntityNotFoundFailed.code")
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    resultActions.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("badge-update",
            pathParameters(
                parameterWithName("badgeId").description("수정할 뱃지의 id")
            ),
            requestParts(
                partWithName("badge").description("뱃지 이미지 (form-data 에서 badge= parameter 부분)")
            ),
            responseFields(
                generateCommonResponseFields("성공: true +\n실패: false", docCode, docMsg)
            )));

  }

  @Test
  @DisplayName("뱃지 수정 - 실패 [input: 비어있는 jpeg 이미지]")
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
  @DisplayName("뱃지 수정 - 실패 [input: 존재하지 않는 id]")
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
  @DisplayName("뱃지 조회 - 성공 [input: 기존 id]")
  public void getBadgeSuccess() throws Exception {
    Long badgeId = DefaultThumbnailInfo.ThumbPosting.getThumbnailId();

    ResultActions resultActions = mockMvc.perform(get("/v1/admin/badge/{badgeId}", badgeId)
        .header("Authorization", adminToken));

    resultActions.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("badge-get",
            pathParameters(
                parameterWithName("badgeId").description("조회할 뱃지의 id")
            )));
  }

  @Test
  @DisplayName("뱃지 조회 - 실패 [input: 존재하지 않는 id]")
  public void getBadgeFailed_IdNotFound() throws Exception {
    ResultActions resultActions = mockMvc.perform(
        get("/v1/admin/badge/{badgeId}", 0)
            .header("Authorization", adminToken));

    resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andDo(print());

  }

  @Test
  @DisplayName("뱃지 삭제 - 성공 [input: 존재하는 id]")
  public void deleteBadgeSuccess() throws Exception {
    ThumbnailEntity deletedEntity = generateThumbnailEntity();

    ResultActions resultActions = mockMvc.perform(
        delete("/v1/admin/badge/{badgeId}", deletedEntity.getId())
            .header("Authorization", adminToken));

    String docMsg = "삭제할 뱃지의 id가 존재하지 않는 경우 실패합니다.";
    String docCode =
        "삭제할 뱃지가 존재하지 않을 경우: " + exceptionAdvice.getMessage("thumbnailEntityNotFoundFailed.code")
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    resultActions.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("badge-delete",
            pathParameters(
                parameterWithName("badgeId").description("삭제할 뱃지의 id")
            ),
            responseFields(
                generateCommonResponseFields("성공: true +\n실패: false", docCode, docMsg)
            )

        ));

  }

  @Test
  @DisplayName("뱃지 삭제 - 실패 [input: 존재하지 않는 id]")
  public void deleteBadgeFailed_IdNotFound() throws Exception {
    ResultActions resultActions = mockMvc.perform(
        delete("/v1/admin/badge/{badgeId}", 0)
            .header("Authorization", adminToken));

    resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andDo(print());

  }
}
