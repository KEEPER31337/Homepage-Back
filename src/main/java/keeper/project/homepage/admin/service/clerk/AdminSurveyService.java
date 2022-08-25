package keeper.project.homepage.admin.service.clerk;

import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.admin.dto.clerk.request.AdminSurveyRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.AdminSurveyResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.DeleteSurveyResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SurveyRespondentResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SurveyUpdateResponseDto;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.exception.clerk.CustomSurveyNotFoundException;
import keeper.project.homepage.repository.clerk.SurveyMemberReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyRepository;
import keeper.project.homepage.util.service.SurveyUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Transactional
  public Long createSurvey(AdminSurveyRequestDto requestDto) {
    return surveyRepository.save(requestDto.toEntity()).getId();
  }

  @Transactional
  public DeleteSurveyResponseDto deleteSurvey(Long surveyId) {
    SurveyEntity survey = surveyUtilService.getSurveyById(surveyId);
    surveyMemberReplyRepository.deleteAllInBatch(survey.getRespondents());
    surveyRepository.delete(survey);
    return DeleteSurveyResponseDto.from(survey);
  }

  public List<SurveyRespondentResponseDto> getRespondents(Long surveyId) {
    List<SurveyMemberReplyEntity> respondents = surveyUtilService.getSurveyMemberReplyEntityById(
        surveyId);
    return respondents.stream().map(SurveyRespondentResponseDto::from).collect(Collectors.toList());
  }

  @Transactional
  public AdminSurveyResponseDto modifySurvey(Long surveyId,
      AdminSurveyRequestDto requestDto) {
    surveyUtilService.checkVirtualSurvey(surveyId);

    SurveyEntity survey = surveyRepository.findById(surveyId)
        .orElseThrow(CustomSurveyNotFoundException::new);

    survey.setName(requestDto.getSurveyName());
    survey.setDescription(requestDto.getDescription());
    survey.setOpenTime(requestDto.getOpenTime());
    survey.setCloseTime(requestDto.getCloseTime());
    survey.setIsVisible(requestDto.getIsVisible());

    return AdminSurveyResponseDto.toDto(surveyRepository.save(survey));

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

}
