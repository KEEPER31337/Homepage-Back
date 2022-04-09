package keeper.project.homepage.controller.sign;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.common.dto.sign.EmailAuthDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
public class SignUpControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String emailAddress = "test@k33p3r.com";
  final private String password = "keeperlove123";
  final private String realName = "가";
  final private String nickName = "afds나다라343";
  final private String birthday = "1998-01-01";
  final private String studentId = "201724579";

  final private String epochTime = String.valueOf(
      LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
  final private String sliceEpochTime = epochTime.substring(epochTime.length() - 4);

  @BeforeEach
  public void setUp() throws Exception {
    SimpleDateFormat stringToDate = new SimpleDateFormat("yyyymmdd");
    Date birthdayDate = stringToDate.parse(birthday);

    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberJobEntity)
        .build();
    MemberEntity memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName + "가")
        .nickName(nickName + "a")
        .emailAddress(emailAddress)
        .studentId(studentId)
        .generation(0F)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);
  }

  @Test
  @DisplayName("회원가입 성공 시")
  public void signUp() throws Exception {
    EmailAuthDto emailAuthDto = new EmailAuthDto(emailAddress + "1", "");
    EmailAuthDto emailAuthDtoForSend = signUpService.generateEmailAuth(emailAuthDto);
    String content = "{\n"
        + "    \"loginId\": \"" + loginId + "1" + "\",\n"
        + "    \"emailAddress\": \"" + emailAddress + "1" + "\",\n"
        + "    \"password\": \"" + password + "\",\n"
        + "    \"realName\": \"" + realName + "\",\n"
        + "    \"nickName\": \"" + nickName + "\",\n"
        + "    \"authCode\": \"" + emailAuthDtoForSend.getAuthCode() + "\",\n"
        + "    \"birthday\": \"" + birthday + "\",\n"
        + "    \"studentId\": \"" + studentId + "1" + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signup")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andDo(document("sign-up",
            requestFields(
                fieldWithPath("loginId").description("로그인 아이디"),
                fieldWithPath("emailAddress").description("이메일 주소"),
                fieldWithPath("password").description("로그인 비밀번호"),
                fieldWithPath("realName").description("실명"),
                fieldWithPath("nickName").description("닉네임"),
                fieldWithPath("authCode").description("인증코드"),
                fieldWithPath("birthday").description("생일 YYYY-MM-DD 형식").optional(),
                fieldWithPath("studentId").description("학번")
            ),
            responseFields(
                fieldWithPath("success").description("회원가입 성공 시 true, 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description("회원가입 성공 시 0, 실패 시 -9999 코드를 보냅니다."),
                fieldWithPath("msg").description("회원가입이 실패하는 경우는 잘못된 접근 뿐임으로 알 수 없는 오류를 발생시킵니다.")
            )));
  }

  @Test
  @DisplayName("회원가입 Unique Column 중복으로 실패 시")
  public void signUpFailed() throws Exception {
    EmailAuthDto emailAuthDto = new EmailAuthDto(emailAddress, "");
    EmailAuthDto emailAuthDtoForSend = signUpService.generateEmailAuth(emailAuthDto);
    String content = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
        + "    \"emailAddress\": \"" + emailAddress + "\",\n"
        + "    \"password\": \"" + password + "\",\n"
        + "    \"realName\": \"" + realName + "\",\n"
        + "    \"nickName\": \"" + nickName + "\",\n"
        + "    \"authCode\": \"" + emailAuthDtoForSend.getAuthCode() + "\",\n"
        + "    \"studentId\": \"" + studentId + "\",\n"
        + "    \"birthday\": \"" + birthday + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signup")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().is5xxServerError());
  }

  @Test
  @DisplayName("회원가입 이메일 인증코드 불일치로 실패 시")
  public void signUpAuthCodeMismatch() throws Exception {
    EmailAuthDto emailAuthDto = new EmailAuthDto(emailAddress + sliceEpochTime, "");
    EmailAuthDto emailAuthDtoForSend = signUpService.generateEmailAuth(emailAuthDto);
    String content = "{\n"
        + "    \"loginId\": \"" + loginId + sliceEpochTime + "\",\n"
        + "    \"emailAddress\": \"" + emailAddress + sliceEpochTime + "\",\n"
        + "    \"password\": \"" + password + "\",\n"
        + "    \"realName\": \"" + realName + "\",\n"
        + "    \"nickName\": \"" + nickName + "\",\n"
        + "    \"authCode\": \"" + emailAuthDtoForSend.getAuthCode() + sliceEpochTime + "\",\n"
        + "    \"birthday\": \"" + birthday + "\",\n"
        + "    \"studentId\": \"" + studentId + sliceEpochTime + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signup")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1001))
        .andExpect(jsonPath("$.msg").exists());
  }

  /* 해당 부분은 어떻게 처리해야 할 지 몰라서 일단 비워놨습니다.
   *
   *  @Test
   *  @DisplayName("회원가입 이메일 인증코드 만료로 실패 시")
   *  public void signUpAuthCodeExpire() throws Exception {
   *    EmailAuthDto emailAuthDto = new EmailAuthDto(emailAddress + epochTime, "");
   *    EmailAuthDto emailAuthDtoForSend = signUpService.generateEmailAuth(emailAuthDto);
   *    String content = "{\n"
   *        + "    \"loginId\": \"" + loginId + epochTime + "\",\n"
   *        + "    \"emailAddress\": \"" + emailAddress + epochTime + "\",\n"
   *        + "    \"password\": \"" + password + "\",\n"
   *        + "    \"realName\": \"" + realName + "\",\n"
   *        + "    \"nickName\": \"" + nickName + "\",\n"
   *        + "    \"authCode\": \"" + emailAuthDtoForSend.getAuthCode() + epochTime + "\",\n"
   *        + "    \"birthday\": \"" + birthday + "\",\n"
   *        + "    \"studentId\": \"" + studentId + epochTime + "\"\n"
   *        + "}";
   *    mockMvc.perform(post("/v1/signup")
   *            .contentType(MediaType.APPLICATION_JSON_VALUE)
   *            .content(content))
   *        .andDo(print())
   *        .andExpect(status().is5xxServerError())
   *        .andExpect(jsonPath("$.success").value(false))
   *        .andExpect(jsonPath("$.code").value(-1001))
   *        .andExpect(jsonPath("$.msg").exists());
   *  }
   */

  @Test
  @DisplayName("이메일 인증코드 생성")
  public void emailAuth() throws Exception {
    String content = "{\n"
        + "    \"emailAddress\": \"" + emailAddress + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signup/emailauth")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andDo(document("email-auth",
            requestFields(
                fieldWithPath("emailAddress").description("이메일 주소")
            ),
            responseFields(
                fieldWithPath("success").description("이메일 인증코드 생성 성공 시 true, 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description("이메일 인증코드 생성 성공 시 0, 실패 시 -9999 코드를 보냅니다."),
                fieldWithPath("msg").description(
                    "이메일 인증코드 생성이 실패하는 경우는 잘못된 접근 뿐임으로 알 수 없는 오류를 발생시킵니다.")
            )));
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
    params.add("loginId", loginId + sliceEpochTime);
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
    params.add("emailAddress", emailAddress + sliceEpochTime);
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
    params.add("studentId", studentId + sliceEpochTime);
    mockMvc.perform(get("/v1/signup/checkstudentidduplication").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(false));
  }
}