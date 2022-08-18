package keeper.project.homepage.repository.clerk;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyExcuseEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SurveyRepositoryTest extends SurveyTestHelper {

  @Test
  @DisplayName("설문 조사의 응답자 수 확인")
  public void getRespondentsInSurvey() {
    //given
    LocalDateTime openTime = LocalDateTime.of(2022, 8, 18, 0, 0, 0);
    LocalDateTime closeTime = LocalDateTime.of(2022, 8, 20, 0, 0, 0);
    SurveyEntity survey = generateSurvey(openTime, closeTime);
    MemberEntity member = memberRepository.getById(1L);
    SurveyReplyEntity reply = generateSurveyReply(
        SurveyReplyEntity.builder().id(1L).type("활동").build());

    generateSurveyMemberReply(survey, member, reply);

    em.flush();
    em.clear();

    //when
    SurveyEntity loadedSurvey = surveyRepository.getById(survey.getId());

    //then
    Assertions.assertThat(loadedSurvey.getRespondents().size()).isEqualTo(1);

  }

  @Test
  @DisplayName("설문 조사의 응답이 휴면(기타)일 경우")
  public void responseOtherDormant() {
    //given
    SurveyEntity survey = surveyRepository.getById(1L);
    MemberEntity member = memberRepository.getById(1L);
    SurveyReplyEntity reply = generateSurveyReply(
        SurveyReplyEntity.builder().id(3L).type("휴면(기타)").build());

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
