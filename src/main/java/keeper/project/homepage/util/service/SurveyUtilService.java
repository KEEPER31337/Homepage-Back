package keeper.project.homepage.util.service;

import java.util.List;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.exception.clerk.CustomSurveyMemberReplyNotFoundException;
import keeper.project.homepage.exception.clerk.CustomSurveyNotFoundException;
import keeper.project.homepage.repository.clerk.SurveyMemberReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyExcuseRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SurveyUtilService {

  public static final Long VIRTUAL_SURVEY_ID = 1L;
  private final SurveyRepository surveyRepository;
  private final SurveyReplyRepository surveyReplyRepository;
  private final SurveyMemberReplyRepository surveyMemberReplyRepository;
  private final SurveyReplyExcuseRepository surveyReplyExcuseRepository;

  public static final SurveyEntity NO_SURVEY = SurveyEntity.builder().id(-1L).build();

  public SurveyEntity getSurveyById(Long surveyId) {
    return surveyRepository.findById(surveyId)
        .orElseThrow(CustomSurveyNotFoundException::new);
  }

  public void checkVirtualSurvey(Long surveyId) {
    if (VIRTUAL_SURVEY_ID.equals(surveyId)) {
      throw new CustomSurveyNotFoundException();
    }
  }

  public List<SurveyMemberReplyEntity> getSurveyMemberReplyEntityBySurveyId(Long surveyId) {
    return surveyMemberReplyRepository.findAllBySurveyId(surveyId)
        .orElseThrow(CustomSurveyMemberReplyNotFoundException::new);
  }

}
