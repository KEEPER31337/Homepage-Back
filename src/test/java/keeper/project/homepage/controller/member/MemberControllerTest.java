package keeper.project.homepage.controller.member;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.common.FileConversion;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.exception.CustomTransferPointLackException;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
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
  final private int point = 100;

  final private String adminLoginId = "hyeonmoAdmin";
  final private String adminPassword = "keeper2";
  final private String adminRealName = "JeongHyeonMo2";
  final private String adminNickName = "JeongHyeonMo2";
  final private String adminEmailAddress = "test2@k33p3r.com";
  final private String adminStudentId = "201724580";
  final private String adminPhoneNumber = "0100100101";
  final private int adminPoint = 50;

  final private String ipAddress1 = "127.0.0.1";

  private MemberEntity memberEntity;
  private ThumbnailEntity thumbnailEntity;
  private FileEntity imageEntity;

  @BeforeAll
  public static void createFile() {
    final String keeperFilesDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files";
    final String thumbnailDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "thumbnail";
    final String befUpdateImage = keeperFilesDirectoryPath + File.separator + "bef.jpg";
    final String befUpdateThumbnail = thumbnailDirectoryPath + File.separator + "thumb_bef.jpg";
    final String aftUpdateImage = keeperFilesDirectoryPath + File.separator + "aft.jpg";

    File keeperFilesDir = new File(keeperFilesDirectoryPath);
    File thumbnailDir = new File(thumbnailDirectoryPath);

    if (!keeperFilesDir.exists()) {
      keeperFilesDir.mkdir();
    }

    if (!thumbnailDir.exists()) {
      thumbnailDir.mkdir();
    }

    createImageForTest(befUpdateImage);
    createImageForTest(befUpdateThumbnail);
    createImageForTest(aftUpdateImage);
  }

  private static void createImageForTest(String filePath) {
    FileConversion fileConversion = new FileConversion();
    fileConversion.makeSampleJPEGImage(filePath);
  }

  @BeforeEach
  public void setUp() throws Exception {
    imageEntity = FileEntity.builder()
        .fileName("bef.jpg")
        .filePath("keeper_files" + File.separator + "bef.jpg")
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(imageEntity);

    thumbnailEntity = ThumbnailEntity.builder()
        .path("keeper_files" + File.separator + "thumbnail" + File.separator + "thumb_bef.jpg")
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
        .point(point)
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
    ObjectMapper mapper = new ObjectMapper();
    SingleResult<SignInDto> sign = mapper.readValue(resultString, new TypeReference<>() {
    });
    userToken = sign.getData().getToken();

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
        .point(adminPoint)
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
    SingleResult<SignInDto> adminSign = mapper.readValue(adminResultString, new TypeReference<>() {
    });
    adminToken = adminSign.getData().getToken();
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
    // friend entity에 followee와 follower가 잘 들어갔는지 확인
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
                fieldWithPath("list[].id").description("아이디"),
                fieldWithPath("list[].emailAddress").description("이메일 주소"),
                fieldWithPath("list[].nickName").description("닉네임"),
                fieldWithPath("list[].birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("list[].registerDate").description("가입 날짜"),
                fieldWithPath("list[].point").description("포인트 점수"),
                fieldWithPath("list[].level").description("레벨"),
                fieldWithPath("list[].rank").description("회원 등급: [null/우수회원/일반회원]"),
                fieldWithPath("list[].type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]"),
                fieldWithPath("list[].jobs").description(
                    "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
            )));
  }


  @Test
  @DisplayName("나를 팔로우한 사람 조회하기")
  public void showFollower() throws Exception {
    // follow: admin(follower) -> member
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
                fieldWithPath("list[].id").description("아이디"),
                fieldWithPath("list[].emailAddress").description("이메일 주소"),
                fieldWithPath("list[].nickName").description("닉네임"),
                fieldWithPath("list[].birthday").description("생일").type(Date.class).optional(),
                fieldWithPath("list[].registerDate").description("가입 날짜"),
                fieldWithPath("list[].point").description("포인트 점수"),
                fieldWithPath("list[].level").description("레벨"),
                fieldWithPath("list[].rank").description("회원 등급: [null/우수회원/일반회원]"),
                fieldWithPath("list[].type").description("회원 상태: [null/비회원/정회원/휴면회원/졸업회원/탈퇴]"),
                fieldWithPath("list[].jobs").description(
                    "동아리 직책: [null/ROLE_회장/ROLE_부회장/ROLE_대외부장/ROLE_학술부장/ROLE_전산관리자/ROLE_서기/ROLE_총무/ROLE_사서]")
            )));
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

  @Test
  @DisplayName("포인트 선물하기 - 성공")
  public void transferPoint() throws Exception {
    MemberEntity receiver = memberRepository.findByLoginId("hyeonmoAdmin").orElseThrow(
        CustomAboutFailedException::new);

    String content = "{\n"
        + "\"receiverId\":\"" + receiver.getId() + "\",\n"
        + "\"transmissionPoint\":\"" + 20 + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/point/transfer")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.senderRemainingPoint").value(point - 20))
        .andExpect(jsonPath("$.data.receiverRemainingPoint").value(adminPoint + 20));
  }

  @Test
  @DisplayName("포인트 선물하기 - 실패(포인트 부족)")
  public void transferPointFailByPointLack() throws Exception {
    MemberEntity receiver = memberRepository.findByLoginId("hyeonmoAdmin").orElseThrow(
        CustomAboutFailedException::new);

    String content = "{\n"
        + "\"receiverId\":\"" + receiver.getId() + "\",\n"
        + "\"transmissionPoint\":\"" + 101 + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/point/transfer")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof CustomTransferPointLackException));
  }

  @Test
  @DisplayName("포인트 선물하기 - 실패(멤버 존재 X)")
  public void transferPointFailByNullMember() throws Exception {
    String content = "{\n"
        + "\"receiverId\":\"" + 0 + "\",\n"
        + "\"transmissionPoint\":\"" + 20 + "\""
        + "}";

    mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/member/update/point/transfer")
            .header("Authorization", userToken)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof CustomMemberNotFoundException));
  }

}