package keeper.project.homepage.ctf.controller;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.출제자;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ctf.controller.CtfSpringTestHelper;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CtfExtraDataControllerTest extends CtfSpringTestHelper {

  private MemberEntity userEntity;
  private String userToken;

  @BeforeEach
  public void setUp() throws Exception {
    userEntity = generateMemberEntity(회원, 정회원, 일반회원);
    userToken = generateJWTToken(userEntity);
  }

  @Test
  @DisplayName("문제 출제자 리스트 불러오기")
  void getChallengeMakerList() throws Exception {
    generateMemberEntity(출제자, 정회원, 일반회원);
    generateMemberEntity(출제자, 정회원, 일반회원);
    generateMemberEntity(출제자, 정회원, 일반회원);

    mockMvc.perform(get("/v1/ctf/extra/data/challenge-maker")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.list.size()").value(3))
        .andDo(document("get-challenge-maker-list",
            responseFields(
                generateCommonMemberCommonResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("문제 타입 불러오기")
  void getChallengeTypeList() throws Exception {
    mockMvc.perform(get("/v1/ctf/extra/data/challenge-type")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("get-challenge-type-list",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].id").description("타입의 id"),
                fieldWithPath("list[].name").description("타입의 이름")
            )));
  }

  @Test
  @DisplayName("문제 카테고리 불러오기")
  void getChallengeCategoryList() throws Exception {
    mockMvc.perform(get("/v1/ctf/extra/data/challenge-category")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("get-challenge-category-list",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].id").description("카테고리의 id"),
                fieldWithPath("list[].name").description("카테고리의 이름")
            )));
  }

  @Test
  @DisplayName("참가 가능한 CTF 목록 불러오기")
  void getContestListSuccess() throws Exception {
    generateCtfContest(userEntity, false);
    generateCtfContest(userEntity, false);
    CtfContestEntity joinableContest1 = generateCtfContest(userEntity, true);
    CtfContestEntity joinableContest2 = generateCtfContest(userEntity, true);
    CtfContestEntity joinableContest3 = generateCtfContest(userEntity, true);
    CtfContestEntity joinableContest4 = generateCtfContest(userEntity, true);
    mockMvc.perform(get("/v1/ctf/extra/data/contests")
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.list.size()").value(4))
        .andExpect(jsonPath("$.list[0].ctfId").value(joinableContest4.getId()))
        .andExpect(jsonPath("$.list[1].ctfId").value(joinableContest3.getId()))
        .andExpect(jsonPath("$.list[2].ctfId").value(joinableContest2.getId()))
        .andExpect(jsonPath("$.list[3].ctfId").value(joinableContest1.getId()))
        .andDo(document("get-contest-list",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].ctfId").description("해당 CTF의 ID"),
                fieldWithPath("list[].name").description("해당 CTF의 이름"),
                fieldWithPath("list[].description").description("해당 CTF의 상세정보"),
                subsectionWithPath("list[].creator").description("해당 CTF 생성자의 정보")
            )));
  }
}