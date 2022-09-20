package keeper.project.homepage.user.controller.clerk;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.user.dto.clerk.response.ClosedSurveyInformationResponseDto;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.user.dto.clerk.response.SurveyModifyResponseDto;
import keeper.project.homepage.user.dto.clerk.request.SurveyResponseRequestDto;
import keeper.project.homepage.user.dto.clerk.response.SurveyInformationResponseDto;
import keeper.project.homepage.user.service.clerk.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Secured("ROLE_회원")
@RequestMapping("/v1/clerk/surveys")
public class SurveyController {

  private final ResponseService responseService;

  private final SurveyService surveyService;

  @PostMapping("/{surveyId}")
  public SingleResult<Long> responseSurvey(
      @PathVariable("surveyId") @NotNull Long surveyId,
      @RequestBody @Valid SurveyResponseRequestDto responseRequestDto
  ) {
    return responseService.getSuccessSingleResult(
        surveyService.responseSurvey(surveyId, responseRequestDto));
  }

  @PatchMapping("/{surveyId}")
  public SingleResult<SurveyModifyResponseDto> modifyResponse(
      @PathVariable("surveyId") @NotNull Long surveyId,
      @RequestBody @Valid SurveyResponseRequestDto responseRequestDto
  ) {
    return responseService.getSuccessSingleResult(
        surveyService.modifyResponse(surveyId, responseRequestDto));
  }

  @GetMapping("/information/{surveyId}")
  public SingleResult<SurveyInformationResponseDto> getSurveyInformation(
      @PathVariable("surveyId") @NotNull Long surveyId
  ) {
    return responseService.getSuccessSingleResult(
        surveyService.getSurveyInformation(surveyId));
  }

  @GetMapping("/visible/ongoing")
  public SingleResult<Long> getLatestVisibleSurveyId() {
    return responseService.getSuccessSingleResult(surveyService.getLatestVisibleSurveyId());
  }

  @GetMapping("/visible/closed")
  public SingleResult<ClosedSurveyInformationResponseDto> getLatestClosedSurveyInformation() {
    return responseService.getSuccessSingleResult(
        surveyService.getLatestClosedSurveyInformation());
  }

}
