package keeper.project.homepage.user.controller.member;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class MemberControllerTest extends ApiControllerTestHelper {

  private MemberEntity userEntity;
  private MemberEntity adminEntity;

  private String userToken;
  private String adminToken;

  @BeforeEach
  public void setUp() throws Exception {
    userEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(userEntity);
    adminEntity = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(adminEntity);
    for (int i = 0; i < 22; i++) {
      generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    }
    System.out.println(userEntity.getId());
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
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
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
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
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
    mockMvc.perform(get("/v1/members")
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
    mockMvc.perform(get("/v1/members")
            .header("Authorization", 1234))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
  }
}