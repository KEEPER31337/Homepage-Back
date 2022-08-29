package keeper.project.homepage.user.service.clerk;

import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.GRADUATE;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.OTHER_DORMANT;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.data.domain.Sort;
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
    SurveyMemberReplyEntity findMemberReply = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
            findSurvey.getId(), user.getId())
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
    generateSurveyMemberReply(survey1, user, surveyReplyRepository.getById(ACTIVITY.getId()));
    SurveyEntity survey2 = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(2),
        true);
    generateSurveyMemberReply(survey2, user, surveyReplyRepository.getById(GRADUATE.getId()));

    //when
    LocalDateTime now = LocalDateTime.now();
    List<SurveyEntity> surveyList = surveyRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

    SurveyMemberReplyEntity surveyMemberReply = null;

    for (SurveyEntity survey : surveyList) {
      if (survey.getCloseTime().isBefore(now)) {
        surveyMemberReply = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
                survey.getId(), user.getId())
            .orElseThrow(CustomSurveyMemberReplyNotFoundException::new);
        break;
      }
    }

    //then
    assertThat(surveyMemberReply.getSurvey().getId()).isEqualTo(survey1.getId());
    assertThat(surveyMemberReply.getSurvey().getName()).isEqualTo(survey1.getName());
    assertThat(surveyMemberReply.getReply().getId()).isEqualTo(ACTIVITY.getId());

  }
}
