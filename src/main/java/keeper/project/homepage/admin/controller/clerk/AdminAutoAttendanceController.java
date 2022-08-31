package keeper.project.homepage.admin.controller.clerk;

import javax.validation.Valid;
import keeper.project.homepage.admin.dto.clerk.request.AttendanceConditionRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.AttendanceConditionResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.LatestSeminarResponseDto;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.admin.service.clerk.AdminAutoAttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Secured("ROLE_회장")
@RequestMapping("/v1/admin/clerk/attendance")
public class AdminAutoAttendanceController {

  private final ResponseService responseService;

  private final AdminAutoAttendanceService adminAutoAttendanceService;

  @GetMapping
  public SingleResult<LatestSeminarResponseDto> getLatestSeminar(
  ) {
    return responseService.getSuccessSingleResult(adminAutoAttendanceService.getLatestSeminar());
  }

  @GetMapping("/conditions")
  public SingleResult<AttendanceConditionResponseDto> getAttendanceConditions(
      @RequestBody @Valid AttendanceConditionRequestDto attendanceConditionRequestDto) {
    return responseService.getSuccessSingleResult(adminAutoAttendanceService.getAttendanceConditions(
        attendanceConditionRequestDto));
  }

  @GetMapping("/end")
  public SingleResult<Integer> attendanceCheckEnd() {
    return responseService.getSuccessSingleResult(adminAutoAttendanceService.attendanceCheckEnd());
  }
}
