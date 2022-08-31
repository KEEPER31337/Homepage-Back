package keeper.project.homepage.user.service.clerk;

import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.GRADUATE;
import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.OTHER_DORMANT;
import static keeper.project.homepage.user.service.clerk.SurveyService.NO_SURVEY;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.admin.dto.clerk.response.ClosedSurveyInformationResponseDto;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SurveyServiceTest extends SurveySpringTestHelper {

  @Autowired
  private EntityManager em;

  @Autowired
  private SurveyService surveyService;

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
        surveyReplyRepository.getById(ACTIVITY.getId()));

    SurveyReplyEntity modifyResponse = surveyReplyRepository.getById(OTHER_DORMANT.getId());
    SurveyReplyExcuseEntity excuse = generateSurveyReplyExcuse(surveyMemberReplyEntity,
        "BOB로 인한 휴학");

    surveyMemberReplyEntity.modifyReply(modifyResponse);
    surveyMemberReplyEntity.setSurveyReplyExcuseEntity(excuse);

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
    assertThat(findSurvey.getDescription()).isEqualTo(survey.getDescription());
    assertThat(isResponded).isEqualTo(true);
    assertThat(survey.getIsVisible()).isEqualTo(true);
    assertThat(findMemberReply.getReply().getId()).isEqualTo(GRADUATE.getId());
  }

  @Test
  @DisplayName("가장 최근의 공개된 설문 조회 - 현재 진행중인")
  public void getLatestVisibleSurveyId() {
    //given
    generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(2),
        true);
    SurveyEntity expectSurvey = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(3),
        true);
    generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(3),
        false);

    //when
    Long resultId = surveyService.getLatestVisibleSurveyId();

    //then
    assertThat(resultId).isEqualTo(expectSurvey.getId());
  }

  @Test
  @DisplayName("가장 최근의 공개된 설문 조회 - 현재 진행중인 설문 없음")
  public void getLatestVisibleSurveyId_noOngoingSurveys() {
    //given
    generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().minusDays(1),
        true);
    generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(3),
        false);

    //when
    Long resultId = surveyService.getLatestVisibleSurveyId();

    //then
    assertThat(resultId).isEqualTo(NO_SURVEY.getId());
  }

  @Test
  @DisplayName("가장 최근에 종료된 설문의 정보와 요청자의 응답 여부 조회")
  public void getLatestClosedSurveyInformation() {
    //given
    setAuthentication(user);

    SurveyEntity expectSurvey = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().minusDays(2), true);
    SurveyMemberReplyEntity expectReply = generateSurveyMemberReply(expectSurvey, user,
        surveyReplyRepository.getById(ACTIVITY.getId()));
    SurveyEntity unExpectedSurvey = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(2), true);
    generateSurveyMemberReply(unExpectedSurvey, user,
        surveyReplyRepository.getById(GRADUATE.getId()));
    SurveyEntity unExpectedSurvey2 = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().minusDays(2), false);
    generateSurveyMemberReply(unExpectedSurvey2, user,
        surveyReplyRepository.getById(GRADUATE.getId()));

    //when
    ClosedSurveyInformationResponseDto result = surveyService.getLatestClosedSurveyInformation();

    //then
    assertThat(expectReply.getSurvey().getId()).isEqualTo(result.getSurveyId());
    assertThat(expectReply.getSurvey().getName()).isEqualTo(result.getSurveyName());
    assertThat(expectReply.getReply().getId()).isEqualTo(result.getReplyId());
  }

  @Test
  @DisplayName("가장 최근에 종료된 설문의 정보와 요청자의 응답 여부 조회 - 응답 하지 않음")
  public void getLatestClosedSurveyInformation_wrongMember() {
    //given
    setAuthentication(user);

    generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().minusDays(2), true);

    //when
    ClosedSurveyInformationResponseDto result = surveyService.getLatestClosedSurveyInformation();

    //then
    assertThat(ClosedSurveyInformationResponseDto.notFound().getSurveyId())
        .isEqualTo(result.getSurveyId());
    assertThat(ClosedSurveyInformationResponseDto.notFound().getSurveyName())
        .isEqualTo(result.getSurveyName());
    assertThat(ClosedSurveyInformationResponseDto.notFound().getReplyId())
        .isEqualTo(result.getReplyId());
  }


  @Test
  @DisplayName("가장 최근에 종료된 설문의 정보와 요청자의 응답 여부 조회 - 최근에 종료된 설문이 없음")
  public void getLatestClosedSurveyInformation_notFoundLatestClosedSurvey() {
    //given
    setAuthentication(user);

    SurveyEntity ongoingSurvey = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().plusDays(2), true);
    generateSurveyMemberReply(ongoingSurvey, user,
        surveyReplyRepository.getById(ACTIVITY.getId()));
    SurveyEntity notStartedSurvey = generateSurvey(LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(2), true);
    generateSurveyMemberReply(notStartedSurvey, user,
        surveyReplyRepository.getById(GRADUATE.getId()));
    SurveyEntity invisibleSurvey = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().minusDays(2), false);
    generateSurveyMemberReply(invisibleSurvey, user,
        surveyReplyRepository.getById(GRADUATE.getId()));

    //when
    ClosedSurveyInformationResponseDto result = surveyService.getLatestClosedSurveyInformation();

    //then
    assertThat(ClosedSurveyInformationResponseDto.notFound().getSurveyId())
        .isEqualTo(result.getSurveyId());
    assertThat(ClosedSurveyInformationResponseDto.notFound().getSurveyName())
        .isEqualTo(result.getSurveyName());
    assertThat(ClosedSurveyInformationResponseDto.notFound().getReplyId())
        .isEqualTo(result.getReplyId());
  }


  private void setAuthentication(MemberEntity reqMember) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(reqMember.getId(), reqMember.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회원"))));
  }
}
