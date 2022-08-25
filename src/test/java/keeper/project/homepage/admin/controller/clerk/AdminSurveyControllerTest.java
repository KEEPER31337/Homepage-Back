package keeper.project.homepage.admin.controller.clerk;

import static keeper.project.homepage.controller.clerk.SurveySpringTestHelper.Reply.ACTIVITY;
import static keeper.project.homepage.controller.clerk.SurveySpringTestHelper.Reply.OTHER_DORMANT;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import keeper.project.homepage.admin.dto.clerk.request.AdminSurveyRequestDto;
import keeper.project.homepage.controller.clerk.SurveySpringTestHelper;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class AdminSurveyControllerTest extends SurveySpringTestHelper {

  private MemberEntity user;
  private MemberEntity admin;
  private String adminToken;

  @BeforeEach
  public void setUp() throws Exception {
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(admin);
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
  }

  @Test
  @DisplayName("[SUCCESS] 설문 조사 개설")
  public void createSurvey() throws Exception {
    String surveyName = "2023년 1학기 활동조사";
    String description = "2023년 1학기 키퍼 회원의 활동 조사를 위한 설문조사";
    LocalDateTime openTime = LocalDateTime.now();
    LocalDateTime closeTime = LocalDateTime.now().plusDays(2);
    Boolean isVisible = false;

    AdminSurveyRequestDto adminSurveyRequestDto = AdminSurveyRequestDto.builder()
        .surveyName(surveyName)
        .description(description)
        .openTime(openTime)
        .closeTime(closeTime)
        .isVisible(isVisible)
        .build();

    mockMvc.perform(post("/v1/admin/clerk/survey")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(adminSurveyRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data").isNumber())
        .andDo(document("survey-create",
            requestFields(
                fieldWithPath("surveyName").description("생성할 설문 조사의 이름"),
                fieldWithPath("description").description("생성할 설문조사의 설명"),
                fieldWithPath("openTime").description("설문조사 시작 시간").optional(),
                fieldWithPath("closeTime").description("설문조사 마감 시간").optional(),
                fieldWithPath("isVisible").description("설문조사 공개 여부")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("생성에 성공한 설문조사의 ID")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 설문 삭제")
  public void deleteSurvey() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        true);

    mockMvc.perform(delete("/v1/admin/clerk/survey/{surveyId}", survey.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-delete",
            pathParameters(
                parameterWithName("surveyId").description("삭제하고자 하는 설문 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.surveyId").description("삭제에 성공한 설문 ID"),
                fieldWithPath("data.surveyName").description("삭제에 성공한 설문 이름")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 설문 응답자 목록 조회")
  public void getRespondents() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        true);
    generateSurveyMemberReply(survey, admin, surveyReplyRepository.getById(ACTIVITY.getReplyId()));
    SurveyMemberReplyEntity surveyMemberReplyEntity = generateSurveyMemberReply(survey, user,
        surveyReplyRepository.getById(OTHER_DORMANT.getReplyId()));
    generateSurveyReplyExcuse(surveyMemberReplyEntity, "BOB");

    mockMvc.perform(get("/v1/admin/clerk/survey/{surveyId}/respondents", survey.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-respondents",
            pathParameters(
                parameterWithName("surveyId").description("응답자를 조회하고자 하는 설문 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].memberId").description("응답자 멤버 ID"),
                fieldWithPath("list[].realName").description("응답자 실제 이름"),
                fieldWithPath("list[].thumbnailPath").description("응답자 썸네일 경로"),
                fieldWithPath("list[].generation").description("응답자 기수"),
                fieldWithPath("list[].reply").description("응답자의 응답"),
                fieldWithPath("list[].excuse").description("응답이 휴면(기타)일 경우 사유")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 설문 수정")
  public void modifySurvey() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        true);
    generateSurveyMemberReply(survey, admin, surveyReplyRepository.getById(ACTIVITY.getReplyId()));

    String modifySurveyName = "2022년 2학기";
    LocalDateTime modifyOpenTime = LocalDateTime.now();
    LocalDateTime modifyCloseTime = LocalDateTime.now().plusDays(10);
    String modifyDescription = "방학 기간임을 고려하여 설문 기간을 다시 설정했습니다.";
    Boolean isVisible = true;

    AdminSurveyRequestDto adminSurveyRequestDto = AdminSurveyRequestDto.builder()
        .surveyName(modifySurveyName)
        .openTime(modifyOpenTime)
        .closeTime(modifyCloseTime)
        .description(modifyDescription)
        .isVisible(isVisible)
        .build();

    mockMvc.perform(patch("/v1/admin/clerk/survey/{surveyId}", survey.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(adminSurveyRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-modify",
            pathParameters(
                parameterWithName("surveyId").description("수정할 설문의 ID")
            ),
            requestFields(
                fieldWithPath("surveyName").description("설문 이름"),
                fieldWithPath("description").description("설문 설명"),
                fieldWithPath("openTime").description("설문 시작 시간"),
                fieldWithPath("closeTime").description("설문 마감 시간"),
                fieldWithPath("isVisible").description("설문 공개 여부")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.surveyId").description("수정에 성공한 설문 ID"),
                fieldWithPath("data.surveyName").description("수정에 성공한 설문 이름"),
                fieldWithPath("data.description").description("수정에 성공한 설문 설명"),
                fieldWithPath("data.openTime").description("수정에 성공한 설문 시작 시간"),
                fieldWithPath("data.closeTime").description("수정에 성공한 설문 마감 시간"),
                fieldWithPath("data.isVisible").description("수정에 성공한 설문 공개 여부"),
                subsectionWithPath("data.respondents").description("설문에 응답한 응답자 정보")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 설문 공개")
  public void openSurvey() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        false);

    mockMvc.perform(patch("/v1/admin/clerk/survey/{surveyId}/open", survey.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data.surveyId").value(survey.getId()))
        .andExpect(jsonPath("$.data.surveyName").value(survey.getName()))
        .andExpect(jsonPath("$.data.isVisible").value(true))
        .andDo(document("survey-open",
            pathParameters(
                parameterWithName("surveyId").description("공개하고자 하는 설문의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.surveyId").description("공개한 설문의 ID"),
                fieldWithPath("data.surveyName").description("공개한 설문의 이름"),
                fieldWithPath("data.isVisible").description("공개 여부")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 설문 비공개")
  public void closeSurvey() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        true);

    mockMvc.perform(patch("/v1/admin/clerk/survey/{surveyId}/close", survey.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data.surveyId").value(survey.getId()))
        .andExpect(jsonPath("$.data.surveyName").value(survey.getName()))
        .andExpect(jsonPath("$.data.isVisible").value(false))
        .andDo(document("survey-close",
            pathParameters(
                parameterWithName("surveyId").description("비공개하고자 하는 설문의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.surveyId").description("비공개한 설문의 ID"),
                fieldWithPath("data.surveyName").description("비공개한 설문의 이름"),
                fieldWithPath("data.isVisible").description("공개 여부")
            )));
  }

}
