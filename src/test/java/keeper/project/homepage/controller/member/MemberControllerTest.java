package keeper.project.homepage.controller.member;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class MemberControllerTest extends ApiControllerTestHelper {

  private String userToken;
  private String deleteToken;
  private String adminToken;

  private MemberEntity memberEntity;
  private MemberEntity deleteMemberEntity;
  private MemberEntity memberAdmin;

  @BeforeEach
  public void setUp() throws Exception {

    memberEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(memberEntity);

    deleteMemberEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    deleteToken = generateJWTToken(deleteMemberEntity);

    memberAdmin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(memberAdmin);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("잘못된 토큰으로 접근하기")
  public void invalidToken() throws Exception {
    mockMvc.perform(get("/v1/members")
            .header("Authorization", "XXXXXXXXXX"))
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(exceptionAdvice.getMessage("accessDenied.code")));
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
    mockMvc.perform(get("/v1/members")
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
        .andExpect(jsonPath("$.code").value(exceptionAdvice.getMessage("accessDenied.code")));
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
            .get("/v1/members")
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
    String docCode = "회원이 존재하지 않을 경우: " + exceptionAdvice.getMessage("memberNotFound.code") + " +\n"
        + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    mockMvc.perform(post("/v1/members/follow/{id}", memberAdmin.getId())
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

    List<FriendEntity> followeeList = memberEntity.getFollowee();
    FriendEntity followee = followeeList.get(followeeList.size() - 1);
    // friend entity에 followee와 follower가 잘 들어갔는지 확인
    assertTrue(followee.getFollowee().equals(memberAdmin));
    assertTrue(followee.getFollower().equals(memberEntity));
  }

  @Test
  @DisplayName("언팔로우하기")
  public void unfollow() throws Exception {
    memberService.follow(memberEntity.getId(), memberAdmin.getId());
    List<FriendEntity> followeeList = memberEntity.getFollowee();
    FriendEntity followee = followeeList.get(followeeList.size() - 1);

    // unfollow
    String docMsg = "언팔로우할 회원이 존재하지 않는다면 실패합니다.";
    String docCode = "회원이 존재하지 않을 경우: " + exceptionAdvice.getMessage("memberNotFound.code") + " +\n"
        + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    mockMvc.perform(delete("/v1/members/unfollow/{id}", memberAdmin.getId())
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
    Assertions.assertFalse(memberEntity.getFollowee().contains(followee));
    Assertions.assertFalse(memberAdmin.getFollower().contains(followee));
  }

  @Test
  @DisplayName("내가 팔로우한 사람 조회하기")
  public void showFollowee() throws Exception {
    // follow: member -> admin(followee)
    memberService.follow(memberEntity.getId(), memberAdmin.getId());

    String docMsg = "";
    String docCode = "에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/members/followees")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list[0].nickName").value(memberAdmin.getNickName()))
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
    memberService.follow(memberAdmin.getId(), memberEntity.getId());

    String docMsg = "";
    String docCode = "에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/members/followers")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list[0].nickName").value(memberAdmin.getNickName()))
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
        "물품 미납 기록이 있거나 비밀번호가 틀린 경우: " + exceptionAdvice.getMessage("accountDeleteFailed.code")
            + " +\n" + "그 외 실패한 경우: " + exceptionAdvice.getMessage("unKnown.code");
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
      memberService.findById(deleteMemberEntity.getId());
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
      memberService.follow(follower.getId(), memberEntity.getId());
    }
    for (int i = 0; i < followeeNum; i++) {
      MemberEntity followee = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
          MemberRankName.일반회원);
      memberService.follow(memberEntity.getId(), followee.getId());
    }

    // TODO : 예외 처리 & doc 메세지 채우기 (회원 관리 전체적으로 예외 수정할 예정)
    String docMsg = "";
    String docCode = "실패한 경우: " + exceptionAdvice.getMessage("unKnown.code");
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
}