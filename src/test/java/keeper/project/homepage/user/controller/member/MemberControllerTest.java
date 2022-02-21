package keeper.project.homepage.user.controller.member;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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
    userToken = generateJWTToken(userEntity.getLoginId(), memberPassword);
    adminEntity = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(adminEntity.getLoginId(), memberPassword);
    for (int i = 0; i < 22; i++) {
      generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    }
  }

  @Test
  @DisplayName("다른 유저 정보 리스트 조회하기 - 성공")
  public void getAllOtherUserInfoSuccess() throws Exception {
    mockMvc.perform(get("/v1/members")
            .param("page", "0")
            .param("size", "20")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-otherInfo-lists",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 20)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].loginId").description("해당 유저의 로그인 아이디"),
                fieldWithPath("list[].realName").description("해당 유저의 실제 이름"),
                fieldWithPath("list[].nickName").description("해당 유저의 닉네임"),
                fieldWithPath("list[].birthday").description("해당 유저의 생일"),
                fieldWithPath("list[].registerDate").description("해당 유저의 회원가입 날짜 및 시간"),
                subsectionWithPath("list[].memberTypeEntity").description("해당 유저의 회원 타입"),
                subsectionWithPath("list[].memberRankEntity").description("해당 유저의 회원 랭크"),
                fieldWithPath("list[].thumbnailEntity").description("해당 유저의 썸네일"),
                fieldWithPath("list[].generation").description("해당 유저의 회원 기수")

            )));
  }

  @Test
  @DisplayName("다른 유저 정보 리스트 조회하기 - 실패(유효하지 않은 토큰)")
  public void getAllOtherUserInfoFail() throws Exception {
    mockMvc.perform(get("/v1/members")
            .param("page", "0")
            .param("size", "10")
            .header("Authorization", 111111))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
  }

  @Test
  @DisplayName("다른 유저 ID를 통해 정보 조회하기 - 성공")
  public void getOtherUserInfoByIdSuccess() throws Exception {
    mockMvc.perform(get("/v1/member/other-id/{id}", adminEntity.getId())
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-otherInfo-ById",
            pathParameters(
                parameterWithName("id").description("찾고자 하는 다른 유저의 데이터베이스 상의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.loginId").description("해당 유저의 로그인 아이디"),
                fieldWithPath("data.realName").description("해당 유저의 실제 이름"),
                fieldWithPath("data.nickName").description("해당 유저의 닉네임"),
                fieldWithPath("data.birthday").description("해당 유저의 생일"),
                fieldWithPath("data.registerDate").description("해당 유저의 회원가입 날짜 및 시간"),
                subsectionWithPath("data.memberTypeEntity").description("해당 유저의 회원 타입"),
                subsectionWithPath("data.memberRankEntity").description("해당 유저의 회원 랭크"),
                fieldWithPath("data.thumbnailEntity").description("해당 유저의 썸네일"),
                fieldWithPath("data.generation").description("해당 유저의 회원 기수")
            )));
  }

  @Test
  @DisplayName("다른 유저 ID를 통해 정보 조회하기 - 실패(유효하지 않은 토큰)")
  public void getOtherUserInfoByIdFailByToken() throws Exception {
    mockMvc.perform(get("/v1/member/other-id/{id}", adminEntity.getId())
            .header("Authorization", 111111))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
  }

  @Test
  @DisplayName("다른 유저 ID를 통해 정보 조회하기 - 실패(존재하지 않는 ID)")
  public void getOtherUserInfoByIdFailById() throws Exception {
    mockMvc.perform(get("/v1/member/other-id/{id}", 1234567)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 회원입니다."));
  }

  @Test
  @DisplayName("다른 유저 실제 이름을 통해 정보 조회하기 - 성공")
  public void getOtherUserInfoByRealNameSuccess() throws Exception {
    mockMvc.perform(get("/v1/member/other-name/{name}", adminEntity.getRealName())
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("member-otherInfo-ByRealName",
            pathParameters(
                parameterWithName("name").description("찾고자 하는 다른 유저의 실제 이름")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.loginId").description("해당 유저의 로그인 아이디"),
                fieldWithPath("data.realName").description("해당 유저의 실제 이름"),
                fieldWithPath("data.nickName").description("해당 유저의 닉네임"),
                fieldWithPath("data.birthday").description("해당 유저의 생일"),
                fieldWithPath("data.registerDate").description("해당 유저의 회원가입 날짜 및 시간"),
                subsectionWithPath("data.memberTypeEntity").description("해당 유저의 회원 타입"),
                subsectionWithPath("data.memberRankEntity").description("해당 유저의 회원 랭크"),
                fieldWithPath("data.thumbnailEntity").description("해당 유저의 썸네일"),
                fieldWithPath("data.generation").description("해당 유저의 회원 기수")
            )));

  }

  @Test
  @DisplayName("다른 유저 실제 이름을 통해 정보 조회하기 - 실패(존재하지 않는 이름)")
  public void getOtherUserInfoByRealNameFailByName() throws Exception {
    mockMvc.perform(get("/v1/member/other-name/{name}", "없는 이름")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 회원입니다."));

  }

}