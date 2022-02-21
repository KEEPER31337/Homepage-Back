package keeper.project.homepage.admin.controller.posting;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class AdminCategoryControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";
  final private Integer point = 200;

  private MemberEntity member, admin;
  private String memberToken, adminToken;

  private MemberEntity generateTestMember(String role) {
    final long epochTime = System.nanoTime();
    MemberJobEntity memberAdminJobEntity = memberJobRepository.findByName(role).get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberAdminJobEntity)
        .build();
    MemberEntity memberEntity = MemberEntity.builder()
        .loginId(loginId + epochTime)
        .password(passwordEncoder.encode(password))
        .realName(realName + epochTime)
        .nickName(nickName + epochTime)
        .emailAddress(emailAddress + epochTime)
        .studentId(studentId + epochTime)
        .point(point)
        .generation(0F)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);
    return memberEntity;
  }

  private String generateTestMemberJWT(MemberEntity member) throws Exception {

    String content = "{\n"
        + "    \"loginId\": \"" + member.getLoginId() + "\",\n"
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
    return sign.getData().getToken();
  }

  @BeforeEach
  public void setUp() throws Exception {
    member = generateTestMember("ROLE_회원");
    admin = generateTestMember("ROLE_회장");
    memberToken = generateTestMemberJWT(member);
    adminToken = generateTestMemberJWT(admin);
  }

  @Test
  @DisplayName("카테고리 생성 - 성공")
  public void createCategorySuccess() throws Exception {
    String content = "{\n"
        + "    \"name\": \"" + "카테고리 생성 테스트" + "\",\n"
        + "    \"parentId\": \"" + null + "\",\n"
        + "    \"href\": \"" + "board" + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/admin/category/create")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-create",
            requestFields(
                fieldWithPath("name").description("생성하고자 하는 카테고리 이름"),
                fieldWithPath("parentId").description("생성하고자 하는 카테고리의 부모 카테고리 값(존재하지 않으면 null)"),
                fieldWithPath("href").description("생성하고자 하는 카테고리의 참조값")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("생성에 성공한 카테고리의 ID"),
                fieldWithPath("data.name").description("생성에 성공한 카테고리의 이름"),
                fieldWithPath("data.href").description("생성에 성공한 카테고리의 참조값")
            )));
  }

  @Test
  @DisplayName("카테고리 생성 - 실패(권한 에러)")
  public void createCategoryFail() throws Exception {
    String content = "{\n"
        + "    \"name\": \"" + "카테고리 생성 테스트" + "\",\n"
        + "    \"parentId\": \"" + null + "\",\n"
        + "    \"href\": \"" + "board" + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/admin/category/create")
            .header("Authorization", memberToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
  }

  @Test
  @DisplayName("카테고리 삭제 - 성공")
  public void deleteCategoryByIdSuccess() throws Exception {
    mockMvc.perform(delete("/v1/admin/category/delete/{id}", 5125)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-delete",
            pathParameters(
                parameterWithName("id").description("삭제하고자 하는 카테고리의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("삭제에 성공한 카테고리의 ID"),
                fieldWithPath("data.name").description("삭제에 성공한 카테고리의 이름"),
                fieldWithPath("data.href").description("삭제에 성공한 카테고리의 참조값")
            )));
  }

  @Test
  @DisplayName("카테고리 삭제 - 실패(권한 에러)")
  public void deleteCategoryByIdFailByAuth() throws Exception {
    mockMvc.perform(delete("/v1/admin/category/delete/{id}", 5125)
            .header("Authorization", memberToken))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
  }

  @Test
  @DisplayName("카테고리 삭제 - 실패(존재하지 않는 ID)")
  public void deleteCategoryByIdFailByNullId() throws Exception {
    mockMvc.perform(delete("/v1/admin/category/delete/{id}", 111111)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("해당 카테고리가 존재하지 않습니다."));
  }

  @Test
  @DisplayName("카테고리 수정 - 성공")
  public void modifyCategoryByIdSuccess() throws Exception {
    String content = "{\n"
        + "    \"name\": \"" + "카테고리 수정 테스트" + "\",\n"
        + "    \"parentId\": \"" + 2 + "\",\n"
        + "    \"href\": \"" + "test" + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/category/modify/{id}", 5125)
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-modify",
            pathParameters(
                parameterWithName("id").description("수정하고자 하는 카테고리의 ID")
            ),
            requestFields(
                fieldWithPath("name").description("수정을 원하는 카테고리 이름(변하지 않을 시 기존값)"),
                fieldWithPath("parentId").description("수정을 원하는 카테고리의 부모 카테고리 값(변하지 않을 시 기존값)"),
                fieldWithPath("href").description("수정을 원하는 카테고리의 참조값(변하지 않을 시 기존값)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("수정에 성공한 카테고리의 ID"),
                fieldWithPath("data.name").description("수정에 성공한 카테고리의 이름"),
                fieldWithPath("data.href").description("수정에 성공한 카테고리의 참조값")
            )));
  }

  @Test
  @DisplayName("카테고리 수정 - 실패(권한 에러)")
  public void modifyCategoryByIdFailByAuth() throws Exception {
    String content = "{\n"
        + "    \"name\": \"" + "카테고리 수정 테스트" + "\",\n"
        + "    \"parentId\": \"" + 2 + "\",\n"
        + "    \"href\": \"" + "test" + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/category/modify/{id}", 5125)
            .header("Authorization", memberToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
  }

  @Test
  @DisplayName("카테고리 수정 - 실패(존재하지 않는 ID)")
  public void modifyCategoryByIdFailByNullId() throws Exception {
    String content = "{\n"
        + "    \"name\": \"" + "카테고리 수정 테스트" + "\",\n"
        + "    \"parentId\": \"" + 2 + "\",\n"
        + "    \"href\": \"" + "test" + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/admin/category/modify/{id}", 111111)
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("해당 카테고리가 존재하지 않습니다."));
  }

}
