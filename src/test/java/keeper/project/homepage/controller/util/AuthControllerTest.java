package keeper.project.homepage.controller.util;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AuthControllerTest extends ApiControllerTestSetUp {

  private String userToken;

  final private String loginId = "hyeonmomo";
  final private String emailAddress = "test@k33p3r.com";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "HyeonMoJeong";
  final private String birthday = "1998-01-01";
  final private String studentId = "201724579";

  MemberEntity memberEntity;

  @BeforeEach
  public void setUp() throws Exception {

    memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .generation(0F)
        .build();
    memberRepository.save(memberEntity);

    MemberJobEntity memberJobEntity1 = memberJobRepository.findByName("ROLE_사서").get();
    MemberHasMemberJobEntity hasMemberJobEntity1 = MemberHasMemberJobEntity.builder()
        .memberEntity(memberEntity)
        .memberJobEntity(memberJobEntity1)
        .build();
    memberHasMemberJobRepository.save(hasMemberJobEntity1);
    MemberJobEntity memberJobEntity2 = memberJobRepository.findByName("ROLE_전산관리자").get();
    MemberHasMemberJobEntity hasMemberJobEntity2 = MemberHasMemberJobEntity.builder()
        .memberEntity(memberEntity)
        .memberJobEntity(memberJobEntity2)
        .build();
    memberHasMemberJobRepository.save(hasMemberJobEntity2);

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
  }

  @Test
  @DisplayName("Jwt 토큰을 이용한 권한 확인")
  public void checkRolesTest() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/auth")
            .header("Authorization", userToken));

    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.list[0]").value("ROLE_사서"))
        .andExpect(jsonPath("$.list[1]").value("ROLE_전산관리자"))
        .andDo(print())
        .andDo(document("get-auth",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("list[]").description("역할들이 담겨서 나갑니다.")
            )
        ));
  }
}