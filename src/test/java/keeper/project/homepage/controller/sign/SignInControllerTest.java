package keeper.project.homepage.controller.sign;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.Date;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.sign.dto.SignInDto;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SignInControllerTest extends ApiControllerTestHelper {

  private final String loginId = "hyeonmomo";
  private final String emailAddress = "test@k33p3r.com";
  private final String password = "keeper";
  private final String realName = "JeongHyeonMo";
  private final String nickName = "HyeonMoJeong";
  private final String birthday = "19980101";
  private final String studentId = "201724579";

  private final String ipAddress = "127.0.0.1";

  @BeforeEach
  public void setUp() throws Exception {
    SimpleDateFormat stringToDate = new SimpleDateFormat("yyyymmdd");
    Date birthdayDate = stringToDate.parse(birthday);

    ThumbnailEntity thumbnailEntity = generateThumbnailEntity();
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberTypeEntity memberTypeEntity = memberTypeRepository.findByName("정회원").get();
    MemberRankEntity memberRankEntity = memberRankRepository.findByName("일반회원").get();
    MemberEntity memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .memberType(memberTypeEntity)
        .memberRank(memberRankEntity)
        .thumbnail(thumbnailEntity)
        .generation(0F)
        .build();
    memberEntity.addMemberJob(memberJobEntity);
    memberRepository.save(memberEntity);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
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
                fieldWithPath("data.token").description("로그인 성공 시 JWT 토큰을 담아서 보냅니다."),
                fieldWithPath("data.member.id").description("아이디"),
                fieldWithPath("data.member.emailAddress").description("이메일 주소"),
                fieldWithPath("data.member.nickName").description("닉네임"),
                fieldWithPath("data.member.birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("data.member.registerDate").description("가입 날짜"),
                fieldWithPath("data.member.point").description("포인트 점수"),
                fieldWithPath("data.member.level").description("레벨"),
                fieldWithPath("data.member.merit").description("상점"),
                fieldWithPath("data.member.demerit").description("벌점"),
                fieldWithPath("data.member.generation").description("기수 (7월 이후는 N.5기)"),
                fieldWithPath("data.member.rank").description("회원 등급: [null/우수회원/일반회원]").optional(),
                fieldWithPath("data.member.type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]")
                    .optional(),
                fieldWithPath("data.member.jobs").description(
                        "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
                    .optional(),
                fieldWithPath("data.member.thumbnailPath").description("썸네일 Url").optional()
            )));
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
  @DisplayName("아이디 찾기 - 이메일로 유저의 아이디를 전송")
  public void findId() throws Exception {
    String signInFailedCode = messageSource.getMessage("SigninFailed.code", null,
        LocaleContextHolder.getLocale());

    String content = "{\n"
        + "    \"emailAddress\": \"" + emailAddress + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signin/find-id")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andDo(document("find-id",
            requestFields(
                fieldWithPath("emailAddress").description("이메일 주소")
            ),
            responseFields(
                fieldWithPath("success").description(
                    "아이디 찾기 성공 시 true, 존재하지 않는 email일 경우 false 값을 보냅니다."),
                fieldWithPath("code").description(
                    "아이디 찾기 성공 시 0, 존재하지 않는 email일 경우 -1001 코드를 보냅니다."),
                fieldWithPath("msg").description(
                    "존재하지 않는 email일 경우 " + signInFailedCode)
            )));
  }

  @Test
  @DisplayName("비밀번호 찾기 - 이메일로 유저의 임시 비밀번호를 전송")
  public void findPassword() throws Exception {
    String content = "{\n"
        + "    \"emailAddress\": \"" + emailAddress + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/signin/find-password")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andDo(document("find-password",
            requestFields(
                fieldWithPath("emailAddress").description("이메일 주소")
            ),
            responseFields(
                fieldWithPath("success").description(
                    "비밀번호 찾기 성공 시 true, 존재하지 않는 email일 경우 false 값을 보냅니다."),
                fieldWithPath("code").description(
                    "비밀번호 찾기 성공 시 0, 존재하지 않는 email일 경우 -1001 코드를 보냅니다."),
                fieldWithPath("msg").description(
                    "존재하지 않는 email일 경우 " + "\"해당 이메일을 가진 유저가 존재하지 않습니다\"" + " 메시지를 반환합니다.")
            )));
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
    ObjectMapper mapper = new ObjectMapper();
    SingleResult<SignInDto> sign = mapper.readValue(resultString, new TypeReference<>() {
    });
    String userToken = sign.getData().getToken();
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
    ObjectMapper mapper = new ObjectMapper();
    SingleResult<SignInDto> sign = mapper.readValue(resultString, new TypeReference<>() {
    });
    String userToken = sign.getData().getToken();
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