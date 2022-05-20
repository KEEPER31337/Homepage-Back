package keeper.project.homepage.user.controller.member;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
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
}