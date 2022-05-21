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
    generalMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    adminMember = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
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
  @DisplayName("페이지 블럭 서브 타이틀 생성 - 성공")
  public void createSubTitleSuccess() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "테스트 서브 타이틀");
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
        .andDo(document("aboutSubtitle-create",
            requestParameters(
                parameterWithName("subtitle").description("생성하고자 하는 페이지 블럭 서브 타이틀의 부제목"),
                parameterWithName("staticWriteTitleId").description(
                    "생성하고자 하는 페이지 블럭 서브 타이틀과 연결되는 페이지 블럭 타이틀의 ID"),
                parameterWithName("displayOrder").description("생성하고자 하는 페이지 블럭 서브 타이틀이 보여지는 순서"),
                parameterWithName("ipAddress").description("IP 주소")
            ),
            requestParts(
                partWithName("thumbnail").description(
                    "썸네일 용 이미지 (form-data 에서 thumbnail= parameter 부분)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("생성에 성공한 페이지 블럭 서브 타이틀의 ID"),
                fieldWithPath("data.subtitle").description("생성에 성공한 페이지 블럭 서브 타이틀의 부제목"),
                fieldWithPath("data.staticWriteTitleId").description(
                    "생성에 성공한 페이지 블럭 서브 타이틀과 연결된 페이지 블럭 타이틀의 ID"),
                subsectionWithPath("data.thumbnailPath").description(
                    "생성에 성공한 페이지 블럭 서브 타이틀과 연결된 썸네일 이미지를 조회하는 api path"),
                fieldWithPath("data.displayOrder").description("생성에 성공한 페이지 블럭 서브 타이틀이 보여지는 순서"),
                subsectionWithPath("data.staticWriteContents[]").description(
                    "생성에 성공한 페이지 블럭 서브 타이틀과 연결된 페이지 블럭 컨텐츠 데이터 리스트")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 서브 타이틀 생성 - 성공(썸네일이 없는 경우)")
  public void createSubTitleSuccess_NullThumbnail() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    params.add("subtitle", "테스트 서브 타이틀");
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
  @DisplayName("페이지 블럭 서브 타이틀 생성 - 실패(권한이 부족한 경우)")
  public void createSubTitleFail_Auth() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "테스트 서브 타이틀");
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
  @DisplayName("페이지 블럭 서브 타이틀 생성 - 실패(존재하지 않는 타이틀)")
  public void createSubTitleFail_Title() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = usrDir + testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "테스트 서브 타이틀");
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
        .andExpect(jsonPath("$.msg").value("존재하지 않는 타이틀입니다."));
  }

  @Test
  @DisplayName("페이지 블럭 서브 타이틀 수정 - 성공")
  public void modifySubtitleByIdSuccess() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "수정된 서브 타이틀");
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
        .andDo(document("aboutSubtitle-update",
            pathParameters(
                parameterWithName("id").description("삭제하고자 하는 페이지 블럭 서브 타이틀의 ID")
            ),
            requestParameters(
                parameterWithName("subtitle").description(
                    "수정하고자 하는 페이지 블럭 서브 타이틀의 부제목(변하지 않을 시 기존값)"),
                parameterWithName("staticWriteTitleId").description(
                    "수정하고자 하는 페이지 블럭 서브 타이틀과 연결되는 페이지 블럭 타이틀의 ID(변하지 않을 시 기존값)"),
                parameterWithName("displayOrder").description(
                    "수정하고자 하는 페이지 블럭 서브 타이틀이 보여지는 순서(변하지 않을 시 기존값)"),
                parameterWithName("ipAddress").description("IP 주소(변하지 않을 시 기존값)")
            ),
            requestParts(
                partWithName("thumbnail").description(
                    "썸네일 용 이미지 (form-data 에서 thumbnail= parameter 부분) (변하지 않을 시 기존값)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("수정에 성공한 페이지 블럭 서브 타이틀의 ID"),
                fieldWithPath("data.subtitle").description("수정에 성공한 페이지 블럭 서브 타이틀의 부제목"),
                fieldWithPath("data.staticWriteTitleId").description(
                    "수정에 성공한 페이지 블럭 서브 타이틀과 연결된 페이지 블럭 타이틀의 ID"),
                subsectionWithPath("data.thumbnailPath").description(
                    "수정에 성공한 페이지 블럭 서브 타이틀과 연결된 썸네일 이미지를 조회하는 api path"),
                fieldWithPath("data.displayOrder").description("수정에 성공한 페이지 블럭 서브 타이틀이 보여지는 순서"),
                subsectionWithPath("data.staticWriteContents[]").description(
                    "수정에 성공한 페이지 블럭 서브 타이틀과 연결된 페이지 블럭 컨텐츠 데이터 리스트")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 서브 타이틀 수정 - 성공(새로운 썸네일이 Null 인 경우)")
  public void modifySubtitleByIdSuccess_NullThumbnail() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    params.add("subtitle", "수정된 서브 타이틀");
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
  @DisplayName("페이지 블럭 서브 타이틀 수정 - 성공(디폴트 이미지로 설정된 서브 타이틀)")
  public void modifySubtitleByIdSuccess_DefaultThumbnail() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "수정된 서브 타이틀");
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
  @DisplayName("페이지 블럭 서브 타이틀 수정 - 실패(존재하지 않는 서브 타이틀 ID)")
  public void modifySubtitleByIdFail_ID() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String testImagePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(testImagePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testImagePath)));

    params.add("subtitle", "수정된 서브 타이틀");
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
        .andExpect(jsonPath("$.msg").value("존재하지 않는 서브 타이틀입니다."));
  }

  @Test
  @DisplayName("페이지 블럭 서브 타이틀 삭제 - 성공")
  public void deleteSubTitleByIdSuccess() throws Exception {
    mockMvc.perform(
            delete("/v1/admin/about/sub-titles/{id}", staticWriteSubtitleImageEntity.getId())
                .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("aboutSubtitle-delete",
            pathParameters(
                parameterWithName("id").description("삭제하고자 하는 페이지 블럭 서브 타이틀의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("삭제에 성공한 페이지 블럭 서브 타이틀의 ID"),
                fieldWithPath("data.subtitle").description("삭제에 성공한 페이지 블럭 서브 타이틀의 부제목"),
                fieldWithPath("data.staticWriteTitleId").description(
                    "삭제에 성공한 페이지 블럭 서브 타이틀과 연결된 페이지 블럭 타이틀의 ID"),
                subsectionWithPath("data.thumbnailPath").description(
                    "삭제에 성공한 페이지 블럭 서브 타이틀과 연결된 썸네일 이미지를 조회하는 api path"),
                fieldWithPath("data.displayOrder").description("삭제에 성공한 페이지 블럭 서브 타이틀이 보여지는 순서"),
                subsectionWithPath("data.staticWriteContents[]").description(
                    "삭제에 성공한 페이지 블럭 서브 타이틀과 연결된 페이지 블럭 컨텐츠 데이터 리스트")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 서브 타이틀 삭제 - 성공(디폴트 이미지로 지정된 경우)")
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
  @DisplayName("페이지 블럭 서브 타이틀 삭제 - 실패(존재하지 않는 서브 타이틀 ID)")
  public void deleteSubTitleByIdFail_Id() throws Exception {
    mockMvc.perform(
            delete("/v1/admin/about/sub-titles/{id}", 1234)
                .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false));
  }

}
