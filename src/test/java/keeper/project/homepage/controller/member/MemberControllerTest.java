package keeper.project.homepage.controller.member;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.common.FileConversion;
import keeper.project.homepage.dto.EmailAuthDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRankRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import keeper.project.homepage.service.member.MemberService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
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

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRankRepository memberRankRepository;

  @Autowired
  private MemberTypeRepository memberTypeRepository;

  @Autowired
  private MemberHasMemberJobRepository memberHasMemberJobRepository;

  @Autowired
  private MemberJobRepository memberJobRepository;

  @BeforeAll
  public static void createFile() {
    final String keeperFilesDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files";
    final String thumbnailDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "thumbnail";
    final String imageFilePath = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "test_file.jpg";

    File keeperFilesDir = new File(keeperFilesDirectoryPath);
    File thumbnailDir = new File(thumbnailDirectoryPath);

    if (!keeperFilesDir.exists()) {
      keeperFilesDir.mkdir();
    }

    if (!thumbnailDir.exists()) {
      thumbnailDir.mkdir();
    }

    createImageForTest(imageFilePath);
  }

  private static void createImageForTest(String filePath) {
    FileConversion fileConversion = new FileConversion();
    fileConversion.makeSampleJPEGImage(filePath);
  }

  @BeforeEach
  public void setUp() throws Exception {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberEntity memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
//        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberEntity = memberRepository.save(memberEntity);
    MemberHasMemberJobEntity mj = memberHasMemberJobRepository.save(
        MemberHasMemberJobEntity.builder()
            .memberJobEntity(memberJobEntity)
            .memberEntity(memberEntity)
            .build());
    memberJobEntity.addMember(mj);
    memberJobRepository.save(memberJobEntity);
    memberEntity.addJob(mj);
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

  @Test
  @DisplayName("Admin 권한으로 회원 등급 변경하기")
  public void updateRank() throws Exception {
    String content = "{\n"
        + "\"memberLoginId\" : \"" + loginId + "\",\n"
        + "\"name\" : \"우수회원\"\n"
        + "}";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/rank")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.rank").value("우수회원"));

    MemberEntity member = memberRepository.findByLoginId(loginId).get();
    Assertions.assertTrue(
        memberRankRepository.findByName("우수회원").get().getMembers().contains(member));
  }

  @Test
  @DisplayName("Admin 권한으로 회원 유형 변경하기")
  public void updateType() throws Exception {
    String content = "{\n"
        + "\"memberLoginId\" : \"" + loginId + "\",\n"
        + "\"name\" : \"탈퇴\"\n"
        + "}";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/type")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.type").value("탈퇴"));

    MemberEntity member = memberRepository.findByLoginId(loginId).get();
    Assertions.assertTrue(
        memberTypeRepository.findByName("탈퇴").get().getMembers().contains(member));
  }

  @Test
  @DisplayName("Admin 권한으로 회원 직책 변경하기")
  public void updateJob() throws Exception {
//    memberJobRepository.findAll().forEach(memberJobEntity -> log.info(memberJobEntity.getName()));
    String content = "{\n"
        + "\"memberLoginId\" : \"" + loginId + "\",\n"
        + "\"names\" : [\"ROLE_사서\",\"ROLE_총무\"]\n"
        + "}";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/job")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.jobs[0]").value("ROLE_사서"))
        .andExpect(jsonPath("$.data.jobs[1]").value("ROLE_총무"));

    MemberEntity member = memberRepository.findByLoginId(loginId).get();
    MemberJobEntity job1 = memberJobRepository.findByName("ROLE_사서").get();
    MemberJobEntity job2 = memberJobRepository.findByName("ROLE_총무").get();
    Assertions.assertTrue(
        memberHasMemberJobRepository.findAllByMemberEntity_IdAndAndMemberJobEntity_Id(
            member.getId(), job1.getId()).isEmpty() == false);
    Assertions.assertTrue(
        memberHasMemberJobRepository.findAllByMemberEntity_IdAndAndMemberJobEntity_Id(
            member.getId(), job2.getId()).isEmpty() == false);
  }

  @Test
  @DisplayName("기본 권한으로 본인 실명, 닉네임 변경하기")
  public void updateNames() throws Exception {
    String updateContent = "{"
        + "\"realName\":\"변경한\","
        + "\"nickName\":\"변경한 닉네임\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/names")
            .header("Authorization", userToken)
            .content(updateContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.realName").value("변경한"))
        .andExpect(jsonPath("$.data.nickName").value("변경한 닉네임"));
  }

  @Test
  @DisplayName("기본 권한으로 이메일 변경하기")
  public void updateEmailAddress() throws Exception {
    String newEmail = "new@email.address";
    EmailAuthDto emailAuthDto = new EmailAuthDto(newEmail, "");
    EmailAuthDto emailAuthDtoForSend = memberService.generateEmailAuth(emailAuthDto);
    String content = "{\n"
        + "\"emailAddress\":\"" + newEmail + "\",\n"
        + "    \"authCode\": \"" + emailAuthDtoForSend.getAuthCode() + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/email")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.emailAddress").value(newEmail));
  }

  @Test
  @DisplayName("기본 권한으로 이메일 변경하기 - 이메일 중복으로 실패")
  public void updateEmailAddress_DuplFail() throws Exception {
    EmailAuthDto emailAuthDto = new EmailAuthDto(emailAddress, "");
    EmailAuthDto emailAuthDtoForSend = memberService.generateEmailAuth(emailAuthDto);
    String content = "{\n"
        + "\"emailAddress\":\"" + emailAddress + "\",\n"
        + "    \"authCode\": \"" + emailAuthDtoForSend.getAuthCode() + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/email")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-9999));
  }

  @Test
  @DisplayName("기본 권한으로 이메일 변경하기 - 이메일 인증 실패")
  public void updateEmailAddress_AuthFail() throws Exception {
    String newEmail = "new@email.address";
    EmailAuthDto emailAuthDto = new EmailAuthDto(newEmail, "");
    memberService.generateEmailAuth(emailAuthDto);
    String content = "{\n"
        + "\"emailAddress\":\"" + newEmail + "\",\n"
        + "    \"authCode\": \"" + "wrong auth code" + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/email")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1001));
  }

  @Test
  @DisplayName("기본 권한으로 본인의 썸네일 변경하기")
  public void updateThumbnails() throws Exception {
    MockMultipartFile image = new MockMultipartFile("image", "test_file.jpg", "image/jpg",
        new FileInputStream(new File(
            System.getProperty("user.dir") + File.separator + "keeper_files" + File.separator
                + "test_file.jpg")));
    mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/v1/member/update/thumbnail")
            .file(image)
            .param("ipAddress", "111.111.111.111")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header("Authorization", userToken)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-update-thumbnail",
            requestParameters(
                parameterWithName("ipAddress").description("회원의 IP 주소")
            ),
//            requestParts(
//                partWithName("image").description("썸네일 용 원본 이미지")
//            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description(""),
                fieldWithPath("msg").description(""),
                fieldWithPath("data.id").description(""),
                fieldWithPath("data.loginId").description(""),
                fieldWithPath("data.emailAddress").description(""),
                fieldWithPath("data.password").description(""),
                fieldWithPath("data.realName").description(""),
                fieldWithPath("data.nickName").description(""),
                fieldWithPath("data.authCode").description(""),
                fieldWithPath("data.birthday").description(""),
                fieldWithPath("data.studentId").description(""),
                fieldWithPath("data.registerDate").description(""),
                fieldWithPath("data.point").description(""),
                fieldWithPath("data.level").description("")
            )));
  }

  @Test
  @DisplayName("기본 권한으로 학번 변경하기")
  public void updateStudentId() throws Exception {
    String newStudentId = "123456789";
    String content = "{\n"
        + "\"studentId\":\"" + newStudentId + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/studentid")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.studentId").value(newStudentId));
  }

  @Test
  @DisplayName("기본 권한으로 학번 변경 실패 - 중복 학번")
  public void updateStudentId_DuplFail() throws Exception {
    String content = "{\n"
        + "\"studentId\":\"" + studentId + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/studentid")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-9999));
  }
}