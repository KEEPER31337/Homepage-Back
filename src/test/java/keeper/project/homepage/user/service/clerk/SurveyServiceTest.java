package keeper.project.homepage.user.service.clerk;

import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.GRADUATE;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.OTHER_DORMANT;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import keeper.project.homepage.controller.clerk.SurveySpringTestHelper;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyExcuseEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomSurveyMemberReplyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SurveyServiceTest extends SurveySpringTestHelper {

  @Autowired
  private EntityManager em;

  private MemberEntity user;
  private MemberEntity admin;

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
  }

  @Test
  @DisplayName("설문 응답")
  public void responseSurvey() throws Exception {
    //given
    SurveyEntity survey = surveyRepository.getById(1L);
    SurveyMemberReplyEntity surveyMemberReplyEntity = generateSurveyMemberReply(survey, user,
        surveyReplyRepository.getById(GRADUATE.getId()));

    //when
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());

    //then
    assertThat(findSurvey.getRespondents()).contains(surveyMemberReplyEntity);
  }

  @Test
  @DisplayName("설문 응답 수정")
  public void modifyResponse() throws Exception {
    //given
    SurveyEntity survey = surveyRepository.getById(1L);
    SurveyMemberReplyEntity surveyMemberReplyEntity = generateSurveyMemberReply(survey, user,
        surveyReplyRepository.getById(GRADUATE.getId()));

    SurveyReplyEntity modifyResponse = surveyReplyRepository.getById(OTHER_DORMANT.getId());
    SurveyReplyExcuseEntity excuse = generateSurveyReplyExcuse(surveyMemberReplyEntity,
        "BOB로 인한 휴학");

    surveyMemberReplyEntity.modifyReply(modifyResponse);
    surveyMemberReplyEntity.assignSurveyReplyExcuseEntity(excuse);

    //when
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());

    //then
    assertThat(findSurvey.getRespondents()).contains(surveyMemberReplyEntity);
    assertThat(surveyMemberReplyEntity.getReply().getId()).isEqualTo(OTHER_DORMANT.getId());
    assertThat(surveyMemberReplyEntity.getSurveyReplyExcuseEntity().getRestExcuse()).isEqualTo(
        "BOB로 인한 휴학");
  }

  @Test
  @DisplayName("설문 정보 조회")
  public void getSurveyInformation() throws Exception {
    //given
    LocalDateTime openTime = LocalDateTime.now();
    LocalDateTime closeTime = LocalDateTime.now().plusDays(5);
    SurveyEntity survey = generateSurvey(openTime, closeTime, true);
    SurveyMemberReplyEntity surveyMemberReplyEntity = generateSurveyMemberReply(survey, user,
        surveyReplyRepository.getById(GRADUATE.getId()));

    Boolean isResponded = false;

    if (survey.getRespondents().contains(surveyMemberReplyEntity)) {
      isResponded = true;
    }

    //when
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());
    SurveyMemberReplyEntity findMemberReply = surveyMemberReplyRepository.findByMemberId(
            user.getId())
        .orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(findSurvey.getId()).isEqualTo(survey.getId());
    assertThat(findSurvey.getName()).isEqualTo(survey.getName());
    assertThat(findSurvey.getOpenTime()).isEqualTo(openTime);
    assertThat(findSurvey.getCloseTime()).isEqualTo(closeTime);
    assertThat(isResponded).isEqualTo(true);
    assertThat(survey.getIsVisible()).isEqualTo(true);
    assertThat(findMemberReply.getReply().getId()).isEqualTo(GRADUATE.getId());
  }
}
