package keeper.project.homepage.admin.controller.about;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class AdminStaticWriteTitleControllerTest extends AdminStaticWriteTestHelper {

  private MemberEntity member;
  private String memberToken;
  private MemberEntity adminMember;
  private String adminToken;

  private StaticWriteTitleEntity staticWriteTitle;

  @BeforeEach
  public void setUp() throws Exception {
    member = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    adminMember = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    memberToken = generateJWTToken(member.getLoginId(), memberPassword);
    adminToken = generateJWTToken(adminMember.getLoginId(), memberPassword);

    staticWriteTitle = generateTestTitle(1);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 수정 - 성공")
  public void updateTitleByIdSuccess() throws Exception {
    String content = "{\n"
        + "    \"title\": \"" + "수정된 타이틀" + "\",\n"
        + "    \"type\": \"" + "수정된 타입" + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/about/titles/{id}", staticWriteTitle.getId())
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
  public void updateTitleByIdFailById() throws Exception {
    String content = "{\n"
        + "    \"title\": \"" + "수정된 타이틀" + "\",\n"
        + "    \"type\": \"" + "수정된 타입" + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/about/titles/{id}", -1)
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 타이틀에 접근하였습니다."));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 수정 - 실패(권한)")
  public void updateTitleByIdFailByAuth() throws Exception {
    String content = "{\n"
        + "    \"title\": \"" + "수정된 타이틀" + "\",\n"
        + "    \"type\": \"" + "수정된 타입" + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/about/titles/{id}", -1)
            .header("Authorization", memberToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("접근이 거부되었습니다."));
  }

}
