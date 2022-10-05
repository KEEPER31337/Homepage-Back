package keeper.project.homepage.member.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.sign.dto.EmailAuthDto;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class MemberUpdateControllerTest extends ApiControllerTestHelper {

  private String userToken;
  private String adminToken;

  private MemberEntity memberEntity;

  @BeforeEach
  public void setUp() throws Exception {
    memberEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(memberEntity);
    MemberEntity memberAdmin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원,
        MemberRankName.일반회원);
    adminToken = generateJWTToken(memberAdmin);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("Admin 권한으로 회원 기수 변경하기")
  public void updateGeneration() throws Exception {
    String content = "{"
        + "\"memberLoginId\" : \"" + memberEntity.getLoginId() + "\","
        + "\"generation\" : \"" + 7.5 + "\""
        + "}";
    String docMsg = "변경할 기수를 입력하지 않았다면 실패합니다.";
    String docCode =
        "입력 데이터가 비어있는 경우: " + exceptionUtil.getMessage("memberEmptyField.code") + " +\n"
            + "존재하지 않는 회원인 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/admin/members/generation")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.generation").value(7.5))
        .andDo(document("member-update-generation",
            requestFields(
                fieldWithPath("memberLoginId").description("변경할 회원의 로그인 아이디"),
                fieldWithPath("generation").description("변경할 회원 기수")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", docCode, docMsg)
            )
        ));
  }

  @Test
  @DisplayName("Admin 권한으로 회원 등급 변경하기")
  public void updateRank() throws Exception {
    String loginId = memberEntity.getLoginId();
    String content = "{\n"
        + "\"memberLoginId\" : \"" + loginId + "\",\n"
        + "\"name\" : \"우수회원\"\n"
        + "}";
    String docMsg = "변경할 rank의 입력 데이터가 비어있거나, 입력한 rank가 존재하지 않는다면 실패합니다.";
    String docCode =
        "입력 데이터가 비어있는 경우: " + exceptionUtil.getMessage("memberEmptyField.code") + " +\n"
            + "존재하지 않는 회원인 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "입력한 rank가 존재하지 않는 경우: " + exceptionUtil.getMessage("memberInfoNotFound.code")
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/admin/members/rank")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.rank").value("우수회원"))
        .andDo(document("member-update-rank",
            requestFields(
                fieldWithPath("memberLoginId").description("변경할 회원의 로그인 아이디"),
                fieldWithPath("name").description("변경할 회원 등급명")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", docCode, docMsg)
            )
        ));

    MemberEntity member = memberRepository.findByLoginId(loginId).get();
    assertTrue(
        memberRankRepository.findByName("우수회원").get().getMembers().contains(member));
  }

  @Test
  @DisplayName("Admin 권한으로 회원 유형 변경하기")
  public void updateType() throws Exception {
    String loginId = memberEntity.getLoginId();
    String content = "{\n"
        + "\"memberLoginId\" : \"" + loginId + "\",\n"
        + "\"name\" : \"탈퇴\"\n"
        + "}";
    String docMsg = "변경할 type의 입력 데이터가 비어있거나, 입력한 type이 존재하지 않는다면 실패합니다.";
    String docCode =
        "입력 데이터가 비어있는 경우: " + exceptionUtil.getMessage("memberEmptyField.code") + " +\n"
            + "존재하지 않는 회원인 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "입력한 type이 존재하지 않는 경우: " + exceptionUtil.getMessage("memberInfoNotFound.code")
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/admin/members/type")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.type").value("탈퇴"))
        .andDo(document("member-update-type",
            requestFields(
                fieldWithPath("memberLoginId").description("변경할 회원의 로그인 아이디"),
                fieldWithPath("name").description("변경할 회원 유형")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", docCode, docMsg)
            )
        ));

    MemberEntity member = memberRepository.findByLoginId(loginId).get();
    assertTrue(
        memberTypeRepository.findByName("탈퇴").get().getMembers().contains(member));
  }

  @Test
  @DisplayName("Admin 권한으로 회원 직책 변경하기")
  public void updateJob() throws Exception {
    String loginId = memberEntity.getLoginId();
    String content = "{\n"
        + "\"memberLoginId\" : \"" + loginId + "\",\n"
        + "\"names\" : [\"ROLE_사서\",\"ROLE_총무\"]\n"
        + "}";

    String docMsg = "변경할 job의 입력 데이터가 비어있거나, 입력한 job이 존재하지 않는다면 실패합니다.";
    String docCode =
        "입력 데이터가 비어있는 경우: " + exceptionUtil.getMessage("memberEmptyField.code") + " +\n"
            + "존재하지 않는 회원인 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "입력한 job이 존재하지 않는 경우: " + exceptionUtil.getMessage("memberInfoNotFound.code")
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/admin/members/job")
            .header("Authorization", adminToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.jobs[0]").value("ROLE_사서"))
        .andExpect(jsonPath("$.data.jobs[1]").value("ROLE_총무"))
        .andDo(document("member-update-job",
            requestFields(
                fieldWithPath("memberLoginId").description("변경할 회원의 로그인 아이디"),
                fieldWithPath("names").description("변경할 직책명 리스트")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", docCode, docMsg)
            )
        ));

    MemberEntity member = memberRepository.findByLoginId(loginId).get();
    MemberJobEntity job1 = memberJobRepository.findByName("ROLE_사서").get();
    MemberJobEntity job2 = memberJobRepository.findByName("ROLE_총무").get();
    assertTrue(
        memberHasMemberJobRepository.findAllByMemberEntity_IdAndAndMemberJobEntity_Id(
            member.getId(), job1.getId()).isEmpty() == false);
    assertTrue(
        memberHasMemberJobRepository.findAllByMemberEntity_IdAndAndMemberJobEntity_Id(
            member.getId(), job2.getId()).isEmpty() == false);
  }

  @Test
  @DisplayName("기본 권한으로 프로필 변경하기")
  public void updateProfile() throws Exception {
    String newStudentId = "53186487";
    String updateContent = "{"
        + "\"realName\":\"Changed\","
        + "\"nickName\":\"Changed Nick\","
        + "\"studentId\":\"" + newStudentId + "\""
        + "}";

    String docMsg = "프로필에는 이름, 닉네임, 학번 요소가 포함되어 있습니다. +\n"
        + "변경할 요소의 입력 데이터가 비어있거나, 입력한 학번이 중복된다면 실패합니다.";
    String docCode =
        "입력 데이터가 비어있는 경우: " + exceptionUtil.getMessage("memberEmptyField.code") + " +\n"
            + "존재하지 않는 회원인 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/members/profile")
            .header("Authorization", userToken)
            .content(updateContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.nickName").value("Changed Nick"))
        .andDo(document("member-update-profile",
            requestFields(
                fieldWithPath("realName").description("변경할 이름"),
                fieldWithPath("nickName").description("변경할 닉네임"),
                fieldWithPath("studentId").description("변경할 학번")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", docCode, docMsg)
            )
        ));
    assertTrue(memberEntity.getRealName().equals("Changed"));
    assertTrue(memberEntity.getStudentId().equals(newStudentId));
  }

  @Test
  @DisplayName("기본 권한으로 학번 변경 실패 - 중복 학번")
  public void updateStudentId_DuplFail() throws Exception {
    String studentId = memberEntity.getStudentId();
    String content = "{"
        + "\"realName\":\"Changed\","
        + "\"nickName\":\"Changed Nick\","
        + "\"studentId\":\"" + studentId + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/members/profile")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-22));
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

    String docMsg = "변경할 이메일 주소의 입력 데이터가 비어있는 경우, 이메일 주소가 중복되는 경우, "
        + "이메일 인증 코드가 만료된 경우, 이메일 인증 코드가 일치하지 않는 경우 실패합니다.";
    String docCode =
        "입력 데이터가 비어있는 경우: " + exceptionUtil.getMessage("memberEmptyField.code") + " +\n"
            + "존재하지 않는 회원인 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "이메일 주소가 중복되는 경우: " + exceptionUtil.getMessage("memberDuplicate.code") + " +\n"
            + "이메일 인증 코드가 민료되었거나 일치하지 않는 경우: " + exceptionUtil.getMessage(
            "entryPointException.code") + " +\n"
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/members/email")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.emailAddress").value(newEmail))
        .andDo(document("member-update-email",
            requestFields(
                fieldWithPath("emailAddress").description("이메일 주소"),
                fieldWithPath("authCode").description("이메일 인증 코드")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", docCode, docMsg,
                    fieldWithPath("data.authCode").description("이메일 인증 코드"))
            )
        ));
  }

  @Test
  @DisplayName("기본 권한으로 이메일 변경하기 - 이메일 중복으로 실패")
  public void updateEmailAddress_DuplFail() throws Exception {
    String emailAddress = memberEntity.getEmailAddress();
    EmailAuthDto emailAuthDto = new EmailAuthDto(emailAddress, "");
    EmailAuthDto emailAuthDtoForSend = memberService.generateEmailAuth(emailAuthDto);
    String content = "{\n"
        + "\"emailAddress\":\"" + emailAddress + "\",\n"
        + "    \"authCode\": \"" + emailAuthDtoForSend.getAuthCode() + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/members/email")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(exceptionUtil.getMessage("memberDuplicate.code")));
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
            .put("/v1/members/email")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(
            jsonPath("$.code").value(exceptionUtil.getMessage("entryPointException.code")));
  }

  @Test
  @DisplayName("기본 권한으로 본인의 썸네일 변경하기")
  public void updateThumbnails() throws Exception {
    String befFilePath = memberEntity.getThumbnailPath();
    String testFilePath = testFileRelDir + File.separator + "test.jpg";
    createFileForTest(usrDir + testFilePath);
    MockMultipartFile image = new MockMultipartFile("thumbnail", "test.jpg", "image/jpg",
        new FileInputStream(new File(testFilePath)));

    System.out.println("Error");
    System.out.println(memberEntity.getThumbnail().getPath());
    System.out.println(memberEntity.getThumbnailPath());
    String docMsg = "첨부 파일이 비어있을 경우, 기본 썸네일 이미지가 설정됩니다. +\n"
        + "다음과 같은 상황에 실패합니다. +\n"
        + "* 첨부한 파일이 이미지 파일이 아닌 경우" + " +\n"
        + "* 포맷은 이미지 파일이나, 내용이 정상적인 이미지 파일이 아닌 경우" + " +\n"
        + "* 서버 내부 문제로 이미지 파일을 읽는 것을 실패하는 경우" + " +\n";
    // @formatter:off
    String docCode =
        "기존 파일이 서버에 존재하지 않는 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "기존 파일을 삭제하는 데 실패한 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "새 파일을 저장하는 데 실패한 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "DB에 파일 레코드가 존재하지 않는 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "DB에 썸네일 레코드가 존재하지 않는 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "이미지 파일 형식이 잘못된 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "이미지 파일을 읽고 쓰는 것에 실패한 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    // @formatter:on
    mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/v1/members/thumbnail")
            .file(image)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .param("ipAddress", "111.111.111.111")
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
            requestParts(
                partWithName("thumbnail").description("썸네일 용 이미지 파일")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", docCode, docMsg)
            )
        ));
    Assertions.assertFalse(memberEntity.getThumbnail().getPath().equals(befFilePath));
    deleteTestThumbnailFile(memberEntity.getThumbnail());
  }

  @Test
  @DisplayName("관리자 권한으로 상점 변경하기")
  public void updateMerit() throws Exception {
    Integer merit = 10;
    String updateContent = "{"
        + "\"memberLoginId\":\"" + memberEntity.getLoginId() + "\","
        + "\"merit\":" + merit + "}";
    String docMsg = "변경할 상점을 입력하지 않았다면 실패합니다.";
    String docCode =
        "입력 데이터가 비어있는 경우: " + exceptionUtil.getMessage("memberEmptyField.code") + " +\n"
            + "존재하지 않는 회원인 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/admin/members/merit")
            .header("Authorization", adminToken)
            .content(updateContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.merit").value(merit))
        .andDo(document("member-update-merit",
            requestFields(
                fieldWithPath("memberLoginId").description("변경할 회원의 로그인 아이디"),
                fieldWithPath("merit").description("변경할 회원의 상점")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.SINGLE, "성공: true +\n실패: false",
                    docCode, docMsg)
            )
        ));
  }

  @Test
  @DisplayName("관리자 권한으로 벌점 변경하기")
  public void updateDemerit() throws Exception {
    Integer demerit = 10;
    String updateContent = "{"
        + "\"memberLoginId\":\"" + memberEntity.getLoginId() + "\","
        + "\"demerit\":" + demerit + "}";
    String docMsg = "변경할 벌점을 입력하지 않았다면 실패합니다.";
    String docCode =
        "입력 데이터가 비어있는 경우: " + exceptionUtil.getMessage("memberEmptyField.code") + " +\n"
            + "존재하지 않는 회원인 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
            + " +\n" + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/admin/members/demerit")
            .header("Authorization", adminToken)
            .content(updateContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.demerit").value(demerit))
        .andDo(document("member-update-demerit",
            requestFields(
                fieldWithPath("memberLoginId").description("변경할 회원의 로그인 아이디"),
                fieldWithPath("demerit").description("변경할 회원의 벌점")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.SINGLE, "성공: true +\n실패: false",
                    docCode, docMsg)
            )
        ));
  }

}