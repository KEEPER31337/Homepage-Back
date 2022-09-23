package keeper.project.homepage.clerk.controller;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.clerk.entity.SurveyEntity;
import keeper.project.homepage.clerk.entity.SurveyMemberReplyEntity;
import keeper.project.homepage.clerk.entity.SurveyReplyEntity;
import keeper.project.homepage.clerk.entity.SurveyReplyExcuseEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.clerk.repository.SurveyMemberReplyRepository;
import keeper.project.homepage.clerk.repository.SurveyReplyExcuseRepository;
import keeper.project.homepage.clerk.repository.SurveyReplyRepository;
import keeper.project.homepage.clerk.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.FieldDescriptor;

public class SurveySpringTestHelper extends ApiControllerTestHelper {

  @Autowired
  protected SurveyRepository surveyRepository;
  @Autowired
  protected SurveyMemberReplyRepository surveyMemberReplyRepository;
  @Autowired
  protected SurveyReplyRepository surveyReplyRepository;

  protected static final SurveyEntity NO_SURVEY = SurveyEntity.builder().id(-1L).build();

  @Autowired
  protected SurveyReplyExcuseRepository surveyReplyExcuseRepository;

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.registerModule(new JavaTimeModule()).writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected SurveyEntity generateSurvey(LocalDateTime openTime, LocalDateTime closeTime,
      Boolean isVisible) {
    final long epochTime = System.nanoTime();
    return surveyRepository.save(
        SurveyEntity.builder()
            .name("name_" + epochTime)
            .openTime(openTime)
            .closeTime(closeTime)
            .description("description_" + epochTime)
            .isVisible(isVisible)
            .build()
    );
  }

  protected SurveyMemberReplyEntity generateSurveyMemberReply(SurveyEntity survey,
      MemberEntity member,
      SurveyReplyEntity reply) {

    SurveyMemberReplyEntity respondent = SurveyMemberReplyEntity.builder()
        .member(member)
        .survey(survey)
        .reply(reply)
        .replyTime(LocalDateTime.now())
        .build();

    surveyMemberReplyRepository.save(respondent);
    survey.getRespondents().add(respondent);
    return respondent;
  }

  protected SurveyReplyExcuseEntity generateSurveyReplyExcuse(
      SurveyMemberReplyEntity surveyMemberReplyEntity, String because) {

    SurveyReplyExcuseEntity excuse = SurveyReplyExcuseEntity.builder()
        .surveyMemberReplyEntity(surveyMemberReplyEntity)
        .restExcuse(because)
        .build();

    surveyMemberReplyEntity.setSurveyReplyExcuseEntity(excuse);
    excuse.setSurveyMemberReplyEntity(surveyMemberReplyEntity);

    return surveyReplyExcuseRepository.save(excuse);

  }

  protected List<FieldDescriptor> generateSurveyRespondentDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".memberId").description("응답자 멤버 ID"),
        fieldWithPath(prefix + ".realName").description("실제 이름"),
        fieldWithPath(prefix + ".thumbnailPath").description("썸네일 경로"),
        fieldWithPath(prefix + ".generation").description("기수"),
        fieldWithPath(prefix + ".reply").description("응답"),
        fieldWithPath(prefix + ".excuse").description("응답이 휴면(기타)일 경우 사유")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateSurveyDtoResponseFields(
      ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".surveyId").description("조회한 설문 ID"),
        fieldWithPath(prefix + ".surveyName").description("설문 이름"),
        fieldWithPath(prefix + ".openTime").description("설문의 시작 시간"),
        fieldWithPath(prefix + ".closeTime").description("조설문의 마감 시간"),
        fieldWithPath(prefix + ".description").description("설문의 설명"),
        fieldWithPath(prefix + ".isVisible").description(
            "설문 공개 여부")
    ));
    if (type.equals(ResponseType.PAGE)) {
      commonFields.addAll(Arrays.asList(
          fieldWithPath("page.empty").description("페이지가 비었는 지 여부"),
          fieldWithPath("page.first").description("첫 페이지 인지"),
          fieldWithPath("page.last").description("마지막 페이지 인지"),
          fieldWithPath("page.number").description("요소를 가져 온 페이지 번호 (0부터 시작)"),
          fieldWithPath("page.numberOfElements").description("요소 개수"),
          subsectionWithPath("page.pageable").description("해당 페이지에 대한 DB 정보"),
          fieldWithPath("page.size").description("요청한 페이지 크기"),
          subsectionWithPath("page.sort").description("정렬에 대한 정보"),
          fieldWithPath("page.totalElements").description("총 요소 개수"),
          fieldWithPath("page.totalPages").description("총 페이지")));
    }
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateSurveyInformationDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".surveyId").description("조회한 설문 ID"),
        fieldWithPath(prefix + ".surveyName").description("설문 이름"),
        fieldWithPath(prefix + ".openTime").description("설문의 시작 시간"),
        fieldWithPath(prefix + ".closeTime").description("설문의 마감 시간"),
        fieldWithPath(prefix + ".description").description("설문의 설명"),
        fieldWithPath(prefix + ".isVisible").description("설문 공개 여부"),
        fieldWithPath(prefix + ".isResponded").description("설문 응답 여부"),
        fieldWithPath(prefix + ".replyId").description("설문에 응답한 응답 ID"),
        fieldWithPath(prefix + ".excuse").description("응답이 휴면(기타)일 경우 사유")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateSurveyModifyDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".surveyId").description("조회한 설문 ID"),
        fieldWithPath(prefix + ".surveyName").description("설문 이름"),
        fieldWithPath(prefix + ".openTime").description("설문의 시작 시간"),
        fieldWithPath(prefix + ".closeTime").description("설문의 마감 시간"),
        fieldWithPath(prefix + ".description").description("설문의 설명"),
        fieldWithPath(prefix + ".isVisible").description("설문 공개 여부"),
        subsectionWithPath(prefix + ".respondents").description("설문에 응답한 응답자 정보")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateSurveyUpdateDtoFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".surveyId").description("설문 ID"),
        fieldWithPath(prefix + ".surveyName").description("설문 이름"),
        fieldWithPath(prefix + ".isVisible").description("설문 공개 여부")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateCloseSurveyInformationDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".surveyId").description("가장 최근에 종료된 설문 ID"),
        fieldWithPath(prefix + ".surveyName").description("가장 최근에 종료된 설문 이름"),
        fieldWithPath(prefix + ".replyId").description("가장 최근에 종료된 설문에 응답한 응답")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateSurveyResponseModifyDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".surveyId").description("수정에 성공한 설문 ID"),
        fieldWithPath(prefix + ".memberId").description("응답자 멤버 ID"),
        fieldWithPath(prefix + ".replyId").description("수정에 성공한 설문 응답"),
        fieldWithPath(prefix + ".excuse").description("휴면(기타)응답일 경우 사유"),
        fieldWithPath(prefix + ".replyTime").description("설문 응답 시간")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }
}
