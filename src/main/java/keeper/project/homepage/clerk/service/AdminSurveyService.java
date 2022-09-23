package keeper.project.homepage.clerk.service;

import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.clerk.dto.request.AdminSurveyRequestDto;
import keeper.project.homepage.clerk.dto.response.AdminSurveyResponseDto;
import keeper.project.homepage.clerk.dto.response.DeleteSurveyResponseDto;
import keeper.project.homepage.clerk.dto.response.SurveyRespondentResponseDto;
import keeper.project.homepage.clerk.dto.response.SurveyResponseDto;
import keeper.project.homepage.clerk.dto.response.SurveyUpdateResponseDto;
import keeper.project.homepage.clerk.entity.SurveyEntity;
import keeper.project.homepage.clerk.entity.SurveyMemberReplyEntity;
import keeper.project.homepage.clerk.exception.CustomSurveyNotFoundException;
import keeper.project.homepage.clerk.repository.SurveyMemberReplyRepository;
import keeper.project.homepage.clerk.repository.SurveyRepository;
import keeper.project.homepage.member.service.MemberUtilService;
import keeper.project.homepage.util.service.SurveyUtilService;
import keeper.project.homepage.util.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final AuthService authService;

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
    List<SurveyMemberReplyEntity> respondents = surveyUtilService.getSurveyMemberReplyEntityBySurveyId(
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

  public Page<SurveyResponseDto> getSurveyList(Pageable pageable) {
    return surveyRepository.findAllByIdIsNot(
        SurveyUtilService.VIRTUAL_SURVEY_ID, pageable).map(SurveyResponseDto::from);
  }
}
