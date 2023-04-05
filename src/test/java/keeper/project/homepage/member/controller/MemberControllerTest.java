package keeper.project.homepage.member.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.member.entity.FriendEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.member.service.MemberUtilService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
@Log4j2
public class MemberControllerTest extends ApiControllerTestHelper {

  @Autowired
  private MemberUtilService memberUtilService;

  private String userToken;
  private String deleteToken;
  private String adminToken;

  private MemberEntity userEntity;
  private MemberEntity deleteMemberEntity;
  private MemberEntity adminEntity;

  @BeforeEach
  public void setUp() throws Exception {

    userEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(userEntity);

    deleteMemberEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
        MemberRankName.일반회원);
    deleteToken = generateJWTToken(deleteMemberEntity);

    adminEntity = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(adminEntity);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("잘못된 토큰으로 접근하기")
  public void invalidToken() throws Exception {
    mockMvc.perform(get("/v1/members/profile")
            .header("Authorization", "XXXXXXXXXX"))
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(exceptionUtil.getMessage("accessDenied.code")));
  }

  @Test
  @DisplayName("토큰 없이 접근하기")
  public void noToken() throws Exception {
    mockMvc.perform(get("/v1/members"))
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false));
//        .andExpect(jsonPath("$.code").value(-1002));
  }

  @Test
  @DisplayName("옳은 토큰으로 접근하기")
  public void validToken() throws Exception {
    mockMvc.perform(get("/v1/members/profile")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("권한이 없을 때")
  public void accessDenied() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/admin/members")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(exceptionUtil.getMessage("accessDenied.code")));
  }

  @Test
  @DisplayName("권한이 있을 때")
  public void accessAccept() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/admin/members")
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
            .get("/v1/admin/members")
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
            .get("/v1/members/profile")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").exists());
  }

  @Test
  @DisplayName("팔로우하기")
  public void follow() throws Exception {
    String docMsg = "팔로우할 회원이 존재하지 않는다면 실패합니다.";
    String docCode = "회원이 존재하지 않을 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
        + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(post("/v1/members/follow/{id}", adminEntity.getId())
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-follow",
            pathParameters(
                parameterWithName("id").description("팔로우할 회원의 ID")
            ),
            responseFields(
                generateCommonResponseFields("성공: true +\n실패: false", docCode, docMsg)
            )
        ));

    List<FriendEntity> followeeList = userEntity.getFollowee();
    FriendEntity followee = followeeList.get(followeeList.size() - 1);
    // friend entity에 followee와 follower가 잘 들어갔는지 확인
    assertTrue(followee.getFollowee().equals(adminEntity));
    assertTrue(followee.getFollower().equals(userEntity));
  }

  @Test
  @DisplayName("언팔로우하기")
  public void unfollow() throws Exception {
    memberFollowService.follow(userEntity.getId(), adminEntity.getId());
    List<FriendEntity> followeeList = userEntity.getFollowee();
    FriendEntity followee = followeeList.get(followeeList.size() - 1);

    // unfollow
    String docMsg = "언팔로우할 회원이 존재하지 않는다면 실패합니다.";
    String docCode = "회원이 존재하지 않을 경우: " + exceptionUtil.getMessage("memberNotFound.code") + " +\n"
        + "그 외 에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(delete("/v1/members/unfollow/{id}", adminEntity.getId())
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-unfollow",
            pathParameters(
                parameterWithName("id").description("팔로우한 회원의 로그인 아이디")
            ),
            responseFields(
                generateCommonResponseFields("성공: true +\n실패: false", docCode, docMsg)
            )
        ));

    assertTrue(friendRepository.findById(followee.getId()).isEmpty());
    Assertions.assertFalse(userEntity.getFollowee().contains(followee));
    Assertions.assertFalse(adminEntity.getFollower().contains(followee));
  }

  @Test
  @DisplayName("내가 팔로우한 사람 조회하기")
  public void showFollowee() throws Exception {
    // follow: member -> admin(followee)
    memberFollowService.follow(userEntity.getId(), adminEntity.getId());

    String docMsg = "";
    String docCode = "에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/members/followees")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list[0].nickName").value(adminEntity.getNickName()))
        .andDo(document("member-show-followee",
            responseFields(
                generateMemberCommonResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", docCode, docMsg)
            )
        ));
  }


  @Test
  @DisplayName("나를 팔로우한 사람 조회하기")
  public void showFollower() throws Exception {
    // follow: admin(follower) -> member
    memberFollowService.follow(adminEntity.getId(), userEntity.getId());

    String docMsg = "";
    String docCode = "에러가 발생한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/members/followers")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list[0].nickName").value(adminEntity.getNickName()))
        .andDo(document("member-show-follower",
            responseFields(
                generateMemberCommonResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", docCode, docMsg)
            )
        ));
  }

  @Test
  @DisplayName("회원 탈퇴하기")
  public void deleteAccount() throws Exception {
    String docMsg = "물품을 대여하고 미납한 기록이 남아있을 경우, 또는 입력한 비밀번호가 옳지 않은 경우 탈퇴가 실패합니다.";
    String docCode =
        "물품 미납 기록이 있거나 비밀번호가 틀린 경우: " + exceptionUtil.getMessage("accountDeleteFailed.code")
            + " +\n" + "그 외 실패한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(delete("/v1/members")
            .param("password", memberPassword)
            .header("Authorization", deleteToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-delete",
            requestParameters(
                parameterWithName("password").description("비밀번호")
            ),
            responseFields(
                generateCommonResponseFields("성공 시: success, 실패 시: fail", docCode, docMsg)
            )
        ));

    Assertions.assertThrows(CustomMemberNotFoundException.class, () -> {
      memberUtilService.getById(deleteMemberEntity.getId());
    });
  }

  @Test
  @DisplayName("팔로워, 팔로우 숫자 조회하기")
  public void getFollowerAndFolloweeCount() throws Exception {
    int followerNum = 3;
    int followeeNum = 2;
    for (int i = 0; i < followerNum; i++) {
      MemberEntity follower = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
          MemberRankName.일반회원);
      memberFollowService.follow(follower.getId(), userEntity.getId());
    }
    for (int i = 0; i < followeeNum; i++) {
      MemberEntity followee = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
          MemberRankName.일반회원);
      memberFollowService.follow(userEntity.getId(), followee.getId());
    }

    // TODO : 예외 처리 & doc 메세지 채우기 (회원 관리 전체적으로 예외 수정할 예정)
    String docMsg = "";
    String docCode = "실패한 경우: " + exceptionUtil.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/members/follow-number")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.followerNumber").value(followerNum))
        .andExpect(jsonPath("$.data.followeeNumber").value(followeeNum))
        .andDo(document("member-follow-number",
            responseFields(
                generateCommonFollowResponse(ResponseType.SINGLE, "성공 시: success, 실패 시: fail",
                    docCode, docMsg)
            )));
  }

  @Test
  @DisplayName("다른 유저 정보 리스트 조회하기 - 성공")
  public void getAllOtherUserInfoSuccess() throws Exception {
    String expectedById = "$.list.[?(@.memberId == '%s')]";

    mockMvc.perform(get("/v1/members/others")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath(expectedById, userEntity.getId()).exists())
        .andDo(document("member-otherInfo-lists",
            responseFields(
                generateOtherMemberInfoCommonResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("다른 유저 정보 리스트 조회하기 - 실패(유효하지 않은 토큰)")
  public void getAllOtherUserInfoFail() throws Exception {
    mockMvc.perform(get("/v1/members/others")
            .header("Authorization", 111111))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("접근이 거부되었습니다."));
  }

  @Test
  @DisplayName("다른 유저 ID를 통해 정보 조회하기 - 성공")
  public void getOtherUserInfoByIdSuccess() throws Exception {
    mockMvc.perform(get("/v1/members/others/{id}", adminEntity.getId())
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-otherInfo-ById",
            pathParameters(
                parameterWithName("id").description("찾고자 하는 다른 유저의 데이터베이스 상의 ID")
            ),
            responseFields(
                generateOtherMemberInfoCommonResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("다른 유저 ID를 통해 정보 조회하기 - 실패(유효하지 않은 토큰)")
  public void getOtherUserInfoByIdFailByToken() throws Exception {
    mockMvc.perform(get("/v1/members/others/{id}", adminEntity.getId())
            .header("Authorization", 111111))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("접근이 거부되었습니다."));
  }

  @Test
  @DisplayName("다른 유저 ID를 통해 정보 조회하기 - 실패(존재하지 않는 ID)")
  public void getOtherUserInfoByIdFailById() throws Exception {
    mockMvc.perform(get("/v1/members/others/{id}", 1234567)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 회원입니다."));
  }

  @Test
  @DisplayName("다른 유저 ID를 통해 정보 조회하기 - 실패(탈퇴회원 조회)")
  public void getOtherUserInfoByIdFailByVirtualId() throws Exception {
    mockMvc.perform(get("/v1/members/others/{id}", 1)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("접근할 수 없는 회원입니다."));
  }

  @Test
  @DisplayName("내 정보 조회하기 - 성공")
  public void getMemberSuccess() throws Exception {
    mockMvc.perform(get("/v1/members/profile")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-info",
            responseFields(
                generatePrivateMemberCommonResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("내 정보 조회하기 - 실패(권한 에러)")
  public void getMemberFailByAuth() throws Exception {
    mockMvc.perform(get("/v1/members/profile")
            .header("Authorization", 1234))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.msg").value("접근이 거부되었습니다."));
  }

  @Test
  @DisplayName("다중 회원 조회 - 성공")
  public void getMultiMembersSuccess() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String ids = userEntity.getId() + "," + adminEntity.getId() + "," + "1,3";

    params.add("ids", ids);
    mockMvc.perform(get("/v1/members/multi")
            .header("Authorization", userToken)
            .params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-multi",
            requestParameters(
                parameterWithName("ids").description(
                    "조회하고자 하는 멤버 ID 리스트 ex) /v1/members/multi?ids=1,2,3")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].id").description("멤버 ID"),
                fieldWithPath("list[].nickName").description("멤버 닉네임").optional(),
                fieldWithPath("list[].thumbnailPath").description("회원의 썸네일 이미지 조회 api path")
                    .optional(),
                fieldWithPath("list[].generation").description("멤버 기수 (7월 이후는 N.5기)").optional(),
                fieldWithPath("list[].jobs").description(
                        "동아리 직책: null, ROLE_회장, ROLE_부회장, ROLE_대외부장, ROLE_학술부장, ROLE_전산관리자, ROLE_서기, ROLE_총무, ROLE_사서")
                    .optional(),
                fieldWithPath("list[].type").description("회원 상태: null, 비회원, 정회원, 휴면회원, 졸업회원, 탈퇴")
                    .optional(),
                fieldWithPath("list[].msg").description("멤버 조회 성공 여부 (실패 시 이유 반환)")
            )));
  }

  @Test
  @DisplayName("다중 회원 조회 - Virtual Member")
  public void getMultiMembersWithVirtualMember() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String ids = "1";

    params.add("ids", ids);
    mockMvc.perform(get("/v1/members/multi")
            .header("Authorization", userToken)
            .params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.list[0].id").value(1))
        .andExpect(jsonPath("$.list[0].msg").value("Fail: Access Virtual Member"));
  }

  @Test
  @DisplayName("다중 회원 조회 - 존재하지 않는 Member")
  public void getMultiMembersWithNotExistMember() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    String ids = "-999";

    params.add("ids", ids);
    mockMvc.perform(get("/v1/members/multi")
            .header("Authorization", userToken)
            .params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.list[0].id").value(-999))
        .andExpect(jsonPath("$.list[0].msg").value("Fail: Not Exist Member"));
  }

  @Test
  @DisplayName("[SUCCESS] 회원 기수 목록 가져오기")
  public void getAllGenerations() throws Exception {
    generateMemberByGeneration(3.5f);
    generateMemberByGeneration(5f);
    generateMemberByGeneration(8f);
    generateMemberByGeneration(8f);
    mockMvc.perform(get("/v1/members/generations")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.size()").value(4))
        .andExpect(jsonPath("$.list[0]").value(getMemberGeneration()))
        .andExpect(jsonPath("$.list[1]").value(8f))
        .andExpect(jsonPath("$.list[2]").value(5f))
        .andExpect(jsonPath("$.list[3]").value(3.5f))
        .andDo(document("get-all-generations",
            responseFields(
                generateGetGenerationListResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  private MemberEntity generateMemberByGeneration(Float generation) {
    final String epochTime = Long.toHexString(System.nanoTime()).substring(0, 10);
    return memberRepository.save(MemberEntity.builder()
        .loginId("LoginId" + epochTime)
        .password(passwordEncoder.encode(memberPassword))
        .nickName("nickName" + epochTime)
        .realName("RealName" + epochTime)
        .emailAddress("member" + epochTime + "@k33p3r.com")
        .generation(generation)
        .build());
  }
}