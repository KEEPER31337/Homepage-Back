package keeper.project.homepage.util.service;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyExcuseEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomSurveyNotFoundException;
import keeper.project.homepage.repository.clerk.SurveyMemberReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyExcuseRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SurveyUtilService {
  public static final Long VIRTUAL_SURVEY_ID = 1L;
  private final SurveyRepository surveyRepository;
  private final SurveyReplyRepository surveyReplyRepository;
  private final SurveyMemberReplyRepository surveyMemberReplyRepository;
  private final SurveyReplyExcuseRepository surveyReplyExcuseRepository;

  public SurveyEntity getSurveyById(Long surveyId){
    return surveyRepository.findById(surveyId)
        .orElseThrow();// 찾을 수 없을 때는 오류 띄우기.
  }

  public SurveyReplyEntity getReplyById(Long replyId){
    return surveyReplyRepository.getById(replyId);
  }
  public void checkVirtualSurvey(Long surveyId){
    if (VIRTUAL_SURVEY_ID.equals(surveyId)){
      throw new CustomSurveyNotFoundException();
    }
  }
  public SurveyReplyExcuseEntity generateSurveyReplyExcuse(
      SurveyMemberReplyEntity surveyMemberReplyEntity, String because) {
    SurveyReplyExcuseEntity excuse = SurveyReplyExcuseEntity.builder()
        .surveyMemberReplyEntity(surveyMemberReplyEntity)
        .restExcuse(because)
        .build();

    surveyMemberReplyEntity.setSurveyReplyExcuseEntity(excuse);

    return surveyReplyExcuseRepository.save(excuse);

  }

  public List<SurveyMemberReplyEntity> getSurveyMemberReplyEntityById(Long surveyId) {
    return surveyMemberReplyRepository.findAllBySurveyId(
        surveyId);
  }

  public SurveyMemberReplyEntity generateSurveyMemberReplyEntity(MemberEntity member,
      SurveyEntity survey, SurveyReplyEntity reply) {
    return SurveyMemberReplyEntity.builder()
        .member(member)
        .survey(survey)
        .reply(reply)
        .replyTime(LocalDateTime.now())
        .build();
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
