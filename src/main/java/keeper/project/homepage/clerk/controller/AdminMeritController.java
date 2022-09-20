package keeper.project.homepage.clerk.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.clerk.dto.request.MeritAddRequestDto;
import keeper.project.homepage.clerk.dto.request.MeritLogUpdateRequestDto;
import keeper.project.homepage.clerk.dto.request.MeritTypeCreateRequestDto;
import keeper.project.homepage.clerk.dto.response.MemberTotalMeritLogsResponseDto;
import keeper.project.homepage.clerk.dto.response.MeritLogByYearResponseDto;
import keeper.project.homepage.clerk.dto.response.MeritTypeResponseDto;
import keeper.project.homepage.admin.service.clerk.AdminMeritService;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Secured({"ROLE_회장", "ROLE_부회장", "ROLE_서기"})
@RequestMapping("/v1/admin/clerk/merits")
public class AdminMeritController {

  private final ResponseService responseService;
  private final AdminMeritService adminMeritService;

  @GetMapping
  public ListResult<MeritLogByYearResponseDto> getMeritLogByYear(
      @RequestParam Integer year) {
    return responseService.getSuccessListResult(adminMeritService.getMeritLogByYear(year));
  }

  @GetMapping("/total")
  public ListResult<MemberTotalMeritLogsResponseDto> getMemberTotalMeritLogs() {
    return responseService.getSuccessListResult(adminMeritService.getMemberTotalMeritLogs());
  }

  @PostMapping
  public ListResult<Long> addMeritsWithLogs(
      @RequestBody @Valid List<MeritAddRequestDto> requestDtoList) {
    return responseService.getSuccessListResult(
        adminMeritService.addMeritsWithLogs(requestDtoList));
  }

  @PatchMapping
  public SingleResult<Long> updateMeritWithLog(
      @RequestBody @Valid MeritLogUpdateRequestDto requestDto) {
    return responseService.getSuccessSingleResult(adminMeritService.updateMeritWithLog(requestDto));
  }

  @DeleteMapping("/{meritId}")
  public SingleResult<Long> deleteMeritWithLog(@PathVariable @NotNull Long meritId) {
    return responseService.getSuccessSingleResult(adminMeritService.deleteMeritWithLog(meritId));
  }

  @GetMapping("/years")
  public ListResult<Integer> getYears() {
    return responseService.getSuccessListResult(adminMeritService.getYears());
  }

  @GetMapping("types")
  public ListResult<MeritTypeResponseDto> getMeritTypes() {
    return responseService.getSuccessListResult(adminMeritService.getMeritTypes());
  }

  @PostMapping("types")
  public ListResult<Long> createMeritTypes(
      @RequestBody @Valid List<MeritTypeCreateRequestDto> requestDtoList) {
    return responseService.getSuccessListResult(adminMeritService.createMeritTypes(requestDtoList));
  }

  @DeleteMapping("types")
  public ListResult<Long> deleteMeritTypes(@RequestParam List<Long> typeIds) {
    return responseService.getSuccessListResult(adminMeritService.deleteMeritTypes(typeIds));
  }

}
