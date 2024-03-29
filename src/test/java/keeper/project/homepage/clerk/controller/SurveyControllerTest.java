package keeper.project.homepage.clerk.controller;

import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.OTHER_DORMANT;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import keeper.project.homepage.clerk.controller.SurveySpringTestHelper;
import keeper.project.homepage.clerk.entity.SurveyEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.clerk.dto.request.SurveyResponseRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class SurveyControllerTest extends SurveySpringTestHelper {

  private MemberEntity user;
  private MemberEntity admin;
  private String userToken;
  private String adminToken;

  @BeforeEach
  public void setUp() throws Exception {
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(admin);
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(user);
  }

  @Test
  @DisplayName("[SUCCESS] 설문 조사 응답")
  public void replySurvey() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);

    SurveyResponseRequestDto surveyResponseRequestDto = SurveyResponseRequestDto.builder()
        .replyId(OTHER_DORMANT.getId())
        .excuse("BOB 교육으로 인한 휴학")
        .build();

    mockMvc.perform(post("/v1/clerk/surveys/{surveyId}", survey.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(surveyResponseRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data").isNumber())
        .andDo(document("survey-response",
            pathParameters(
                parameterWithName("surveyId").description("응답할 설문의 ID")
            ),
            requestFields(
                fieldWithPath("replyId").description("응답 ID"),
                fieldWithPath("excuse").description("응답이 휴면(기타)일 경우 사유")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("응답에 성공한 설문조사의 ID")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 설문 응답 수정")
  public void modifyResponse() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        true);
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(ACTIVITY.getId()));

    Long modifyReplyId = OTHER_DORMANT.getId();
    String excuse = "BOB 교육으로 인한 휴학";

    SurveyResponseRequestDto surveyResponseRequestDto = SurveyResponseRequestDto.builder()
        .replyId(modifyReplyId)
        .excuse(excuse)
        .build();

    mockMvc.perform(patch("/v1/clerk/surveys/{surveyId}", survey.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(surveyResponseRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-response-modify",
            pathParameters(
                parameterWithName("surveyId").description("응답을 수정할 설문의 Id")
            ),
            requestFields(
                fieldWithPath("replyId").description("수정할 응답"),
                fieldWithPath("excuse").description("휴면(기타) 응답일 경우 사유")
            ),
            responseFields(
                generateSurveyResponseModifyDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 설문 정보 조회")
  public void getSurveyInformation() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        true);
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(ACTIVITY.getId()));

    mockMvc.perform(
            get("/v1/clerk/surveys/information/{surveyId}", survey.getId())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-information",
            pathParameters(
                parameterWithName("surveyId").description("조회하는 설문 ID")
            ),
            responseFields(
                generateSurveyInformationDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
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
            get("/v1/clerk/surveys/visible/ongoing")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data").isNumber())
        .andDo(document("survey-visible-latest",
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
    generateSurveyMemberReply(survey2, user, surveyReplyRepository.getById(ACTIVITY.getId()));

    mockMvc.perform(
            get("/v1/clerk/surveys/visible/closed")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-closed-latest",
            responseFields(
                generateCloseSurveyInformationDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }
}
