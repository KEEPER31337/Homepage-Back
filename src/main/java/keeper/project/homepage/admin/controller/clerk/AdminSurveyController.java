package keeper.project.homepage.admin.controller.clerk;

import javax.validation.Valid;
import keeper.project.homepage.admin.dto.clerk.request.AdminSurveyRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.AdminSurveyResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.DeleteSurveyResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SurveyRespondentResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SurveyUpdateResponseDto;
import keeper.project.homepage.admin.service.clerk.AdminSurveyService;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Secured({"ROLE_회장", "ROLE_부회장", "ROLE_서기"})
@RequestMapping("/v1/admin/clerk/survey")
public class AdminSurveyController {

  private final ResponseService responseService;

  private final AdminSurveyService adminSurveyService;

  @PostMapping("")
  public SingleResult<Long> createSurvey(
      @RequestBody @Valid AdminSurveyRequestDto adminSurveyRequestDto) {
    return responseService.getSuccessSingleResult(adminSurveyService.createSurvey(
        adminSurveyRequestDto));
  }

  @DeleteMapping("/{surveyId}")
  public SingleResult<DeleteSurveyResponseDto> deleteSurvey(
      @PathVariable("surveyId") Long surveyId
  ) {
    return responseService.getSuccessSingleResult(adminSurveyService.deleteSurvey(surveyId));
  }

  @GetMapping("/{surveyId}/respondents")
  public ListResult<SurveyRespondentResponseDto> getRespondents(
      @PathVariable("surveyId") Long surveyId
  ) {
    return responseService.getSuccessListResult(adminSurveyService.getRespondents(surveyId));
  }

  @PatchMapping("/{surveyId}")
  public SingleResult<AdminSurveyResponseDto> modifySurvey(
      @PathVariable("surveyId") Long surveyId,
      @RequestBody @Valid AdminSurveyRequestDto adminSurveyRequestDto
  ) {
    return responseService.getSuccessSingleResult(
        adminSurveyService.modifySurvey(surveyId, adminSurveyRequestDto));
  }

  @PatchMapping("/{surveyId}/open")
  public SingleResult<SurveyUpdateResponseDto> openSurvey(
      @PathVariable("surveyId") Long surveyId
  ) {
    return responseService.getSuccessSingleResult(adminSurveyService.openSurvey(surveyId));
  }

  @PatchMapping("/{surveyId}/close")
  public SingleResult<SurveyUpdateResponseDto> closeSurvey(
      @PathVariable("surveyId") Long surveyId
  ) {
    return responseService.getSuccessSingleResult(adminSurveyService.closeSurvey(surveyId));
  }
}
