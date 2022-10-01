package keeper.project.homepage.clerk.service;

import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.GRADUATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.clerk.dto.request.AdminSurveyRequestDto;
import keeper.project.homepage.clerk.dto.response.SurveyRespondentResponseDto;
import keeper.project.homepage.clerk.dto.response.SurveyResponseDto;
import keeper.project.homepage.clerk.controller.SurveySpringTestHelper;
import keeper.project.homepage.clerk.entity.SurveyEntity;
import keeper.project.homepage.clerk.entity.SurveyMemberReplyEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AdminSurveyServiceTest extends SurveySpringTestHelper {

  @Autowired
  private EntityManager em;

  @Autowired
  private AdminSurveyService adminSurveyService;
  private MemberEntity user;
  private MemberEntity admin;

  private static final String SURVEY_NAME = "2022년 2학기 활동인원 조사";
  private static final String SURVEY_DESCRIPTION = "활동인원 조사입니다.";

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
  }

  @Test
  @DisplayName("설문 조사 개설")
  public void createSurvey() throws Exception {
    //given
    LocalDateTime openTime = LocalDateTime.now();
    LocalDateTime closeTime = LocalDateTime.now().plusDays(5);
    AdminSurveyRequestDto requestDto = AdminSurveyRequestDto.builder()
        .surveyName(SURVEY_NAME)
        .openTime(openTime)
        .closeTime(closeTime)
        .description(SURVEY_DESCRIPTION)
        .isVisible(true)
        .build();

    //when
    adminSurveyService.createSurvey(requestDto);
    List<SurveyEntity> all = surveyRepository.findAll();

    //then
    assertThat(all.size()).isEqualTo(2); // virtual data 포함
    assertThat(all.get(1).getName()).isEqualTo(SURVEY_NAME);
    assertThat(all.get(1).getOpenTime()).isEqualTo(openTime);
    assertThat(all.get(1).getCloseTime()).isEqualTo(closeTime);
    assertThat(all.get(1).getDescription()).isEqualTo(SURVEY_DESCRIPTION);
    assertThat(all.get(1).getIsVisible()).isEqualTo(true);
  }

  @Test
  @DisplayName("설문 삭제 - 응답자도 삭제")
  public void deleteSurvey() throws Exception {
    //given
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(ACTIVITY.getId()));

    //when
    adminSurveyService.deleteSurvey(survey.getId());
    List<SurveyEntity> surveys = surveyRepository.findAll();
    List<SurveyMemberReplyEntity> respondents = surveyMemberReplyRepository.findAll();

    //then
    assertThat(surveys.size()).isEqualTo(1); // virtual value
    assertThat(respondents.size()).isEqualTo(0);
  }

  @Test
  @DisplayName("설문 공개")
  public void openSurvey() throws Exception {
    //given
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        false);

    //when
    adminSurveyService.openSurvey(survey.getId());
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());

    //then
    assertThat(findSurvey.getIsVisible()).isEqualTo(true);
  }

  @Test
  @DisplayName("설문 비공개")
  public void closeSurvey() throws Exception {
    //given
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);

    //when
    adminSurveyService.closeSurvey(survey.getId());
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());

    //then
    assertThat(findSurvey.getIsVisible()).isEqualTo(false);
  }

  @Test
  @DisplayName("설문 응답자 조회")
  public void getSurveyRespondents() throws Exception {
    //given
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(GRADUATE.getId()));
    generateSurveyMemberReply(survey, admin, surveyReplyRepository.getById(ACTIVITY.getId()));

    //when
    List<SurveyRespondentResponseDto> respondents = adminSurveyService.getRespondents(
        survey.getId());

    //then
    assertThat(respondents.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("설문 목록 조회")
  public void getSurveyList() throws Exception {
    //given
    setAuthentication(admin);
    PageRequest pageable = PageRequest.of(0, 5, Sort.by("id").descending());

    Boolean isVisible = true;
    for (int i = 0; i < 7; i++) {
      SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
          isVisible);
      generateSurveyMemberReply(survey, admin, surveyReplyRepository.getById(ACTIVITY.getId()));
      isVisible = !isVisible;
    }

    //when
    Page<SurveyResponseDto> surveyList = adminSurveyService.getSurveyList(pageable);

    //then
    assertThat(surveyList.getTotalElements()).isEqualTo(7);
    assertThat(surveyList.getNumberOfElements()).isEqualTo(5);
  }

  private void setAuthentication(MemberEntity reqMember) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(reqMember.getId(), reqMember.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회장"))));
  }
}
