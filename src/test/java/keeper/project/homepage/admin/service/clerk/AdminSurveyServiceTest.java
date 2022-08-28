package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.GRADUATE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.controller.clerk.SurveySpringTestHelper;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AdminSurveyServiceTest extends SurveySpringTestHelper {

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
  @DisplayName("설문 조사 개설")
  public void createSurvey() throws Exception {
    //given
    SurveyEntity survey = surveyRepository.save(
        SurveyEntity.builder()
            .name("2022년 2학기 활동인원 조사")
            .description("활동인원 조사입니다.")
            .openTime(LocalDateTime.now())
            .closeTime(LocalDateTime.now().plusDays(5))
            .isVisible(true)
            .build()
    );

    //when
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());

    //then
    assertThat(findSurvey.getId()).isEqualTo(survey.getId());
  }

  @Test
  @DisplayName("설문 삭제 - 응답자도 삭제")
  public void deleteSurvey() throws Exception {
    //given
    SurveyEntity survey = surveyRepository.save(
        SurveyEntity.builder()
            .name("2022년 2학기 활동인원 조사")
            .description("활동인원 조사입니다.")
            .openTime(LocalDateTime.now())
            .closeTime(LocalDateTime.now().plusDays(5))
            .isVisible(true)
            .build()
    );
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(ACTIVITY.getId()));

    //when
    surveyRepository.delete(survey);

    em.flush();
    em.clear();

    //then
    List<SurveyEntity> surveys = surveyRepository.findAll();
    List<SurveyMemberReplyEntity> respondents = surveyMemberReplyRepository.findAll();

    assertThat(surveys.size()).isEqualTo(1); // virtual value
    assertThat(respondents.size()).isEqualTo(0);
  }

  @Test
  @DisplayName("설문 공개")
  public void openSurvey() throws Exception {
    //given
    SurveyEntity survey = surveyRepository.save(
        SurveyEntity.builder()
            .name("2022년 2학기 활동인원 조사")
            .description("활동인원 조사입니다.")
            .openTime(LocalDateTime.now())
            .closeTime(LocalDateTime.now().plusDays(5))
            .isVisible(false)
            .build()
    );

    //when
    survey.openSurvey();
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());

    //then
    assertThat(findSurvey.getIsVisible()).isEqualTo(true);
  }

  @Test
  @DisplayName("설문 비공개")
  public void closeSurvey() throws Exception {
    //given
    SurveyEntity survey = surveyRepository.save(
        SurveyEntity.builder()
            .name("2022년 2학기 활동인원 조사")
            .description("활동인원 조사입니다.")
            .openTime(LocalDateTime.now())
            .closeTime(LocalDateTime.now().plusDays(5))
            .isVisible(true)
            .build()
    );

    //when
    survey.closeSurvey();
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());

    //then
    assertThat(findSurvey.getIsVisible()).isEqualTo(false);
  }

  @Test
  @DisplayName("설문 응답자 조회")
  public void getSurveyRespondents() throws Exception {
    //given
    SurveyEntity survey = surveyRepository.save(
        SurveyEntity.builder()
            .name("2022년 2학기 활동인원 조사")
            .description("활동인원 조사입니다.")
            .openTime(LocalDateTime.now())
            .closeTime(LocalDateTime.now().plusDays(5))
            .isVisible(true)
            .build()
    );
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(GRADUATE.getId()));
    generateSurveyMemberReply(survey, admin, surveyReplyRepository.getById(ACTIVITY.getId()));

    //when
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());
    List<SurveyMemberReplyEntity> respondents = findSurvey.getRespondents();

    //then
    assertThat(respondents.size()).isEqualTo(2);
  }
}
