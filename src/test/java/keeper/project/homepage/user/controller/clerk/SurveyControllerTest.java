package keeper.project.homepage.user.controller.clerk;

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
import keeper.project.homepage.controller.clerk.SurveySpringTestHelper;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.clerk.request.SurveyInformationRequestDto;
import keeper.project.homepage.user.dto.clerk.request.SurveyResponseRequestDto;
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
        .memberId(user.getId())
        .replyId(3L)
        .excuse("BOB 교육으로 인한 휴학")
        .replyTime(LocalDateTime.now())
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
                fieldWithPath("memberId").description("응답하는 멤버의 ID"),
                fieldWithPath("replyId").description("응답 ID"),
                fieldWithPath("excuse").description("응답이 휴면(기타)일 경우 사유"),
                fieldWithPath("replyTime").description("응답한 시간")
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
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(1L));

    Long modifyReplyId = 3L;
    String excuse = "BOB 교육으로 인한 휴학";
    LocalDateTime replyTime = LocalDateTime.now();

    SurveyResponseRequestDto surveyResponseRequestDto = SurveyResponseRequestDto.builder()
        .memberId(user.getId())
        .replyId(modifyReplyId)
        .excuse(excuse)
        .replyTime(replyTime)
        .build();

    mockMvc.perform(patch("/v1/clerk/surveys/{surveyId}", survey.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(surveyResponseRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-reply-modify",
            pathParameters(
                parameterWithName("surveyId").description("응답을 수정할 설문의 Id")
            ),
            requestFields(
                fieldWithPath("memberId").description("응답하는 멤버의 ID"),
                fieldWithPath("replyId").description("수정할 응답"),
                fieldWithPath("excuse").description("휴면(기타) 응답일 경우 사유"),
                fieldWithPath("replyTime").description("응답 시간")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.surveyId").description("수정에 성공한 설문 ID"),
                fieldWithPath("data.memberId").description("응답자 멤버 ID"),
                fieldWithPath("data.replyId").description("수정에 성공한 설문 응답"),
                fieldWithPath("data.excuse").description("휴면(기타)응답일 경우 사유"),
                fieldWithPath("data.replyTime").description("설문 응답 시간")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 설문 정보 조회")
  public void getSurveyInformation() throws Exception {
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        true);
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(1L));

    SurveyInformationRequestDto surveyInformationRequestDto = SurveyInformationRequestDto.builder()
        .surveyId(survey.getId())
        .memberId(user.getId())
        .build();

    mockMvc.perform(get("/v1/clerk/surveys")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(surveyInformationRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("survey-information",
            requestFields(
                fieldWithPath("surveyId").description("조회하고자 하는 설문 ID"),
                fieldWithPath("memberId").description("조회하고자 하는 멤버 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.surveyId").description("조회한 설문 ID"),
                fieldWithPath("data.surveyName").description("조회한 설문 이름"),
                fieldWithPath("data.openTime").description("조회한 설문의 시작 시간"),
                fieldWithPath("data.closeTime").description("조회한 설문의 마감 시간"),
                fieldWithPath("data.isResponded").description("설문 응답 여부"),
                fieldWithPath("data.isVisible").description("설문 공개 여부"),
                fieldWithPath("data.reply").description("설문에 응답한 응답")
            )));
  }
}
