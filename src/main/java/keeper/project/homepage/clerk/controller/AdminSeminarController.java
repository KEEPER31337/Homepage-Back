package keeper.project.homepage.clerk.controller;

import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.clerk.dto.request.AttendanceStartRequestDto;
import keeper.project.homepage.clerk.dto.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.clerk.dto.request.SeminarCreateRequestDto;
import keeper.project.homepage.clerk.dto.response.AttendanceStartResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarAttendanceResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarAttendanceStatusResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarAttendanceUpdateResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarCreateResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarSearchByDateResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarWithAttendancesResponseByPeriodDto;
import keeper.project.homepage.clerk.service.AdminSeminarService;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.PageResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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
@RequestMapping("/v1/admin/clerk/seminars")
public class AdminSeminarController {

  private final ResponseService responseService;
  private final AdminSeminarService seminarService;

  @GetMapping
  ListResult<SeminarResponseDto> getSeminars() {
    return responseService.getSuccessListResult(seminarService.getSeminars());
  }

  @PostMapping
  SingleResult<SeminarCreateResponseDto> createSeminar(
      @RequestBody @Valid SeminarCreateRequestDto request) {
    return responseService.getSuccessSingleResult(seminarService.createSeminar(request));
  }

  @DeleteMapping("/{seminarId}")
  SingleResult<Long> deleteSeminar(@PathVariable Long seminarId) {
    return responseService.getSuccessSingleResult(seminarService.deleteSeminar(seminarId));
  }

  @GetMapping("/attendances")
  PageResult<SeminarWithAttendancesResponseByPeriodDto> getSeminarWithAttendancesByPeriod(
      Pageable pageable,
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate seasonStartDate,
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate seasonEndDate) {
    return responseService.getSuccessPageResult(
        seminarService.getSeminarWithAttendancesByPeriod(pageable, seasonStartDate, seasonEndDate));
  }

  @GetMapping("{seminarId}/attendances")
  ListResult<SeminarAttendanceResponseDto> getSeminarAttendances(
      @PathVariable @NotNull Long seminarId
  ) {
    return responseService.getSuccessListResult(seminarService.getSeminarAttendances(seminarId));
  }

  @GetMapping("/statuses")
  ListResult<SeminarAttendanceStatusResponseDto> getSeminarAttendanceStatuses() {
    return responseService.getSuccessListResult(seminarService.getSeminarAttendanceStatuses());
  }

  @PatchMapping("/attendances/{attendanceId}")
  SingleResult<SeminarAttendanceUpdateResponseDto> updateSeminarAttendance(
      @PathVariable @NotNull Long attendanceId,
      @RequestBody @Valid SeminarAttendanceUpdateRequestDto request) {
    return responseService.getSuccessSingleResult(
        seminarService.updateSeminarAttendanceStatus(attendanceId, request));
  }

  @GetMapping("/search")
  public SingleResult<SeminarSearchByDateResponseDto> findSeminarByDate(
      @RequestParam @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate searchDate
  ) {
    return responseService.getSuccessSingleResult(seminarService.findSeminarByDate(searchDate));
  }

  @PatchMapping("/attendances/start")
  public SingleResult<AttendanceStartResponseDto> startSeminarAttendance(
      @RequestBody @Valid AttendanceStartRequestDto request
  ) {
    return responseService.getSuccessSingleResult(seminarService.startSeminarAttendance(request));
  }
}
