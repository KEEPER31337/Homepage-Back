package keeper.project.homepage.admin.controller.about;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import keeper.project.homepage.entity.about.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
@Log4j2
public class AdminStaticWriteSubtitleImageControllerTest extends AdminStaticWriteTestHelper {

  private MemberEntity generalMember;
  private String generalToken;
  private MemberEntity adminMember;
  private String adminToken;

  private StaticWriteTitleEntity staticWriteTitleEntity;
  private StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity, defaultStaticWirteSubtitleImageEntity;

  @BeforeEach
  public void setUp() throws Exception {
    generalMember = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????, MemberRankName.????????????);
    adminMember = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????, MemberRankName.????????????);
    generalToken = generateJWTToken(generalMember.getLoginId(), memberPassword);
    adminToken = generateJWTToken(adminMember.getLoginId(), memberPassword);

    staticWriteTitleEntity = generateTestTitle(1);
    staticWriteSubtitleImageEntity = generateTestSubtitleImage(staticWriteTitleEntity, 1);
    defaultStaticWirteSubtitleImageEntity = generateTestSubtitleImage(staticWriteTitleEntity, 2);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????")
  public void createSubTitleSuccess() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "????????? ?????? ?????????");
    params.add("staticWriteTitleId", "1");
    params.add("displayOrder", "1");
    params.add("ipAddress", "192.111.222");

    mockMvc.perform(multipart("/v1/admin/about/sub-titles")
            .file(image)
            .params(params)
            .header("Authorization", adminToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> {
              request.setMethod("POST");
              return request;
            }))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("adminStaticWriteSubtitleImage-create",
            requestParameters(
                parameterWithName("subtitle").description("??????????????? ?????? ????????? ?????? ?????? ???????????? ?????????"),
                parameterWithName("staticWriteTitleId").description(
                    "??????????????? ?????? ????????? ?????? ?????? ???????????? ???????????? ????????? ?????? ???????????? ID"),
                parameterWithName("displayOrder").description("??????????????? ?????? ????????? ?????? ?????? ???????????? ???????????? ??????"),
                parameterWithName("ipAddress").description("IP ??????")
            ),
            requestParts(
                partWithName("thumbnail").description(
                    "????????? ??? ????????? (form-data ?????? thumbnail= parameter ??????)")
            ),
            responseFields(
                fieldWithPath("success").description("??????: true +\n??????: false"),
                fieldWithPath("code").description("?????? ??? 0??? ??????"),
                fieldWithPath("msg").description("??????: ????????????????????? +\n??????: ?????? ????????? ??????"),
                fieldWithPath("data.id").description("????????? ????????? ????????? ?????? ?????? ???????????? ID"),
                fieldWithPath("data.subtitle").description("????????? ????????? ????????? ?????? ?????? ???????????? ?????????"),
                fieldWithPath("data.staticWriteTitleId").description(
                    "????????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ?????? ???????????? ID"),
                subsectionWithPath("data.thumbnailPath").description(
                    "????????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ???????????? ???????????? api path"),
                fieldWithPath("data.displayOrder").description("????????? ????????? ????????? ?????? ?????? ???????????? ???????????? ??????"),
                subsectionWithPath("data.staticWriteContents[]").description(
                    "????????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ?????? ????????? ????????? ?????????")
            )));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????(???????????? ?????? ??????)")
  public void createSubTitleSuccess_NullThumbnail() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    params.add("subtitle", "????????? ?????? ?????????");
    params.add("staticWriteTitleId", "1");
    params.add("displayOrder", "1");
    params.add("ipAddress", "192.111.222");

    mockMvc.perform(multipart("/v1/admin/about/sub-titles")
            .params(params)
            .header("Authorization", adminToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> {
              request.setMethod("POST");
              return request;
            }))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????(????????? ????????? ??????)")
  public void createSubTitleFail_Auth() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "????????? ?????? ?????????");
    params.add("staticWriteTitleId", "1");
    params.add("displayOrder", "1");
    params.add("ipAddress", "192.111.222");

    mockMvc.perform(multipart("/v1/admin/about/sub-titles")
            .file(image)
            .params(params)
            .header("Authorization", generalToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> {
              request.setMethod("POST");
              return request;
            }))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????(???????????? ?????? ?????????)")
  public void createSubTitleFail_Title() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = usrDir + testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "????????? ?????? ?????????");
    params.add("staticWriteTitleId", "7");
    params.add("displayOrder", "1");
    params.add("ipAddress", "192.111.222");

    mockMvc.perform(multipart("/v1/admin/about/sub-titles")
            .file(image)
            .params(params)
            .header("Authorization", adminToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> {
              request.setMethod("POST");
              return request;
            }))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("???????????? ?????? ???????????? ?????????????????????."));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????")
  public void modifySubtitleByIdSuccess() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "????????? ?????? ?????????");
    params.add("staticWriteTitleId", "1");
    params.add("displayOrder", "7");
    params.add("ipAddress", "192.111.777");

    mockMvc.perform(
            RestDocumentationRequestBuilders.fileUpload("/v1/admin/about/sub-titles/{id}",
                    staticWriteSubtitleImageEntity.getId())
                .file(image)
                .params(params)
                .header("Authorization", adminToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(request -> {
                  request.setMethod("PUT");
                  return request;
                }))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("adminStaticWriteSubtitleImage-update",
            pathParameters(
                parameterWithName("id").description("??????????????? ?????? ????????? ?????? ?????? ???????????? ID")
            ),
            requestParameters(
                parameterWithName("subtitle").description(
                    "??????????????? ?????? ????????? ?????? ?????? ???????????? ?????????(????????? ?????? ??? ?????????)"),
                parameterWithName("staticWriteTitleId").description(
                    "??????????????? ?????? ????????? ?????? ?????? ???????????? ???????????? ????????? ?????? ???????????? ID(????????? ?????? ??? ?????????)"),
                parameterWithName("displayOrder").description(
                    "??????????????? ?????? ????????? ?????? ?????? ???????????? ???????????? ??????(????????? ?????? ??? ?????????)"),
                parameterWithName("ipAddress").description("IP ??????(????????? ?????? ??? ?????????)")
            ),
            requestParts(
                partWithName("thumbnail").description(
                    "????????? ??? ????????? (form-data ?????? thumbnail= parameter ??????) (????????? ?????? ??? ?????????)")
            ),
            responseFields(
                fieldWithPath("success").description("??????: true +\n??????: false"),
                fieldWithPath("code").description("?????? ??? 0??? ??????"),
                fieldWithPath("msg").description("??????: ????????????????????? +\n??????: ?????? ????????? ??????"),
                fieldWithPath("data.id").description("????????? ????????? ????????? ?????? ?????? ???????????? ID"),
                fieldWithPath("data.subtitle").description("????????? ????????? ????????? ?????? ?????? ???????????? ?????????"),
                fieldWithPath("data.staticWriteTitleId").description(
                    "????????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ?????? ???????????? ID"),
                subsectionWithPath("data.thumbnailPath").description(
                    "????????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ???????????? ???????????? api path"),
                fieldWithPath("data.displayOrder").description("????????? ????????? ????????? ?????? ?????? ???????????? ???????????? ??????"),
                subsectionWithPath("data.staticWriteContents[]").description(
                    "????????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ?????? ????????? ????????? ?????????")
            )));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????(????????? ???????????? Null ??? ??????)")
  public void modifySubtitleByIdSuccess_NullThumbnail() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    params.add("subtitle", "????????? ?????? ?????????");
    params.add("staticWriteTitleId", "1");
    params.add("displayOrder", "7");
    params.add("ipAddress", "192.111.777");

    mockMvc.perform(
            multipart("/v1/admin/about/sub-titles/{id}", staticWriteSubtitleImageEntity.getId())
                .params(params)
                .header("Authorization", adminToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(request -> {
                  request.setMethod("PUT");
                  return request;
                }))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????(????????? ???????????? ????????? ?????? ?????????)")
  public void modifySubtitleByIdSuccess_DefaultThumbnail() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "????????? ?????? ?????????");
    params.add("staticWriteTitleId", "1");
    params.add("displayOrder", "7");
    params.add("ipAddress", "192.111.777");

    mockMvc.perform(multipart("/v1/admin/about/sub-titles/{id}",
            defaultStaticWirteSubtitleImageEntity.getId())
            .file(image)
            .params(params)
            .header("Authorization", adminToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????(???????????? ?????? ?????? ????????? ID)")
  public void modifySubtitleByIdFail_ID() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "????????? ?????? ?????????");
    params.add("staticWriteTitleId", "1");
    params.add("displayOrder", "7");
    params.add("ipAddress", "192.111.777");

    mockMvc.perform(multipart("/v1/admin/about/sub-titles/{id}", 1234)
            .file(image)
            .params(params)
            .header("Authorization", adminToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("???????????? ?????? ?????????????????? ?????????????????????."));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????")
  public void deleteSubTitleByIdSuccess() throws Exception {
    mockMvc.perform(
            delete("/v1/admin/about/sub-titles/{id}", staticWriteSubtitleImageEntity.getId())
                .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("adminStaticWriteSubtitleImage-delete",
            pathParameters(
                parameterWithName("id").description("??????????????? ?????? ????????? ?????? ?????? ???????????? ID")
            ),
            responseFields(
                fieldWithPath("success").description("??????: true +\n??????: false"),
                fieldWithPath("code").description("?????? ??? 0??? ??????"),
                fieldWithPath("msg").description("??????: ????????????????????? +\n??????: ?????? ????????? ??????"),
                fieldWithPath("data.id").description("????????? ????????? ????????? ?????? ?????? ???????????? ID"),
                fieldWithPath("data.subtitle").description("????????? ????????? ????????? ?????? ?????? ???????????? ?????????"),
                fieldWithPath("data.staticWriteTitleId").description(
                    "????????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ?????? ???????????? ID"),
                subsectionWithPath("data.thumbnailPath").description(
                    "????????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ???????????? ???????????? api path"),
                fieldWithPath("data.displayOrder").description("????????? ????????? ????????? ?????? ?????? ???????????? ???????????? ??????"),
                subsectionWithPath("data.staticWriteContents[]").description(
                    "????????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ?????? ????????? ????????? ?????????")
            )));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????(????????? ???????????? ????????? ??????)")
  public void deleteSubtitleByIdSuccess_DefaultThumbnail() throws Exception {
    mockMvc.perform(
            delete("/v1/admin/about/sub-titles/{id}",
                defaultStaticWirteSubtitleImageEntity.getId())
                .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("????????? ?????? ?????? ????????? ?????? - ??????(???????????? ?????? ?????? ????????? ID)")
  public void deleteSubTitleByIdFail_Id() throws Exception {
    mockMvc.perform(
            delete("/v1/admin/about/sub-titles/{id}", 1234)
                .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false));
  }

}
