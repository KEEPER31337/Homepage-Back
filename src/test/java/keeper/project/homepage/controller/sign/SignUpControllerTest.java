package keeper.project.homepage.controller.sign;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRepository;
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
public class SignUpControllerTest {

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

  final private long epochTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

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
            .roles(new ArrayList<String>(List.of("ROLE_USER")))
            .build());
  }

//  @Test
//  @DisplayName("회원가입 성공 시")
//  public void signUp() throws Exception {
//    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//    params.add("loginId", loginId + epochTime);
//    params.add("emailAddress", emailAddress + epochTime);
//    params.add("password", password);
//    params.add("realName", realName);
//    params.add("nickName", nickName);
//    params.add("birthday", birthday);
//    params.add("studentId", studentId + epochTime);
//    mockMvc.perform(post("/v1/signup").params(params))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.success").value(true))
//        .andExpect(jsonPath("$.code").value(0))
//        .andExpect(jsonPath("$.msg").exists())
//        .andDo(document("sign-up",
//            requestParameters(
//                parameterWithName("loginId").description("로그인 아이디"),
//                parameterWithName("emailAddress").description("이메일 주소"),
//                parameterWithName("password").description("로그인 비밀번호"),
//                parameterWithName("realName").description("실명"),
//                parameterWithName("nickName").description("닉네임"),
//                parameterWithName("birthday").description("생일").optional(),
//                parameterWithName("studentId").description("학번")
//            ),
//            responseFields(
//                fieldWithPath("success").description("회원가입 성공 시 true, 실패 시 false 값을 보냅니다."),
//                fieldWithPath("code").description("회원가입 성공 시 0, 실패 시 -9999 코드를 보냅니다."),
//                fieldWithPath("msg").description("회원가입이 실패하는 경우는 잘못된 접근 뿐임으로 알 수 없는 오류를 발생시킵니다.")
//            )));
//  }

  @Test
  @DisplayName("회원가입 실패 시")
  public void signUpFailed() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("loginId", loginId);
    params.add("emailAddress", emailAddress);
    params.add("password", password);
    params.add("realName", realName);
    params.add("nickName", nickName);
    params.add("birthday", birthday);
    params.add("studentId", studentId);
    mockMvc.perform(post("/v1/signup").params(params))
        .andDo(print())
        .andExpect(status().is5xxServerError());
  }

  @Test
  @DisplayName("아이디 중복 검사 - 중복이 존재 할 때")
  public void checkLoginIdDuplication() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("loginId", loginId);
    mockMvc.perform(get("/v1/signup/checkloginidduplication").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(true))
        .andDo(document("sign-up-check-loginid-duplication",
            requestParameters(
                parameterWithName("loginId").description("로그인 아이디")
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data").description("중복이면 true, 중복이 아니면 false")
            )));
  }

  @Test
  @DisplayName("아이디 중복 검사 - 중복이 존재 하지 않을 때")
  public void checkLoginIdNoDuplication() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("loginId", loginId + epochTime);
    mockMvc.perform(get("/v1/signup/checkloginidduplication").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(false));
  }

  @Test
  @DisplayName("이메일 중복 검사 - 중복이 존재 할 때")
  public void checkEmailAddressDuplication() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("emailAddress", emailAddress);
    mockMvc.perform(get("/v1/signup/checkemailaddressduplication").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(true))
        .andExpect(jsonPath("$.data").value(true))
        .andDo(document("sign-up-check-email-duplication",
            requestParameters(
                parameterWithName("emailAddress").description("이메일 주소")
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data").description("중복이면 true, 중복이 아니면 false")
            )));
  }

  @Test
  @DisplayName("이메일 중복 검사 - 중복이 존재 하지 않을 때")
  public void checkEmailAddressNoDuplication() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("emailAddress", emailAddress + epochTime);
    mockMvc.perform(get("/v1/signup/checkemailaddressduplication").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(false));
  }

  @Test
  @DisplayName("학번 중복 검사 - 중복이 존재 할 때")
  public void checkStudentIdDuplication() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("studentId", studentId);
    mockMvc.perform(get("/v1/signup/checkstudentidduplication").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(true))
        .andDo(document("sign-up-check-studentid-duplication",
            requestParameters(
                parameterWithName("studentId").description("학번")
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data").description("중복이면 true, 중복이 아니면 false")
            )));
  }

  @Test
  @DisplayName("학번 중복 검사 - 중복이 존재 하지 않을 때")
  public void checkStudentIdNoDuplication() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("studentId", studentId + epochTime);
    mockMvc.perform(get("/v1/signup/checkstudentidduplication").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(false));
  }
}