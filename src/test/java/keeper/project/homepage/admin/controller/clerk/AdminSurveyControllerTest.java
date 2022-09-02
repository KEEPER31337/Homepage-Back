package keeper.project.homepage.admin.controller.clerk;

import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.GRADUATE;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.OTHER_DORMANT;
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
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
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
  private String userToken;

  @BeforeEach
  public void setUp() throws Exception {
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(admin);
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(user);
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

    mockMvc.perform(post("/v1/admin/clerk/surveys")
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

    mockMvc.perform(delete("/v1/admin/clerk/surveys/{surveyId}", survey.getId())
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
    generateSurveyMemberReply(survey, admin, surveyReplyRepository.getById(ACTIVITY.getId()));
    SurveyMemberReplyEntity surveyMemberReplyEntity = generateSurveyMemberReply(survey, user,
        surveyReplyRepository.getById(OTHER_DORMANT.getId()));
    generateSurveyReplyExcuse(surveyMemberReplyEntity, "BOB");

    mockMvc.perform(get("/v1/admin/clerk/surveys/{surveyId}/respondents", survey.getId())
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
    generateSurveyMemberReply(survey, admin, surveyReplyRepository.getById(ACTIVITY.getId()));

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

    mockMvc.perform(patch("/v1/admin/clerk/surveys/{surveyId}", survey.getId())
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

    mockMvc.perform(patch("/v1/admin/clerk/surveys/{surveyId}/open", survey.getId())
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

    mockMvc.perform(patch("/v1/admin/clerk/surveys/{surveyId}/close", survey.getId())
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

  @Test
  @DisplayName("[SUCCESS] 설문 정보 조회")
  public void getSurveyInformation() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        true);
    generateSurveyMemberReply(survey, admin, surveyReplyRepository.getById(ACTIVITY.getId()));

    mockMvc.perform(
            get("/v1/admin/clerk/surveys/information/{surveyId}", survey.getId())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-admin-information",
            pathParameters(
                parameterWithName("surveyId").description("조회하는 설문 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.surveyId").description("조회한 설문 ID"),
                fieldWithPath("data.surveyName").description("조회한 설문 이름"),
                fieldWithPath("data.openTime").description("조회한 설문의 시작 시간"),
                fieldWithPath("data.closeTime").description("조회한 설문의 마감 시간"),
                fieldWithPath("data.description").description("조회한 설문의 설명"),
                fieldWithPath("data.isVisible").description("설문 공개 여부"),
                fieldWithPath("data.isResponded").description("설문 응답 여부"),
                fieldWithPath("data.replyId").description("설문에 응답한 응답 ID"),
                fieldWithPath("data.excuse").description("응답이 휴면(기타)일 경우 사유")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 공개 상태의 설문 조회(가장 최근) - 현재 진행중인")
  public void getLatestVisibleSurveyId() throws Exception {
    SurveyEntity survey1 = surveyRepository.getById(ACTIVITY.getId());
    SurveyEntity survey2 = generateSurvey(LocalDateTime.now().minusDays(2),
        LocalDateTime.now().plusDays(2),
        true);

    mockMvc.perform(
            get("/v1/admin/clerk/surveys/visible/ongoing")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data").isNumber())
        .andDo(document("survey-admin-visible-latest",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("조회한 설문 ID")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 종료된 설문 정보 조회(가장 최근)")
  public void getLatestClosedSurveyInformation() throws Exception {
    SurveyEntity survey1 = surveyRepository.getById(ACTIVITY.getId());
    SurveyEntity survey2 = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().minusDays(2),
        true);
    generateSurveyMemberReply(survey2, admin, surveyReplyRepository.getById(ACTIVITY.getId()));

    mockMvc.perform(
            get("/v1/admin/clerk/surveys/visible/closed")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-admin-closed-latest",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.surveyId").description("가장 최근에 종료된 설문 ID"),
                fieldWithPath("data.surveyName").description("가장 최근에 종료된 설문 이름"),
                fieldWithPath("data.replyId").description("가장 최근에 종료된 설문에 응답한 응답")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 설문 목록 조회")
  public void getSurveyList() throws Exception {
    Boolean isVisible = true;
    for (int i = 0; i< 7; i++){
      SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
          isVisible);
      generateSurveyMemberReply(survey, admin, surveyReplyRepository.getById(ACTIVITY.getId()));
      isVisible = !isVisible;
    }

    String page = "0";
    String size = "10";

    mockMvc.perform(
            get("/v1/admin/clerk/surveys/list")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", page)
                .param("size", size))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-list",
            pathParameters(
                parameterWithName("page").description("설문 목록의 페이지 번호(default = 0)").optional(),
                parameterWithName("size").description("설문 목록 한 페이지의 개수(default = 10)").optional()
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("page.content[].surveyId").description("조회한 설문 ID"),
                fieldWithPath("page.content[].surveyName").description("조회한 설문 이름"),
                fieldWithPath("page.content[].openTime").description("조회한 설문의 시작 시간"),
                fieldWithPath("page.content[].closeTime").description("조회한 설문의 마감 시간"),
                fieldWithPath("page.content[].description").description("조회한 설문의 설명"),
                fieldWithPath("page.content[].isVisible").description("설문 공개 여부"),
                fieldWithPath("page.empty").description("페이지가 비었는 지 여부"),
                fieldWithPath("page.first").description("첫 페이지 인지"),
                fieldWithPath("page.last").description("마지막 페이지 인지"),
                fieldWithPath("page.number").description("요소를 가져 온 페이지 번호 (0부터 시작)"),
                fieldWithPath("page.numberOfElements").description("요소 개수"),
                subsectionWithPath("page.pageable").description("해당 페이지에 대한 DB 정보"),
                fieldWithPath("page.size").description("요청한 페이지 크기"),
                subsectionWithPath("page.sort").description("정렬에 대한 정보"),
                fieldWithPath("page.totalElements").description("총 요소 개수"),
                fieldWithPath("page.totalPages").description("총 페이지")
            )));
  }

}
