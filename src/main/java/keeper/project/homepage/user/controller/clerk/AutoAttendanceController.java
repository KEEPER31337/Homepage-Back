package keeper.project.homepage.user.controller.clerk;

import javax.validation.Valid;
import keeper.project.homepage.admin.dto.clerk.response.LatestSeminarResponseDto;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.dto.clerk.request.AttendanceCheckRequestDto;
import keeper.project.homepage.user.dto.clerk.response.AttendanceCheckResponseDto;
import keeper.project.homepage.user.service.clerk.AutoAttendanceService;
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
@Secured("ROLE_회원")
@RequestMapping("/v1/clerk/attendance")
public class AutoAttendanceController {

  private final ResponseService responseService;

  private final AutoAttendanceService autoAttendanceService;

  @GetMapping
  public SingleResult<Long> getLatestSeminarId(
  ) {
    return responseService.getSuccessSingleResult(autoAttendanceService.getLatestSeminarId());
  }

  @GetMapping("/check")
  public SingleResult<AttendanceCheckResponseDto> attendanceCheck(
      @RequestBody @Valid AttendanceCheckRequestDto attendanceCheckRequestDto
  ) {
    return responseService.getSuccessSingleResult(
        autoAttendanceService.attendanceCheck(attendanceCheckRequestDto));
  }


}
