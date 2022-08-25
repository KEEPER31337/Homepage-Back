package keeper.project.homepage.controller.clerk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyExcuseEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.clerk.SurveyMemberReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyExcuseRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class SurveySpringTestHelper extends ApiControllerTestHelper {

  @Autowired
  protected SurveyRepository surveyRepository;
  @Autowired
  protected SurveyMemberReplyRepository surveyMemberReplyRepository;
  @Autowired
  protected SurveyReplyRepository surveyReplyRepository;

  @Autowired
  protected SurveyReplyExcuseRepository surveyReplyExcuseRepository;

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
      return mapper.writeValueAsString(obj);
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

    return surveyReplyExcuseRepository.save(excuse);

  }

  public enum Reply{
    ACTIVITY(1L),
    MILITARY_DORMANT(2L),
    OTHER_DORMANT(3L),
    GRADUATE(4L),
    LEAVE(5L);

    private Long replyId;

    Reply(Long replyId){
      this.replyId = replyId;
    }

    public Long getReplyId(){
      return this.replyId;
    }
  }

}
