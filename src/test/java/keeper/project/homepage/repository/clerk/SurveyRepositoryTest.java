package keeper.project.homepage.repository.clerk;


import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.OTHER_DORMANT;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyExcuseEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SurveyRepositoryTest extends SurveyRepositoryTestHelper {

  @Test
  @DisplayName("설문 조사의 응답자 수 확인")
  public void getRespondentsInSurvey() {
    //given
    SurveyEntity survey = surveyRepository.getById(1L);
    MemberEntity member = memberRepository.getById(1L);
    SurveyReplyEntity reply = surveyReplyRepository.getById(ACTIVITY.getId());

    generateSurveyMemberReply(survey, member, reply);

    em.flush();
    em.clear();

    //when
    SurveyEntity loadedSurvey = surveyRepository.getById(survey.getId());

    //then
    Assertions.assertThat(loadedSurvey.getRespondents().size()).isEqualTo(1);

  }

  @Test
  @DisplayName("설문 조사 시간 설정")
  public void surveyAvailable() {
    //given
    LocalDateTime openTime = LocalDateTime.now().minusDays(5);
    LocalDateTime closeTime = LocalDateTime.now().plusDays(5);
    SurveyEntity survey = generateSurvey(openTime, closeTime, true);
    MemberEntity member = memberRepository.getById(1L);
    SurveyReplyEntity reply = surveyReplyRepository.getById(ACTIVITY.getId());

    generateSurveyMemberReply(survey, member, reply);

    em.flush();
    em.clear();

    //when
    SurveyEntity loadedSurvey = surveyRepository.getById(survey.getId());

    //then
    Assertions.assertThat(LocalDateTime.now()).isAfter(openTime);
    Assertions.assertThat(LocalDateTime.now()).isBefore(closeTime);
    Assertions.assertThat(loadedSurvey.getIsVisible()).isEqualTo(true);

  }

  @Test
  @DisplayName("설문 조사의 응답이 휴면(기타)일 경우")
  public void responseOtherDormant() {
    //given
    SurveyEntity survey = surveyRepository.getById(1L);
    MemberEntity member = memberRepository.getById(1L);
    SurveyReplyEntity reply = surveyReplyRepository.getById(OTHER_DORMANT.getId()); // 휴면(기타)

    SurveyMemberReplyEntity surveyMemberReplyEntity = generateSurveyMemberReply(survey, member,
        reply);

    SurveyReplyExcuseEntity surveyReplyExcuseEntity = generateSurveyReplyExcuse(
        surveyMemberReplyEntity, "BOB");

    em.flush();
    em.clear();

    //when
    SurveyMemberReplyEntity find = surveyMemberReplyRepository.getById(
        surveyMemberReplyEntity.getId());

    //then
    Assertions.assertThat(surveyReplyExcuseEntity.getRestExcuse())
        .isEqualTo(find.getSurveyReplyExcuseEntity().getRestExcuse());
  }

}
