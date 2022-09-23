package keeper.project.homepage.clerk.service;

import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.GRADUATE;
import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.OTHER_DORMANT;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.DORMANT_MEMBER;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.GRADUATED_MEMBER;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.REGULAR_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.clerk.dto.response.ClosedSurveyInformationResponseDto;
import keeper.project.homepage.clerk.service.SurveyService;
import keeper.project.homepage.clerk.controller.SurveySpringTestHelper;
import keeper.project.homepage.clerk.entity.SurveyEntity;
import keeper.project.homepage.clerk.entity.SurveyMemberReplyEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.clerk.exception.CustomSurveyMemberReplyNotFoundException;
import keeper.project.homepage.clerk.dto.request.SurveyResponseRequestDto;
import keeper.project.homepage.clerk.dto.response.SurveyInformationResponseDto;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.member.service.MemberUtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
  @Autowired
  private MemberUtilService memberUtilService;

  private MemberEntity user;
  private MemberEntity admin;

  private static final String REPLY_EXCUSE_1 = "BOB로 인한 휴학";
  private static final String REPLY_EXCUSE_2 = "개인사정";

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
  }

  @Test
  @DisplayName("설문 응답")
  public void responseSurvey() throws Exception {
    //given
    setAuthentication(user);
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);

    SurveyResponseRequestDto requestDto = SurveyResponseRequestDto.builder()
        .replyId(ACTIVITY.getId())
        .excuse(null)
        .build();

    //when
    surveyService.responseSurvey(survey.getId(), requestDto);
    SurveyMemberReplyEntity surveyMemberReplyEntity = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
        survey.getId(), user.getId()).orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(surveyMemberReplyEntity.getReply().getId()).isEqualTo(ACTIVITY.getId());
  }

  @Test
  @DisplayName("설문 응답시 멤버의 활동 상태 타입도 갱신")
  public void updateMemberType() throws Exception {
    //given
    setAuthentication(user);
    MemberTypeEntity type = memberUtilService.getTypeById(DORMANT_MEMBER.getId());
    user.changeMemberType(type);

    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);

    SurveyResponseRequestDto requestDto = SurveyResponseRequestDto.builder()
        .replyId(ACTIVITY.getId())
        .excuse(null)
        .build();

    //when
    surveyService.responseSurvey(survey.getId(), requestDto);

    //then
    assertThat(user.getMemberType().getId()).isEqualTo(REGULAR_MEMBER.getId());
  }

  @Test
  @DisplayName("설문 응답 - 휴면(기타) 응답")
  public void responseSurvey_withOther() throws Exception {
    //given
    setAuthentication(user);
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);

    SurveyResponseRequestDto requestDto = SurveyResponseRequestDto.builder()
        .replyId(OTHER_DORMANT.getId())
        .excuse(REPLY_EXCUSE_1)
        .build();

    //when
    surveyService.responseSurvey(survey.getId(), requestDto);
    SurveyMemberReplyEntity surveyMemberReplyEntity = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
        survey.getId(), user.getId()).orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(surveyMemberReplyEntity.getReply().getId()).isEqualTo(OTHER_DORMANT.getId());
    assertThat(surveyMemberReplyEntity.getSurveyReplyExcuseEntity().getRestExcuse()).isEqualTo(
        REPLY_EXCUSE_1);
  }

  @Test
  @DisplayName("설문 응답 수정 - 기타 X -> 기타 X")
  public void modifyResponse_noOtherTo_noOther() throws Exception {
    //given
    setAuthentication(user);
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(ACTIVITY.getId()));

    SurveyResponseRequestDto requestDto = SurveyResponseRequestDto.builder()
        .replyId(GRADUATE.getId())
        .excuse(null)
        .build();

    //when
    surveyService.modifyResponse(survey.getId(), requestDto);
    SurveyMemberReplyEntity surveyMemberReplyEntity = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
        survey.getId(), user.getId()).orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(surveyMemberReplyEntity.getReply().getId()).isEqualTo(
        surveyReplyRepository.getById(GRADUATE.getId()).getId());
    assertThat(surveyMemberReplyEntity.getSurveyReplyExcuseEntity()).isNull();
  }

  @Test
  @DisplayName("설문 응답 수정 - 기타 X -> 기타")
  public void modifyResponse_noOtherTo_Other() throws Exception {
    //given
    setAuthentication(user);
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(ACTIVITY.getId()));

    SurveyResponseRequestDto requestDto = SurveyResponseRequestDto.builder()
        .replyId(OTHER_DORMANT.getId())
        .excuse(REPLY_EXCUSE_1)
        .build();

    //when
    surveyService.modifyResponse(survey.getId(), requestDto);
    SurveyMemberReplyEntity surveyMemberReplyEntity = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
        survey.getId(), user.getId()).orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(surveyMemberReplyEntity.getReply().getId()).isEqualTo(
        surveyReplyRepository.getById(OTHER_DORMANT.getId()).getId());
    assertThat(surveyMemberReplyEntity.getSurveyReplyExcuseEntity().getRestExcuse()).isEqualTo(
        requestDto.getExcuse());
  }

  @Test
  @DisplayName("설문 응답 수정 - 기타 -> 기타X")
  public void modifyResponse_OtherTo_noOther() throws Exception {
    //given
    setAuthentication(user);
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);
    SurveyMemberReplyEntity surveyMemberReplyEntity1 = generateSurveyMemberReply(survey, user,
        surveyReplyRepository.getById(OTHER_DORMANT.getId()));
    generateSurveyReplyExcuse(surveyMemberReplyEntity1, REPLY_EXCUSE_2);

    SurveyResponseRequestDto requestDto = SurveyResponseRequestDto.builder()
        .replyId(ACTIVITY.getId())
        .excuse(null)
        .build();

    //when
    surveyService.modifyResponse(survey.getId(), requestDto);
    SurveyMemberReplyEntity surveyMemberReplyEntity2 = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
        survey.getId(), user.getId()).orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(surveyMemberReplyEntity2.getReply().getId()).isEqualTo(
        surveyReplyRepository.getById(ACTIVITY.getId()).getId());
    assertThat(surveyMemberReplyEntity2.getSurveyReplyExcuseEntity()).isNull();
  }

  @Test
  @DisplayName("설문 응답 수정 - 기타 -> 기타")
  public void modifyResponse_OtherTo_Other() throws Exception {
    //given
    setAuthentication(user);
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);
    SurveyMemberReplyEntity surveyMemberReplyEntity1 = generateSurveyMemberReply(survey, user,
        surveyReplyRepository.getById(OTHER_DORMANT.getId()));
    generateSurveyReplyExcuse(surveyMemberReplyEntity1, REPLY_EXCUSE_2);

    SurveyResponseRequestDto requestDto = SurveyResponseRequestDto.builder()
        .replyId(OTHER_DORMANT.getId())
        .excuse(REPLY_EXCUSE_1)
        .build();

    //when
    surveyService.modifyResponse(survey.getId(), requestDto);
    SurveyMemberReplyEntity surveyMemberReplyEntity2 = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
        survey.getId(), user.getId()).orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(surveyMemberReplyEntity2.getReply().getId()).isEqualTo(
        surveyReplyRepository.getById(OTHER_DORMANT.getId()).getId());
    assertThat(surveyMemberReplyEntity2.getSurveyReplyExcuseEntity().getRestExcuse()).isEqualTo(
        requestDto.getExcuse());
  }

  @Test
  @DisplayName("설문 응답 수정 시 멤버의 활동 상태 타입도 갱신")
  public void updateMemberTypeWhenModifyResponse() throws Exception {
    //given
    setAuthentication(user);
    MemberTypeEntity type = memberUtilService.getTypeById(REGULAR_MEMBER.getId());
    user.changeMemberType(type);

    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(ACTIVITY.getId()));

    SurveyResponseRequestDto requestDto = SurveyResponseRequestDto.builder()
        .replyId(GRADUATE.getId())
        .excuse(null)
        .build();

    //when
    surveyService.modifyResponse(survey.getId(), requestDto);

    //then
    assertThat(user.getMemberType().getId()).isEqualTo(GRADUATED_MEMBER.getId());
  }


  @Test
  @DisplayName("설문 정보 조회")
  public void getSurveyInformation() throws Exception {
    //given
    setAuthentication(user);
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);
    generateSurveyMemberReply(survey, user, surveyReplyRepository.getById(ACTIVITY.getId()));

    //when
    SurveyInformationResponseDto responseDto = surveyService.getSurveyInformation(
        survey.getId());
    SurveyMemberReplyEntity surveyMemberReplyEntity = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
        survey.getId(), user.getId()).orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(responseDto.getSurveyId()).isEqualTo(survey.getId());
    assertThat(responseDto.getIsResponded()).isEqualTo(true);
    assertThat(responseDto.getReplyId()).isEqualTo(ACTIVITY.getId());
  }

  @Test
  @DisplayName("설문 정보 조회 - 응답을 안 했을 경우")
  public void getSurveyInformation_noReply() throws Exception {
    //given
    setAuthentication(user);
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);

    //when
    SurveyInformationResponseDto responseDto = surveyService.getSurveyInformation(
        survey.getId());

    //then
    assertThat(responseDto.getSurveyId()).isEqualTo(survey.getId());
    assertThat(responseDto.getIsResponded()).isEqualTo(false);
  }

  @Test
  @DisplayName("설문 정보 조회 - 휴면(기타) 응답")
  public void getSurveyInformation_withExcuse() throws Exception {
    //given
    setAuthentication(user);
    SurveyEntity survey = generateSurvey(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
        true);
    SurveyMemberReplyEntity surveyMemberReplyEntity = generateSurveyMemberReply(survey, user,
        surveyReplyRepository.getById(OTHER_DORMANT.getId()));
    generateSurveyReplyExcuse(surveyMemberReplyEntity, REPLY_EXCUSE_1);

    //when
    SurveyInformationResponseDto responseDto = surveyService.getSurveyInformation(
        survey.getId());
    SurveyMemberReplyEntity surveyMemberReply = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
        survey.getId(), user.getId()).orElseThrow(CustomSurveyMemberReplyNotFoundException::new);

    //then
    assertThat(responseDto.getSurveyId()).isEqualTo(survey.getId());
    assertThat(responseDto.getIsResponded()).isEqualTo(true);
    assertThat(responseDto.getReplyId()).isEqualTo(OTHER_DORMANT.getId());
    assertThat(responseDto.getExcuse()).isEqualTo(REPLY_EXCUSE_1);
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

    SurveyEntity survey = generateSurvey(LocalDateTime.now().minusDays(4),
        LocalDateTime.now().minusDays(2), true);

    //when
    ClosedSurveyInformationResponseDto result = surveyService.getLatestClosedSurveyInformation();

    //then
    assertThat(ClosedSurveyInformationResponseDto.of(survey,null).getSurveyId())
        .isEqualTo(result.getSurveyId());
    assertThat(ClosedSurveyInformationResponseDto.of(survey,null).getSurveyName())
        .isEqualTo(result.getSurveyName());
    assertThat(ClosedSurveyInformationResponseDto.of(survey,null).getReplyId())
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
    assertThat(NO_SURVEY.getId())
        .isEqualTo(result.getSurveyId());
    assertThat(NO_SURVEY.getName())
        .isEqualTo(result.getSurveyName());
    assertThat(result.getReplyId()).isNull();
  }


  private void setAuthentication(MemberEntity reqMember) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(reqMember.getId(), reqMember.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회원"))));
  }
}
