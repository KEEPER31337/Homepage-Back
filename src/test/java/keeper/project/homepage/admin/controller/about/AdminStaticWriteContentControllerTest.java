package keeper.project.homepage.admin.controller.about;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.entity.about.StaticWriteContentEntity;
import keeper.project.homepage.entity.about.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class AdminStaticWriteContentControllerTest extends AdminStaticWriteTestHelper {

  private MemberEntity generalMember;
  private String generalToken;
  private MemberEntity adminMember;
  private String adminToken;

  private StaticWriteTitleEntity staticWriteTitleEntity;
  private StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity;
  private StaticWriteContentEntity staticWriteContentEntity;


  @BeforeEach
  public void setUp() throws Exception {
    generalMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    adminMember = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    generalToken = generateJWTToken(generalMember.getLoginId(), memberPassword);
    adminToken = generateJWTToken(adminMember.getLoginId(), memberPassword);

    staticWriteTitleEntity = generateTestTitle(1);
    staticWriteSubtitleImageEntity = generateTestSubtitleImage(staticWriteTitleEntity, 1);
    staticWriteContentEntity = generateTestContent(staticWriteSubtitleImageEntity, 1);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("페이지 블럭 컨텐츠 생성 - 성공")
  public void createContent() throws Exception {
    String content = "{\n"
        + "    \"content\": \"" + "테스트 컨텐츠입니다." + "\",\n"
        + "    \"staticWriteSubtitleImageId\": \"" + staticWriteSubtitleImageEntity.getId()
        + "\",\n"
        + "    \"displayOrder\": \"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/admin/about/contents")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("aboutContent-create",
            requestFields(
                fieldWithPath("content").description("생성하고자 하는 페이지 블럭 컨텐츠의 내용"),
                fieldWithPath("staticWriteSubtitleImageId").description("생성하고자 하는 페이지 블럭 컨텐츠의 연결된 페이지 블럭 서브 타이틀의 ID"),
                fieldWithPath("displayOrder").description("생성하고자 하는 페이지 블럭 컨텐츠가 보여지는 순서")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("생성에 성공한 페이지 블럭 컨텐츠의 ID"),
                fieldWithPath("data.content").description("생성에 성공한 페이지 블럭 컨텐츠의 내용"),
                fieldWithPath("data.staticWriteSubtitleImageId").description("생성에 성공한 페이지 블럭 컨텐츠와 연결된 페이지 블럭 서브 타이틀의 ID"),
                fieldWithPath("data.displayOrder").description("생성에 성공한 페이지 블럭 컨텐츠가 보여지는 순서")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 컨텐츠 생성 - 실패(존재하지 않는 서브 타이틀 ID)")
  public void createContentFailBySubtitle() throws Exception {
    String content = "{\n"
        + "    \"content\": \"" + "테스트 컨텐츠입니다." + "\",\n"
        + "    \"staticWriteSubtitleImageId\": \"" + 1234 + "\",\n"
        + "    \"displayOrder\": \"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/admin/about/contents")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 서브 타이틀입니다."));
  }

  @Test
  @DisplayName("페이지 블럭 컨텐츠 수정 - 성공")
  public void modifyContentByIdSuccess() throws Exception {
    String content = "{\n"
        + "    \"content\": \"" + "테스트 컨텐츠입니다." + "\",\n"
        + "    \"staticWriteSubtitleImageId\": \"" + staticWriteSubtitleImageEntity.getId()
        + "\",\n"
        + "    \"displayOrder\": \"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/about/contents/{id}", staticWriteContentEntity.getId())
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("aboutContent-update",
            pathParameters(
              parameterWithName("id").description("수정하고자 하는 페이지 블럭 컨텐츠의 ID")
            ),
            requestFields(
                fieldWithPath("content").description("수정하고자 하는 페이지 블럭 컨텐츠의 내용(변하지 않을 시 기존값)"),
                fieldWithPath("staticWriteSubtitleImageId").description("수정하고자 하는 페이지 블럭 컨텐츠의 연결된 페이지 블럭 서브 타이틀의 ID(변하지 않을 시 기존값)"),
                fieldWithPath("displayOrder").description("수정하고자 하는 페이지 블럭 컨텐츠가 보여지는 순서(변하지 않을 시 기존값)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("수정에 성공한 페이지 블럭 컨텐츠의 ID"),
                fieldWithPath("data.content").description("수정에 성공한 페이지 블럭 컨텐츠의 내용"),
                fieldWithPath("data.staticWriteSubtitleImageId").description("수정에 성공한 페이지 블럭 컨텐츠와 연결된 페이지 블럭 서브 타이틀의 ID"),
                fieldWithPath("data.displayOrder").description("수정에 성공한 페이지 블럭 컨텐츠가 보여지는 순서")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 컨텐츠 수정 - 실패(존재하지 않는 서브 타이틀 ID)")
  public void modifyContentByIdFailBySubtitle() throws Exception {
    String content = "{\n"
        + "    \"content\": \"" + "테스트 컨텐츠입니다." + "\",\n"
        + "    \"staticWriteSubtitleImageId\": \"" + 1234 + "\",\n"
        + "    \"displayOrder\": \"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/about/contents/{id}", staticWriteContentEntity.getId())
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 서브 타이틀입니다."));
  }

  @Test
  @DisplayName("페이지 블럭 컨텐츠 수정 - 실패(존재하지 않는 컨텐츠 ID)")
  public void modifyContentByIdFailById() throws Exception {
    String content = "{\n"
        + "    \"content\": \"" + "테스트 컨텐츠입니다." + "\",\n"
        + "    \"staticWriteSubtitleImageId\": \"" + staticWriteSubtitleImageEntity.getId()
        + "\",\n"
        + "    \"displayOrder\": \"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/about/contents/{id}", 1234)
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 컨텐츠입니다."));
  }

}
