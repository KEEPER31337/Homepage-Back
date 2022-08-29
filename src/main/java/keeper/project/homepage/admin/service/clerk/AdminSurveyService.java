package keeper.project.homepage.admin.service.clerk;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.admin.dto.clerk.request.AdminSurveyRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.AdminSurveyResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.ClosedSurveyInformationResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.DeleteSurveyResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SurveyRespondentResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SurveyUpdateResponseDto;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomSurveyMemberReplyNotFoundException;
import keeper.project.homepage.exception.clerk.CustomSurveyNotFoundException;
import keeper.project.homepage.repository.clerk.SurveyMemberReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyRepository;
import keeper.project.homepage.user.dto.clerk.response.SurveyInformationResponseDto;
import keeper.project.homepage.user.service.member.MemberUtilService;
import keeper.project.homepage.util.service.SurveyUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSurveyService {

  private final SurveyRepository surveyRepository;
  private final SurveyMemberReplyRepository surveyMemberReplyRepository;
  private final SurveyUtilService surveyUtilService;

  private final MemberUtilService memberUtilService;

  @Transactional
  public Long createSurvey(AdminSurveyRequestDto requestDto) {
    return surveyRepository.save(requestDto.toEntity()).getId();
  }

  @Transactional
  public DeleteSurveyResponseDto deleteSurvey(Long surveyId) {
    SurveyEntity survey = surveyUtilService.getSurveyById(surveyId);
    surveyMemberReplyRepository.deleteAll(survey.getRespondents());
    surveyRepository.delete(survey);
    return DeleteSurveyResponseDto.from(survey);
  }

  public List<SurveyRespondentResponseDto> getRespondents(Long surveyId) {
    List<SurveyMemberReplyEntity> respondents = surveyUtilService.getSurveyMemberReplyEntityById(
        surveyId);
    return respondents.stream()
        .map(SurveyRespondentResponseDto::from)
        .collect(Collectors.toList());
  }

  @Transactional
  public AdminSurveyResponseDto modifySurvey(Long surveyId,
      AdminSurveyRequestDto requestDto) {
    surveyUtilService.checkVirtualSurvey(surveyId);

    SurveyEntity survey = surveyRepository.findById(surveyId)
        .orElseThrow(CustomSurveyNotFoundException::new);

    survey.modifySurveyContents(requestDto.getSurveyName(), requestDto.getDescription(),
        requestDto.getOpenTime(), requestDto.getCloseTime(), requestDto.getIsVisible());

    return AdminSurveyResponseDto.from(surveyRepository.save(survey));

  }

  @Transactional
  public SurveyUpdateResponseDto openSurvey(Long surveyId) {
    SurveyEntity survey = surveyUtilService.getSurveyById(surveyId);
    survey.openSurvey();
    return SurveyUpdateResponseDto.from(survey);
  }

  @Transactional
  public SurveyUpdateResponseDto closeSurvey(Long surveyId) {
    SurveyEntity survey = surveyUtilService.getSurveyById(surveyId);
    survey.closeSurvey();
    return SurveyUpdateResponseDto.from(survey);
  }

  public SurveyInformationResponseDto getSurveyInformation(Long surveyId, Long memberId) {
    SurveyEntity survey = surveyRepository.findById(surveyId)
        .orElseThrow(CustomSurveyNotFoundException::new);
    MemberEntity member = memberUtilService.getById(memberId);

    Boolean isResponded = false;
    String reply = null;

    if (isUserRespondedSurvey(survey, member)) {
      isResponded = true;
      SurveyMemberReplyEntity surveyMemberReplyEntity = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
              surveyId, memberId)
          .orElseThrow(CustomSurveyMemberReplyNotFoundException::new);
      reply = surveyMemberReplyEntity.getReply()
          .getType();
    }

    return SurveyInformationResponseDto.from(survey, reply, isResponded);
  }

  private Boolean isUserRespondedSurvey(SurveyEntity survey, MemberEntity member) {
    return getSurveyRespondedMemberList(survey)
        .contains(member);
  }

  private List<MemberEntity> getSurveyRespondedMemberList(SurveyEntity survey) {
    return survey.getRespondents()
        .stream()
        .map(SurveyMemberReplyEntity::getMember)
        .toList();
  }

  public Long getLatestVisibleSurveyId() {
    LocalDateTime now = LocalDateTime.now();
    List<SurveyEntity> surveyList = surveyRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

    return findVisibleSurveyId(surveyList, now);
  }

  private Long findVisibleSurveyId(List<SurveyEntity> surveyList, LocalDateTime now) {
    Long visibleSurveyId = -1L;

    for (SurveyEntity survey : surveyList) {
      if (survey.getOpenTime().isBefore(now) && survey.getCloseTime().isAfter(now)
          && survey.getIsVisible()) {
        visibleSurveyId = survey.getId();
        break;
      }
    }
    return visibleSurveyId;
  }


  public ClosedSurveyInformationResponseDto getLatestClosedSurveyInformation(Long memberId) {
    LocalDateTime now = LocalDateTime.now();
    List<SurveyEntity> surveyList = surveyRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

    for (SurveyEntity survey : surveyList) {
      if (survey.getCloseTime().isBefore(now)) {
        SurveyMemberReplyEntity surveyMemberReply = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
                survey.getId(),memberId)
            .orElseThrow(CustomSurveyMemberReplyNotFoundException::new);
        return ClosedSurveyInformationResponseDto.from(survey, surveyMemberReply);
      }
    }
    return ClosedSurveyInformationResponseDto.notFound();
  }

  public Long getLatestInVisibleSurveyId() {
    LocalDateTime now = LocalDateTime.now();
    List<SurveyEntity> surveyList = surveyRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

    return findInVisibleSurveyId(surveyList, now);
  }

  private Long findInVisibleSurveyId(List<SurveyEntity> surveyList, LocalDateTime now) {
    Long inVisibleSurveyId = -1L;

    for (SurveyEntity survey : surveyList) {
      if (!survey.getIsVisible()) {
        inVisibleSurveyId = survey.getId();
        break;
      }
    }
    return inVisibleSurveyId;
  }
}
