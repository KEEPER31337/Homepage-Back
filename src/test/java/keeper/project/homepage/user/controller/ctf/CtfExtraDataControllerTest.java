package keeper.project.homepage.user.controller.ctf;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.출제자;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ApiControllerTestHelper.MemberJobName;
import keeper.project.homepage.ApiControllerTestHelper.MemberRankName;
import keeper.project.homepage.ApiControllerTestHelper.MemberTypeName;
import keeper.project.homepage.controller.ctf.CtfSpringTestHelper;
import keeper.project.homepage.entity.member.MemberEntity;
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
                fieldWithPath("list[].id").description("문제가 속한 타입의 id"),
                fieldWithPath("list[].name").description("문제가 속한 타입의 이름")
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
                fieldWithPath("list[].id").description("문제가 속한 카테고리의 id"),
                fieldWithPath("list[].name").description("문제가 속한 카테고리의 이름")
            )));
  }
}