package keeper.project.homepage.controller.sign;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Log4j2
public class SignInControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private WebApplicationContext ctx;

  @Autowired
  private MessageSource messageSource;

  private final String loginId = "hyeonmomo";
  private final String emailAddress = "gusah@naver.com";
  private final String password = "keeper";
  private final String realName = "JeongHyeonMo";
  private final String nickName = "HyeonMoJeong";
  private final String birthday = "19980101";
  private final String studentId = "201724579";

  @BeforeEach
  public void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {
    // mockMvc의 한글 사용을 위한 코드
    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
        .apply(springSecurity()).apply(
            documentationConfiguration(restDocumentation).operationPreprocessors()
                .withRequestDefaults(modifyUris().host("test.com").removePort(), prettyPrint())
                .withResponseDefaults(prettyPrint())).build();

    SimpleDateFormat stringToDate = new SimpleDateFormat("yyyymmdd");
    Date birthdayDate = stringToDate.parse(birthday);

    memberRepository.save(MemberEntity.builder().loginId(loginId).emailAddress(emailAddress)
        .password(passwordEncoder.encode(password)).realName(realName).nickName(nickName)
        .birthday(birthdayDate).studentId(studentId)
        .roles(new ArrayList<String>(List.of("ROLE_USER"))).build());
  }

  @Test
  @DisplayName("로그인 성공")
  public void signIn() throws Exception {
    String content = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
        + "    \"password\": \"" + password + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andDo(document("sign-in",
            requestFields(
                fieldWithPath("loginId").description("로그인 아이디"),
                fieldWithPath("password").description("로그인 비밀번호")),
            responseFields(
                fieldWithPath("success").description("로그인 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description("로그인 실패 시 -1001 코드를 보냅니다."),
                fieldWithPath("msg").description("상태 메시지를 보냅니다."),
                fieldWithPath("data").description("로그인 성공 시 JWT 토큰을 담아서 보냅니다.").optional())));
  }

  @Test
  @DisplayName("로그인 실패")
  public void signInFail() throws Exception {
    String signInFailedCode = messageSource.getMessage("SigninFailed.code", null,
        LocaleContextHolder.getLocale());
    String content = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
        + "    \"password\": \"" + password + "1" + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(signInFailedCode))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("비밀번호 변경 성공")
  @Transactional
  public void passwordChangeSuccess() throws Exception {
    /* ==== 로그인 후 토큰 생성 Start ==== */
    String loginContent = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
        + "    \"password\": \"" + password + "\"\n"
        + "}";
    MvcResult result = mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(loginContent))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    String resultString = result.getResponse().getContentAsString();
    JacksonJsonParser jsonParser = new JacksonJsonParser();
    String userToken = jsonParser.parseMap(resultString).get("data").toString();
    /* ==== 로그인 후 토큰 생성 End ==== */

    MemberEntity PrevMemberEntity = memberRepository.findByLoginId(loginId)
        .orElseThrow(CustomMemberNotFoundException::new);
    String prevHashedPassword = PrevMemberEntity.getPassword();

    String newPassword = password + "1";
    String passwordChangeContent = "{\n"
        + "    \"password\": \"" + newPassword + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signin/change-password")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(passwordChangeContent))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andDo(document("change-password",
            requestFields(
                fieldWithPath("password").description("새로운 비밀번호")),
            responseFields(
                fieldWithPath("success").description("비밀번호 변경 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description("비밀번호 변경 실패 시 -1001 코드를 보냅니다."),
                fieldWithPath("msg").description("상태 메시지를 보냅니다.")
            )));

    MemberEntity memberEntity = memberRepository.findByLoginId(loginId)
        .orElseThrow(CustomMemberNotFoundException::new);
    String newHashedPassword = memberEntity.getPassword();
    // 비밀번호 변경 전 해쉬값과 변경 후 해쉬값이 다르면 테스트 성공.
    assertNotEquals(prevHashedPassword, newHashedPassword);
  }

  @Test
  @DisplayName("잘못된 토큰 비밀번호 변경 실패")
  public void passwordChangeFailed() throws Exception {
    /* ==== 로그인 후 토큰 생성 Start ==== */
    String loginContent = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
        + "    \"password\": \"" + password + "\"\n"
        + "}";
    MvcResult result = mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(loginContent))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    String resultString = result.getResponse().getContentAsString();
    JacksonJsonParser jsonParser = new JacksonJsonParser();
    String userToken = jsonParser.parseMap(resultString).get("data").toString();
    /* ==== 로그인 후 토큰 생성 End ==== */

    String signInFailedCode = messageSource.getMessage("SigninFailed.code", null,
        LocaleContextHolder.getLocale());

    String newPassword = password + "1";
    String passwordChangeContent = "{\n"
        + "    \"password\": \"" + newPassword + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signin/change-password")
            .header("Authorization", userToken + "1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(passwordChangeContent))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(signInFailedCode))
        .andExpect(jsonPath("$.msg").exists());
  }
}