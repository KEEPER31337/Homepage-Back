package keeper.project.homepage.controller.member;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.common.FileConversion;
import keeper.project.homepage.dto.EmailAuthDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
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

  final private String ipAddress1 = "127.0.0.1";

  private MemberEntity memberEntity;
  private ThumbnailEntity thumbnailEntity;
  private FileEntity imageEntity;

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
    imageEntity = FileEntity.builder()
        .fileName("image.jpg")
        .filePath("keeper_files" + File.separator + "image.jpg")
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(imageEntity);

    thumbnailEntity = ThumbnailEntity.builder()
        .path("keeper_files" + File.separator + "thumbnail" + File.separator + "t_image.jpg")
        .file(imageEntity).build();
    thumbnailRepository.save(thumbnailEntity);

    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberTypeEntity memberTypeEntity = memberTypeRepository.findByName("정회원").get();
    MemberRankEntity memberRankEntity = memberRankRepository.findByName("일반회원").get();
    memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .memberType(memberTypeEntity)
        .memberRank(memberRankEntity)
        .thumbnail(thumbnailEntity)
//        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberEntity = memberRepository.save(memberEntity);
    memberTypeEntity.getMembers().add(memberEntity);
    memberRankEntity.getMembers().add(memberEntity);

    MemberHasMemberJobEntity mj = memberHasMemberJobRepository.save(
        MemberHasMemberJobEntity.builder()
            .memberJobEntity(memberJobEntity)
            .memberEntity(memberEntity)
            .build());
    memberJobEntity.getMembers().add(mj);
    memberJobRepository.save(memberJobEntity);
    memberEntity.getMemberJobs().add(mj);
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
        .memberType(memberTypeEntity)
        .memberRank(memberRankEntity)
        .thumbnail(thumbnailEntity)
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
  @DisplayName("팔로우하기")
  public void follow() throws Exception {
    String content = "{"
        + "\"followeeLoginId\" : \"" + adminLoginId + "\""
        + "}";
    mockMvc.perform(MockMvcRequestBuilders.post("/v1/member/follow")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-follow",
            requestFields(
                fieldWithPath("followeeLoginId").description("팔로우할 회원의 로그인 아이디")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("실패 시: -9999")
            )));

    MemberEntity memberEntity = memberRepository.findByLoginId(loginId).get();
    MemberEntity memberAdmin = memberRepository.findByLoginId(adminLoginId).get();
    List<FriendEntity> followeeList = memberEntity.getFollowee();
    FriendEntity followee = followeeList.get(followeeList.size() - 1);
    // friend entity에 followee와 follower가 잘 들어갔나요?
    assertTrue(followee.getFollowee().equals(memberAdmin));
    assertTrue(followee.getFollower().equals(memberEntity));
  }

  @Test
  @DisplayName("언팔로우하기")
  public void unfollow() throws Exception {
    String content = "{"
        + "\"followeeLoginId\" : \"" + adminLoginId + "\""
        + "}";
    // follow
    MemberEntity memberEntity = memberRepository.findByLoginId(loginId).get();
    MemberEntity memberAdmin = memberRepository.findByLoginId(adminLoginId).get();
    memberService.follow(memberEntity.getId(), adminLoginId);
    List<FriendEntity> followeeList = memberEntity.getFollowee();
    FriendEntity followee = followeeList.get(followeeList.size() - 1);

    // unfollow
    mockMvc.perform(MockMvcRequestBuilders.delete("/v1/member/unfollow")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-unfollow",
            requestFields(
                fieldWithPath("followeeLoginId").description("팔로우한 회원의 로그인 아이디")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("실패 시: -9999")
            )));

    assertTrue(friendRepository.findById(followee.getId()).isEmpty());
    Assertions.assertFalse(memberEntity.getFollowee().contains(followee));
    Assertions.assertFalse(memberAdmin.getFollower().contains(followee));
  }

  @Test
  @DisplayName("내가 팔로우한 사람 조회하기")
  public void showFollowee() throws Exception {
    // follow: member -> admin(followee)
    MemberEntity memberEntity = memberRepository.findByLoginId(loginId).get();
    MemberEntity memberAdmin = memberRepository.findByLoginId(adminLoginId).get();
    memberService.follow(memberEntity.getId(), adminLoginId);

    mockMvc.perform(get("/v1/member/followee")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list[0].nickName").value(adminNickName))
        .andDo(document("friend-show-followee",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("실패 시: -9999"),
                fieldWithPath("list[].emailAddress").description("이메일 주소"),
                fieldWithPath("list[].nickName").description("닉네임"),
                fieldWithPath("list[].birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("list[].registerDate").description("가입 날짜"),
                fieldWithPath("list[].point").description("포인트 점수"),
                fieldWithPath("list[].level").description("레벨")
            )));
  }


  @Test
  @DisplayName("Admin 권한으로 회원 등급 변경하기")
  public void updateRank() throws Exception {
    String content = "{\n"
        + "\"memberLoginId\" : \"" + loginId + "\",\n"
        + "\"name\" : \"우수회원\"\n"
        + "}";
    String docMsg = "실패 문구 종류: " + " +\n"
        + "* 변경할 등급을 입력해주세요." + " +\n"
        + "* xxx인 member rank가 존재하지 않습니다.";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/rank")
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
                fieldWithPath("name").description("변경할 등급명")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("실패 시: -9999"),
                fieldWithPath("msg").description(docMsg),
                fieldWithPath("data.emailAddress").description("이메일 주소"),
                fieldWithPath("data.nickName").description("닉네임"),
                fieldWithPath("data.birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("data.registerDate").description("가입 날짜"),
                fieldWithPath("data.point").description("포인트 점수"),
                fieldWithPath("data.level").description("레벨"),
                fieldWithPath("data.rank").description("회원 등급: [null/우수회원/일반회원]"),
                fieldWithPath("data.type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]"),
                fieldWithPath("data.jobs").description(
                    "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
            )));
    ;

    MemberEntity member = memberRepository.findByLoginId(loginId).get();
    assertTrue(
        memberRankRepository.findByName("우수회원").get().getMembers().contains(member));
  }

  @Test
  @DisplayName("Admin 권한으로 회원 유형 변경하기")
  public void updateType() throws Exception {
    String content = "{\n"
        + "\"memberLoginId\" : \"" + loginId + "\",\n"
        + "\"name\" : \"탈퇴\"\n"
        + "}";
    String docMsg = "실패 문구 종류: " + " +\n"
        + "* 변경할 유형을 입력해주세요." + " +\n"
        + "* xxx인 member type이 존재하지 않습니다.";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/type")
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
                fieldWithPath("name").description("변경할 유형")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("실패 시: -9999"),
                fieldWithPath("msg").description(docMsg),
                fieldWithPath("data.emailAddress").description("이메일 주소"),
                fieldWithPath("data.nickName").description("닉네임"),
                fieldWithPath("data.birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("data.registerDate").description("가입 날짜"),
                fieldWithPath("data.point").description("포인트 점수"),
                fieldWithPath("data.level").description("레벨"),
                fieldWithPath("data.rank").description("회원 등급: [null/우수회원/일반회원]"),
                fieldWithPath("data.type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]"),
                fieldWithPath("data.jobs").description(
                    "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
            )));

    MemberEntity member = memberRepository.findByLoginId(loginId).get();
    assertTrue(
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

    String docMsg = "실패 문구 종류: " + " +\n"
        + "* 존재하지 않는 회원입니다.";
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
        .andExpect(jsonPath("$.data.jobs[1]").value("ROLE_총무"))
        .andDo(document("member-update-job",
            requestFields(
                fieldWithPath("memberLoginId").description("변경할 회원의 로그인 아이디"),
                fieldWithPath("names").description("변경할 직책명 리스트")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("실패 시: -1000"),
                fieldWithPath("msg").description(docMsg),
                fieldWithPath("data.emailAddress").description("이메일 주소"),
                fieldWithPath("data.nickName").description("닉네임"),
                fieldWithPath("data.birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("data.registerDate").description("가입 날짜"),
                fieldWithPath("data.point").description("포인트 점수"),
                fieldWithPath("data.level").description("레벨"),
                fieldWithPath("data.rank").description("회원 등급: [null/우수회원/일반회원]"),
                fieldWithPath("data.type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]"),
                fieldWithPath("data.jobs").description(
                    "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
            )));

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
  @DisplayName("기본 권한으로 본인 실명, 닉네임 변경하기")
  public void updateNames() throws Exception {
    String updateContent = "{"
        + "\"realName\":\"Changed\","
        + "\"nickName\":\"Changed Nick\""
        + "}";

    String docMsg = "실패 문구 종류: " + " +\n"
        + "* 알 수 없는 오류가 발생하였습니다";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/names")
            .header("Authorization", userToken)
            .content(updateContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.nickName").value("Changed Nick"))
        .andDo(document("member-update-names",
            requestFields(
                fieldWithPath("realName").description("변경할 이름"),
                fieldWithPath("nickName").description("변경할 닉네임")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("실패 시: -9999"),
                fieldWithPath("msg").description(docMsg),
                fieldWithPath("data.emailAddress").description("이메일 주소"),
                fieldWithPath("data.nickName").description("닉네임"),
                fieldWithPath("data.birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("data.registerDate").description("가입 날짜"),
                fieldWithPath("data.point").description("포인트 점수"),
                fieldWithPath("data.level").description("레벨"),
                fieldWithPath("data.rank").description("회원 등급: [null/우수회원/일반회원]"),
                fieldWithPath("data.type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]"),
                fieldWithPath("data.jobs").description(
                    "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
            )));
    assertTrue(memberEntity.getRealName().equals("Changed"));
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

    String docMsg = "실패 문구 종류: " + " +\n"
        + "알 수 없는 오류가 발생하였습니다" + " +\n"
        + "이메일 인증 코드가 만료되었습니다." + " +\n"
        + "이메일 인증 코드가 일치하지 않습니다.";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/email")
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
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("실패 시: " + " +\n"
                    + "* 인증 실패: -1002" + " +\n"
                    + "* 그 외: -9999"),
                fieldWithPath("msg").description(docMsg),
                fieldWithPath("data.emailAddress").description("이메일 주소"),
                fieldWithPath("data.nickName").description("닉네임"),
                fieldWithPath("data.birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("data.registerDate").description("가입 날짜"),
                fieldWithPath("data.point").description("포인트 점수"),
                fieldWithPath("data.level").description("레벨"),
                fieldWithPath("data.authCode").description("인증 코드"),
                fieldWithPath("data.rank").description("회원 등급: [null/우수회원/일반회원]"),
                fieldWithPath("data.type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]"),
                fieldWithPath("data.jobs").description(
                    "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
            )));
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
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1002));
  }

  @Test
  @DisplayName("기본 권한으로 본인의 썸네일 변경하기")
  public void updateThumbnails() throws Exception {
    MockMultipartFile image = new MockMultipartFile("image", "test_file.jpg", "image/jpg",
        new FileInputStream(new File(
            System.getProperty("user.dir") + File.separator + "keeper_files" + File.separator
                + "test_file.jpg")));

    String docMsg = "실패 문구 종류 : " + " +\n"
        + "* 썸네일 용 이미지는 image 타입이어야 합니다." + " +\n"
        + "* 이미지 파일을 BufferedImage로 읽어들일 수 없습니다." + " +\n"
        + "* 이미지 파일을 읽는 것을 실패했습니다." + " +\n"
        + "* 썸네일 용 파일은 이미지 파일이어야 합니다." + " +\n"
        + "* 이미지 파일을 BufferedImage로 읽어들일 수 없습니다." + " +\n"
        + "* 이미지 파일을 읽는 것을 실패했습니다." + " +\n"
        + "* 썸네일 이미지용 후처리를 실패했습니다.";
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
                fieldWithPath("code").description("실패 시: -9999"),
                fieldWithPath("msg").description(docMsg),
                fieldWithPath("data.emailAddress").description("이메일 주소"),
                fieldWithPath("data.nickName").description("닉네임"),
                fieldWithPath("data.birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("data.registerDate").description("가입 날짜"),
                fieldWithPath("data.point").description("포인트 점수"),
                fieldWithPath("data.level").description("레벨"),
                fieldWithPath("data.rank").description("회원 등급: [null/우수회원/일반회원]"),
                fieldWithPath("data.type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]"),
                fieldWithPath("data.jobs").description(
                    "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
            )));
  }

  @Test
  @DisplayName("나를 팔로우한 사람 조회하기")
  public void showFollower() throws Exception {
    // follow: admin(follower) -> member
    MemberEntity memberEntity = memberRepository.findByLoginId(loginId).get();
    MemberEntity memberAdmin = memberRepository.findByLoginId(adminLoginId).get();
    memberService.follow(memberAdmin.getId(), loginId);

    mockMvc.perform(get("/v1/member/follower")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list[0].nickName").value(adminNickName))
        .andDo(document("friend-show-follower",
            responseFields(
                fieldWithPath("success").description(""),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description(""),
                fieldWithPath("list[].emailAddress").description("이메일 주소"),
                fieldWithPath("list[].nickName").description("닉네임"),
                fieldWithPath("list[].birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("list[].registerDate").description("가입 날짜"),
                fieldWithPath("list[].point").description("포인트 점수"),
                fieldWithPath("list[].level").description("레벨")
            )));
  }

  @Test
  @DisplayName("기본 권한으로 학번 변경하기")
  public void updateStudentId() throws Exception {
    String newStudentId = "123456789";
    String content = "{\n"
        + "\"studentId\":\"" + newStudentId + "\""
        + "}";

    String docMsg = "실패 문구 종류: " + " +\n"
        + "* 알 수 없는 오류가 발생하였습니다" + " +\n"
        + "* 이미 사용중인 학번입니다.";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/studentid")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
//        .andExpect(jsonPath("$.data.studentId").value(newStudentId))
        .andDo(document("member-update-studentid",
            requestFields(
                fieldWithPath("studentId").description("회원의 학번")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("실패 시 -9999"),
                fieldWithPath("msg").description(docMsg),
                fieldWithPath("data.emailAddress").description("이메일 주소"),
                fieldWithPath("data.nickName").description("닉네임"),
                fieldWithPath("data.birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("data.registerDate").description("가입 날짜"),
                fieldWithPath("data.point").description("포인트 점수"),
                fieldWithPath("data.level").description("레벨"),
                fieldWithPath("data.rank").description("회원 등급: [null/우수회원/일반회원]"),
                fieldWithPath("data.type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]"),
                fieldWithPath("data.jobs").description(
                    "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
            )));
    assertTrue(memberEntity.getStudentId().equals("123456789"));
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

  @Test
  @DisplayName("회원 권한으로 다른 회원 정보 조회 - 성공")
  public void getOtherMemberInfoSuccess() throws Exception {
    MemberEntity otherMember = memberRepository.findByLoginId("hyeonmoAdmin")
        .orElseThrow(CustomMemberNotFoundException::new);

    mockMvc.perform(get("/v1/member/other/info/{id}", otherMember.getId())
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }


  @Test
  @DisplayName("회원 권한으로 다른 회원 정보 조회 - 실패(존재하지 않는 회원)")
  public void getOtherMemberInfoFailByNullMember() throws Exception {
    MemberEntity otherMember = memberRepository.findByLoginId("hyeonmoAdmin")
        .orElseThrow(CustomMemberNotFoundException::new);

    mockMvc.perform(get("/v1/member/other/info/{id}", 3001)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof CustomMemberNotFoundException))
        .andExpect(jsonPath("$.success").value(false));
  }

}