package keeper.project.homepage.admin.controller.clerk;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.SeminarCreateRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceStatusResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceUpdateResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarCreateResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarResponseDto;
import keeper.project.homepage.admin.service.clerk.AdminSeminarService;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.PageResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
@Secured({"ROLE_회장", "ROLE_부회장", "ROLE_서기"})
@RequestMapping("/v1/admin/clerk/seminars")
public class AdminSeminarController {

  private final ResponseService responseService;
  private final AdminSeminarService seminarService;

  @GetMapping
  ListResult<SeminarResponseDto> getSeminars() {
    return responseService.getSuccessListResult(seminarService.getSeminars());
  }

  @PostMapping
  SingleResult<SeminarCreateResponseDto> createSeminar(@RequestBody @Valid SeminarCreateRequestDto request) {
    return responseService.getSuccessSingleResult(seminarService.createSeminar(request));
  }

  @GetMapping("/attendances")
  PageResult<SeminarAttendanceResponseDto> getSeminarAttendances(Pageable pageable) {
    return responseService.getSuccessPageResult(seminarService.getSeminarAttendances(pageable));
  }

  @GetMapping("/statuses")
  ListResult<SeminarAttendanceStatusResponseDto> getSeminarAttendanceStatuses() {
    return responseService.getSuccessListResult(seminarService.getSeminarAttendanceStatuses());
  }

  @PatchMapping("/{seminarId}/attendances/members/{memberId}")
  SingleResult<SeminarAttendanceUpdateResponseDto> updateSeminarAttendance(
      @PathVariable @NotNull Long seminarId,
      @PathVariable @NotNull Long memberId,
      @RequestBody  @Valid SeminarAttendanceUpdateRequestDto seminarAttendanceRequest) {
    return responseService.getSuccessSingleResult(
        seminarService.updateSeminarAttendanceStatus(seminarId, memberId, seminarAttendanceRequest));
  }
}
