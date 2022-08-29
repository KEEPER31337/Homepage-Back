package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.GRADUATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.admin.dto.clerk.response.ClosedSurveyInformationResponseDto;
import keeper.project.homepage.controller.clerk.SurveySpringTestHelper;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomSurveyMemberReplyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

  @Test
  @DisplayName("설문 정보 조회")
  public void getSurveyInformation() throws Exception {
    //given
    LocalDateTime openTime = LocalDateTime.now();
    LocalDateTime closeTime = LocalDateTime.now().plusDays(5);
    SurveyEntity survey = generateSurvey(openTime, closeTime, true);
    SurveyMemberReplyEntity surveyMemberReplyEntity = generateSurveyMemberReply(survey, admin,
        surveyReplyRepository.getById(GRADUATE.getId()));

    Boolean isResponded = false;

    if (survey.getRespondents().contains(surveyMemberReplyEntity)) {
      isResponded = true;
    }

    //when
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());
    SurveyMemberReplyEntity findMemberReply = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
            findSurvey.getId(), admin.getId())
        .orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(findSurvey.getId()).isEqualTo(survey.getId());
    assertThat(findSurvey.getName()).isEqualTo(survey.getName());
    assertThat(findSurvey.getOpenTime()).isEqualTo(openTime);
    assertThat(findSurvey.getCloseTime()).isEqualTo(closeTime);
    assertThat(findSurvey.getDescription()).isEqualTo(survey.getDescription());
    assertThat(isResponded).isEqualTo(true);
    assertThat(survey.getIsVisible()).isEqualTo(true);
    assertThat(findMemberReply.getReply().getId()).isEqualTo(GRADUATE.getId());
  }

  @Test
  @DisplayName("가장 최근의 공개된 설문 조회 - 현재 진행중인")
  public void getLatestVisibleSurveyId() throws Exception {
    //given
    SurveyEntity survey1 = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(2),
        true);
    SurveyEntity survey2 = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(2),
        false);

    //when
    LocalDateTime now = LocalDateTime.now();
    List<SurveyEntity> surveyList = surveyRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

    Long latestVisibleSurveyId = findVisibleSurveyId(surveyList, now);

    //then
    assertThat(latestVisibleSurveyId).isEqualTo(survey1.getId());
  }

  @Test
  @DisplayName("가장 최근에 종료된 설문의 정보 조회")
  public void getLatestClosedSurveyInformation() throws Exception {
    //given
    SurveyEntity survey1 = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().minusDays(2),
        true);
    generateSurveyMemberReply(survey1, admin, surveyReplyRepository.getById(ACTIVITY.getId()));
    SurveyEntity survey2 = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(2),
        true);
    generateSurveyMemberReply(survey2, admin, surveyReplyRepository.getById(GRADUATE.getId()));

    //when
    LocalDateTime now = LocalDateTime.now();
    List<SurveyEntity> surveyList = surveyRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

    SurveyMemberReplyEntity surveyMemberReply = null;

    for (SurveyEntity survey : surveyList) {
      if (survey.getCloseTime().isBefore(now)) {
        surveyMemberReply = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
                survey.getId(), admin.getId())
            .orElseThrow(CustomSurveyMemberReplyNotFoundException::new);
        break;
      }
    }

    //then
    assertThat(surveyMemberReply.getSurvey().getId()).isEqualTo(survey1.getId());
    assertThat(surveyMemberReply.getSurvey().getName()).isEqualTo(survey1.getName());
    assertThat(surveyMemberReply.getReply().getId()).isEqualTo(ACTIVITY.getId());

  }

  @Test
  @DisplayName("가장 최근의 비공개 상태의 설문 조회")
  public void getLatestInVisibleSurveyId() throws Exception {
    //given
    SurveyEntity survey1 = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(2),
        false);
    SurveyEntity survey2 = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(2),
        true);

    //when
    LocalDateTime now = LocalDateTime.now();
    List<SurveyEntity> surveyList = surveyRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

    Long latestInVisibleSurveyId = findInVisibleSurveyId(surveyList, now);

    //then
    assertThat(latestInVisibleSurveyId).isEqualTo(survey1.getId());
  }
}
