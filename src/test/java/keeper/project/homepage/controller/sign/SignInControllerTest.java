package keeper.project.homepage.controller.sign;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
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
public class SignInControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private WebApplicationContext ctx;

  final private String loginId = "hyeonmomo";
  final private String emailAddress = "gusah@naver.com";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "HyeonMoJeong";
  final private String birthday = "19980101";
  final private String studentId = "201724579";

  @BeforeEach
  public void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {
    // mockMvc의 한글 사용을 위한 코드
    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
        .apply(documentationConfiguration(restDocumentation)
            .operationPreprocessors()
            .withRequestDefaults(modifyUris().host("test.com").removePort(), prettyPrint())
            .withResponseDefaults(prettyPrint())
        )
        .build();

    SimpleDateFormat stringToDate = new SimpleDateFormat("yyyymmdd");
    Date birthdayDate = stringToDate.parse(birthday);

    memberRepository.save(
        MemberEntity.builder()
            .loginId(loginId)
            .emailAddress(emailAddress)
            .password(passwordEncoder.encode(password))
            .realName(realName)
            .nickName(nickName)
            .birthday(birthdayDate)
            .studentId(studentId)
            .roles(Collections.singletonList("ROLE_USER"))
            .build());
  }

  @Test
  @DisplayName("로그인 성공")
  public void signIn() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("loginId", loginId);
    params.add("password", password);
    mockMvc.perform(post("/v1/signin").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andExpect(status().isOk())
        .andDo(document("sign-in",
            requestParameters(
                parameterWithName("loginId").description("로그인 아이디"),
                parameterWithName("password").description("로그인 비밀번호")
            ),
            responseFields(
                fieldWithPath("success").description("로그인 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description("로그인 실패 시 -1001 코드를 보냅니다."),
                fieldWithPath("msg").description("상태 메시지를 보냅니다."),
                fieldWithPath("data").description("로그인 성공 시 JWT 토큰을 담아서 보냅니다.").optional()
            )));
  }

  @Test
  @DisplayName("로그인 실패")
  public void signInFail() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("loginId", loginId);
    params.add("password", password + "1");
    mockMvc.perform(post("/v1/signin").params(params))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1001))
        .andExpect(jsonPath("$.msg").exists())
    ;
  }
}