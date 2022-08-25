package keeper.project.homepage.user.controller.clerk;

import javax.validation.Valid;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.dto.clerk.SurveyInformationRequestDto;
import keeper.project.homepage.user.dto.clerk.SurveyModifyResponseDto;
import keeper.project.homepage.user.dto.clerk.SurveyResponseRequestDto;
import keeper.project.homepage.user.dto.clerk.SurveyInformationDto;
import keeper.project.homepage.user.service.clerk.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Secured("ROLE_회원")
@RequestMapping("/v1/clerk/survey")
public class SurveyController {

  private final ResponseService responseService;

  private final SurveyService surveyService;

  @PostMapping("/{surveyId}")
  public SingleResult<Long> responseSurvey(
      @PathVariable("surveyId") Long surveyId,
      @RequestBody @Valid SurveyResponseRequestDto responseRequestDto
  ) {
    return responseService.getSuccessSingleResult(surveyService.responseSurvey(surveyId,responseRequestDto));
  }

  @RequestMapping(value = "/{surveyId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
  public SingleResult<SurveyModifyResponseDto> modifyResponse(
      @PathVariable("surveyId") Long surveyId,
      @RequestBody @Valid SurveyResponseRequestDto responseRequestDto
  ) {
    return responseService.getSuccessSingleResult(surveyService.modifyResponse(surveyId, responseRequestDto));
  }

  @GetMapping("")
  public SingleResult<SurveyInformationDto> getSurveyInformation(
      @RequestBody @Valid SurveyInformationRequestDto requestDto
  ){
    return responseService.getSuccessSingleResult(surveyService.getSurveyInformation(requestDto));
  }
}
