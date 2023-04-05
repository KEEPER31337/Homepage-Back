package keeper.project.homepage.attendance.controller;

import static keeper.project.homepage.util.ClientUtil.getUserIP;

import java.time.LocalDate;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.attendance.dto.AttendanceDto;
import keeper.project.homepage.attendance.dto.AttendanceResultDto;
import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.util.annotation.Nullable;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/attend")
public class AttendanceController {

  private final AttendanceService attendanceService;
  private final ResponseService responseService;

  @PostMapping(value = "")
  public CommonResult createAttend(@RequestBody AttendanceDto attendanceDto,
      HttpServletRequest httpServletRequest) {

    attendanceDto.setIpAddress(getUserIP(httpServletRequest));
    attendanceService.save(attendanceDto);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  // TODO: request로 AttendanceDto를 받아 모든 greeting을 업데이트 할 것이라면 PutMapping이 Restful 하다.
  // TODO: 모든 AttendanceDto를 받을 것이 아니라면, greeting message만 받도록 API를 수정하자.
  // TODO: 또한 url에 "/{attendanceId}"를 pathVariable로 받아 해당 출석을 수정해주도록 하자.
  // TODO: *API 스펙이 변경되는 것이기 때문에 FE와 협업이 필요하다.*
  @PatchMapping(value = "")
  public CommonResult updateMessage(@RequestBody AttendanceDto attendanceDto,
      HttpServletRequest httpServletRequest) {

    attendanceDto.setIpAddress(getUserIP(httpServletRequest));
    attendanceService.updateGreeting(attendanceDto);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/date")
  public ListResult<String> getDate(
      @RequestParam @DateTimeFormat(iso = ISO.DATE) @Nullable LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = ISO.DATE) @Nullable LocalDate endDate) {
    return responseService.getSuccessListResult(
        attendanceService.getMyAttendanceDateList(startDate, endDate));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/info")
  public SingleResult<AttendanceResultDto> getAttend(
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
    return responseService.getSuccessSingleResult(
        attendanceService.getMyAttendanceWithDate(date));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/all")
  public ListResult<AttendanceResultDto> getAllAttend(
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
    return responseService.getSuccessListResult(
        attendanceService.getAllAttendance(date));
  }

  @GetMapping(value = "/point-info")
  public SingleResult<HashMap<String, Integer>> getPointInfo() throws IllegalAccessException {
    return responseService.getSuccessSingleResult(
        attendanceService.getAllBonusPointInfo());
  }
}
