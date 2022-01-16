package keeper.project.homepage.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignControllerTest {

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
  public void setUp() throws Exception {
    // mockMvc의 한글 사용을 위한 코드
    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
        .alwaysDo(print())
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
        .andExpect(jsonPath("$.data").exists());
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
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("회원가입 성공 시")
  public void signUp() throws Exception {
    long epochTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("loginId", loginId + epochTime);
    params.add("emailAddress", emailAddress + epochTime);
    params.add("password", password);
    params.add("realName", realName);
    params.add("nickName", nickName);
    params.add("birthday", birthday);
    params.add("studentId", studentId + epochTime);
    mockMvc.perform(post("/v1/signup").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("아이디 중복 검사 - 중복 존재 : data=true / 존재 X : data=false")
  public void checkLoginIdDuplication() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("loginId", loginId);
    mockMvc.perform(get("/v1/signup/checkloginidduplication").params(params))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("이메일 중복 검사 - 중복 존재 : data=true / 존재 X : data=false")
  public void checkEmailAddressDuplication() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("emailAddress", emailAddress);
    mockMvc.perform(get("/v1/signup/checkemailaddressduplication").params(params))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("학번 중복 검사 - 중복 존재 : data=true / 존재 X : data=false")
  public void checkStudentIdDuplication() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("studentId", studentId);
    mockMvc.perform(get("/v1/signup/checkstudentidduplication").params(params))
        .andExpect(status().isOk())
        .andDo(print());
  }
}