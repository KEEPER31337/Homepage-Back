package keeper.project.homepage.controller.member;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MemberControllerTest extends ApiControllerTestSetUp {

  private String userToken;
  private String adminToken;

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";

  final private String adminLoginId = "hyeonmoAdmin";
  final private String adminPassword = "keeper2";
  final private String adminRealName = "JeongHyeonMo2";
  final private String adminNickName = "JeongHyeonMo2";
  final private String adminEmailAddress = "test2@k33p3r.com";
  final private String adminStudentId = "201724580";
  final private String adminPhoneNumber = "0100100101";

  @BeforeEach
  public void setUp() throws Exception {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberJobEntity)
        .build();
    MemberEntity memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);

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
    JacksonJsonParser jsonParser = new JacksonJsonParser();
    userToken = jsonParser.parseMap(resultString).get("data").toString();

    MemberJobEntity memberAdminJobEntity = memberJobRepository.findByName("ROLE_회장").get();
    MemberHasMemberJobEntity hasMemberAdminJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberAdminJobEntity)
        .build();
    MemberEntity memberAdmin = MemberEntity.builder()
        .loginId(adminLoginId)
        .password(passwordEncoder.encode(adminPassword))
        .realName(adminRealName)
        .nickName(adminNickName)
        .emailAddress(adminEmailAddress)
        .studentId(adminStudentId)
        .memberJobs(new ArrayList<>(List.of(hasMemberAdminJobEntity)))
        .build();
    memberRepository.save(memberAdmin);

    String adminContent = "{\n"
        + "    \"loginId\": \"" + adminLoginId + "\",\n"
        + "    \"password\": \"" + adminPassword + "\"\n"
        + "}";
    MvcResult adminResult = mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(adminContent))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    String adminResultString = adminResult.getResponse().getContentAsString();
    JacksonJsonParser jsonParser2 = new JacksonJsonParser();
    adminToken =
        jsonParser2.parseMap(adminResultString).get("data").toString();
  }

  @Test
  @DisplayName("잘못된 토큰으로 접근하기")
  public void invalidToken() throws Exception {
    mockMvc.perform(get("/v1/member")
            .header("Authorization", "XXXXXXXXXX"))
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1003));
  }

  @Test
  @DisplayName("토큰 없이 접근하기")
  public void noToken() throws Exception {
    mockMvc.perform(get("/v1/member"))
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false));
//        .andExpect(jsonPath("$.code").value(-1002));
  }

  @Test
  @DisplayName("옳은 토큰으로 접근하기")
  public void validToken() throws Exception {
    mockMvc.perform(get("/v1/member")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("권한이 없을 때")
  public void accessDenied() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/members")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1003));
  }

  @Test
  @DisplayName("권한이 있을 때")
  public void accessAccept() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/members")
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.list").exists());
  }

  @Test
  @DisplayName("ADMIN 권한으로 모든 멤버 열람하기")
  public void findAllMember() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/members")
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.list").exists());
  }

  @Test
  @DisplayName("기본 권한으로 본인 정보 열람하기")
  public void findMember() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/member")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists());
  }
}