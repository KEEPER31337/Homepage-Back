package keeper.project.homepage.clerk.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.clerk.dto.request.AdminSurveyRequestDto;
import keeper.project.homepage.clerk.dto.response.AdminSurveyResponseDto;
import keeper.project.homepage.clerk.dto.response.DeleteSurveyResponseDto;
import keeper.project.homepage.clerk.dto.response.SurveyResponseDto;
import keeper.project.homepage.clerk.dto.response.SurveyRespondentResponseDto;
import keeper.project.homepage.clerk.dto.response.SurveyUpdateResponseDto;
import keeper.project.homepage.clerk.service.AdminSurveyService;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.PageResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Secured({"ROLE_회장", "ROLE_부회장", "ROLE_서기"})
@RequestMapping("/v1/admin/clerk/surveys")
public class AdminSurveyController {

  private final ResponseService responseService;

  private final AdminSurveyService adminSurveyService;

  @PostMapping
  public SingleResult<Long> createSurvey(
      @RequestBody @Valid AdminSurveyRequestDto adminSurveyRequestDto) {
    return responseService.getSuccessSingleResult(adminSurveyService.createSurvey(
        adminSurveyRequestDto));
  }

  @DeleteMapping("/{surveyId}")
  public SingleResult<DeleteSurveyResponseDto> deleteSurvey(
      @PathVariable("surveyId") @NotNull Long surveyId
  ) {
    return responseService.getSuccessSingleResult(adminSurveyService.deleteSurvey(surveyId));
  }

  @GetMapping("/{surveyId}/respondents")
  public ListResult<SurveyRespondentResponseDto> getRespondents(
      @PathVariable("surveyId") @NotNull Long surveyId
  ) {
    return responseService.getSuccessListResult(adminSurveyService.getRespondents(surveyId));
  }

  @PatchMapping("/{surveyId}")
  public SingleResult<AdminSurveyResponseDto> modifySurvey(
      @PathVariable("surveyId") @NotNull Long surveyId,
      @RequestBody @Valid AdminSurveyRequestDto adminSurveyRequestDto
  ) {
    return responseService.getSuccessSingleResult(
        adminSurveyService.modifySurvey(surveyId, adminSurveyRequestDto));
  }

  @PatchMapping("/{surveyId}/open")
  public SingleResult<SurveyUpdateResponseDto> openSurvey(
      @PathVariable("surveyId") @NotNull Long surveyId
  ) {
    return responseService.getSuccessSingleResult(adminSurveyService.openSurvey(surveyId));
  }

  @PatchMapping("/{surveyId}/close")
  public SingleResult<SurveyUpdateResponseDto> closeSurvey(
      @PathVariable("surveyId") @NotNull Long surveyId
  ) {
    return responseService.getSuccessSingleResult(adminSurveyService.closeSurvey(surveyId));
  }

  @GetMapping
  public PageResult<SurveyResponseDto> getSurveys(
      @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable
  ) {
    return responseService.getSuccessPageResult(
        adminSurveyService.getSurveyList(pageable));
  }
}
