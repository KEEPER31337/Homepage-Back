package keeper.project.homepage.admin.controller.about;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteContentEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class AdminAboutTitleControllerTest extends ApiControllerTestHelper {

  private MemberEntity generalMember;
  private String generalToken;
  private MemberEntity adminMember;
  private String adminToken;

  private ThumbnailEntity thumbnailEntity;
  private FileEntity fileEntity;
  private StaticWriteTitleEntity staticWriteTitleEntity;
  private StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity;
  private StaticWriteContentEntity staticWriteContentEntity;

  private final String ipAddress1 = "127.0.0.1";
  private final String generalTestImage = "keeper_files" + File.separator + "image.jpg";
  private final String generalThumbnailImage =
      "keeper_files" + File.separator + "thumbnail" + File.separator + "t_image.jpg";

  @BeforeEach
  public void setUp() throws Exception {
    generalMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    adminMember = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    generalToken = generateJWTToken(generalMember.getLoginId(), memberPassword);
    adminToken = generateJWTToken(adminMember.getLoginId(), memberPassword);

    fileEntity = FileEntity.builder()
        .fileName(getFileName(generalTestImage))
        .filePath(generalTestImage)
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(fileEntity);

    thumbnailEntity = ThumbnailEntity.builder()
        .path(generalThumbnailImage)
        .file(fileEntity).build();
    thumbnailRepository.save(thumbnailEntity);

    staticWriteTitleEntity = generateTestTitle(1);
    staticWriteSubtitleImageEntity = generateTestSubtitle(1);
    staticWriteContentEntity = generateTestContent(1);

  }

  private String getFileName(String filePath) {
    File file = new File(filePath);
    return file.getName();
  }

  public StaticWriteTitleEntity generateTestTitle(Integer index) {
    StaticWriteTitleEntity staticWriteTitleEntity = StaticWriteTitleEntity.builder()
        .title("테스트 타이틀" + index)
        .type("테스트 타입" + index)
        .build();
    return staticWriteTitleRepository.save(staticWriteTitleEntity);
  }

  public StaticWriteSubtitleImageEntity generateTestSubtitle(Integer index) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = StaticWriteSubtitleImageEntity.builder()
        .subtitle("테스트 서브 타이틀" + index)
        .displayOrder(index)
        .staticWriteTitle(staticWriteTitleEntity)
        .thumbnail(thumbnailEntity)
        .build();
    return staticWriteSubtitleImageRepository.save(staticWriteSubtitleImageEntity);
  }

  public StaticWriteContentEntity generateTestContent(Integer index) {
    StaticWriteContentEntity staticWriteContentEntity = StaticWriteContentEntity.builder()
        .content("테스트 컨텐츠" + index)
        .displayOrder(index)
        .staticWriteSubtitleImage(staticWriteSubtitleImageEntity)
        .build();
    return staticWriteContentRepository.save(staticWriteContentEntity);
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 생성 - 성공")
  public void createTitleSuccess() throws Exception {
    String content = "{\n"
        + "    \"title\": \"" + "테스트 타이틀입니다." + "\",\n"
        + "    \"type\": \"" + "테스트 타입입니다." + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/admin/about/title/create")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("aboutTitle-create",
            requestFields(
                fieldWithPath("title").description("생성하고자 하는 페이지 블럭 타이틀의 제목"),
                fieldWithPath("type").description("생성하고자 하는 페이지 블럭 타이틀의 타입")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("생성에 성공한 페이지 블럭 타이틀의 ID"),
                fieldWithPath("data.title").description("생성에 성공한 페이지 블럭 타이틀의 제목"),
                fieldWithPath("data.type").description("생성에 성공한 페이지 블럭 타이틀의 타입"),
                subsectionWithPath("data.subtitleImageResults[]").description("생성에 성공한 페이지 블럭 타이틀과 연결된 페이지 블럭 서브 타이틀 데이터 리스트")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 생성 - 실패(권한 부족)")
  public void createTitleFailByAuth() throws Exception {
    String content = "{\n"
        + "    \"title\": \"" + "테스트 타이틀입니다." + "\",\n"
        + "    \"type\": \"" + "테스트 타입입니다." + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/admin/about/title/create")
            .header("Authorization", generalToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 생성 - 실패(유효하지 않은 토큰)")
  public void createTitleFailByAuth2() throws Exception {
    String content = "{\n"
        + "    \"title\": \"" + "테스트 타이틀입니다." + "\",\n"
        + "    \"type\": \"" + "테스트 타입입니다." + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/admin/about/title/create")
            .header("Authorization", 111111)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 수정 - 성공")
  public void modifyTitleByIdSuccess() throws Exception {
    String content = "{\n"
        + "    \"title\": \"" + "수정된 타이틀" + "\",\n"
        + "    \"type\": \"" + "수정된 타입" + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/about/title/modify/{id}", staticWriteTitleEntity.getId())
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("aboutTitle-modify",
            requestFields(
                fieldWithPath("title").description("수정하고자 하는 페이지 블럭 타이틀의 제목(변하지 않을 시 기존값)"),
                fieldWithPath("type").description("수정하고자 하는 페이지 블럭 타이틀의 타입(변하지 않을 시 기존값)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("수정에 성공한 페이지 블럭 타이틀의 ID"),
                fieldWithPath("data.title").description("수정에 성공한 페이지 블럭 타이틀의 제목"),
                fieldWithPath("data.type").description("수정에 성공한 페이지 블럭 타이틀의 타입"),
                subsectionWithPath("data.subtitleImageResults[]").description("수정에 성공한 페이지 블럭 타이틀과 연결된 페이지 블럭 서브 타이틀 데이터 리스트")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 수정 - 실패(존재하지 않는 ID)")
  public void modifyTitleByIdFailById() throws Exception {
    String content = "{\n"
        + "    \"title\": \"" + "수정된 타이틀" + "\",\n"
        + "    \"type\": \"" + "수정된 타입" + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/about/title/modify/{id}", 1234)
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 타이틀 ID 입니다."));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 삭제 - 성공")
  public void deleteTitleByIdSuccess() throws Exception {
    mockMvc.perform(delete("/v1/admin/about/title/delete/{id}", staticWriteTitleEntity.getId())
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("aboutTitle-delete",
            pathParameters(
                parameterWithName("id").description("삭제하고자 하는 페이지 블럭 타이틀의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("삭제에 성공한 페이지 블럭 타이틀의 ID"),
                fieldWithPath("data.title").description("삭제에 성공한 페이지 블럭 타이틀의 제목"),
                fieldWithPath("data.type").description("삭제에 성공한 페이지 블럭 타이틀의 타입"),
                subsectionWithPath("data.subtitleImageResults[]").description("삭제에 성공한 페이지 블럭 타이틀과 연결된 페이지 블럭 서브 타이틀 데이터 리스트")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 삭제 - 실패(존재하지 않는 ID)")
  public void deleteTitleByIdFailById() throws Exception {
    mockMvc.perform(delete("/v1/admin/about/title/delete/{id}", 1234)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 타이틀 ID 입니다."));
  }

}
