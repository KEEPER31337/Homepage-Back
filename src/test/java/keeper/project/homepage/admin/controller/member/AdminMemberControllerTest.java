package keeper.project.homepage.admin.controller.member;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class AdminMemberControllerTest extends ApiControllerTestHelper {

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
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("관리자 권한으로 회원 정보 목록 조회 - 성공")
  public void getMembersSuccess() throws Exception {
    mockMvc.perform(get("/v1/admin/members")
            .header("Authorization", adminToken)
            .param("page", "0")
            .param("size", "20"))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("admin-members",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 20)")
            ),
            responseFields(
                generatePrivateMemberCommonResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("관리자 권한으로 회원 정보 목록 조회 - 실패(권한 부족)")
  public void getMembersFailByAuth() throws Exception {
    mockMvc.perform(get("/v1/admin/members")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "20"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.msg").value("접근이 거부되었습니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 이름 키워드로 회원 아이디 조회")
  public void getMemberIdsByRealName() throws Exception {
    generateMemberByRealName("이정학");
    generateMemberByRealName("김정학");
    generateMemberByRealName("박정학");
    mockMvc.perform(get("/v1/admin/members/ids")
            .header("Authorization", adminToken)
            .param("keyword", "정학"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.size()").value(3))
        .andDo(document("get-memberId-list-by-realName",
            requestParameters(parameterWithName("keyword").description("검색할 이름 키워드")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[].memberId").description("회원 id"),
                fieldWithPath("list.[].realName").description("회원 이름"),
                fieldWithPath("list.[].generation").description("회원 기수"),
                fieldWithPath("list.[].thumbnailPath").description("썸네일 경로")

            )));
  }

  private MemberEntity generateMemberByRealName(String name) {
    final long epochTime = System.nanoTime();
    return memberRepository.save(
        MemberEntity.builder()
            .loginId("abcd1234" + epochTime)
            .generation(12.5F)
            .emailAddress("test1234@keeper.co.kr" + epochTime)
            .password("1234")
            .studentId("1234" + epochTime)
            .nickName("nick" + epochTime)
            .realName(name)
            .build());
  }
}
