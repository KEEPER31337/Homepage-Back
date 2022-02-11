package keeper.project.homepage.controller.attendance;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.dto.attendance.AttendancePointDto;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

  @PostMapping(value = "/check", consumes = "application/json", produces = {
      MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> createAttend(@RequestBody AttendanceDto attendanceDto) {

    boolean result = attendanceService.save(attendanceDto);

    return result ? new ResponseEntity<>("success",
        HttpStatus.OK) : new ResponseEntity<>("fail", HttpStatus.OK);
  }

  @Secured("ROLE_회원")
  @PatchMapping(value = "/")
  public CommonResult updateMessage(@RequestBody AttendanceDto attendanceDto) {

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
  public SingleResult<AttendanceEntity> getAttend(
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
    return responseService.getSuccessSingleResult(
        attendanceService.getMyAttendance(date));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/all")
  public ListResult<AttendanceEntity> getAllAttend(
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
    return responseService.getSuccessListResult(
        attendanceService.getAllAttendance(date));
  }

  @GetMapping(value = "/point-info")
  public HashMap<String, Integer> getPointInfo() throws IllegalAccessException {
    return attendanceService.getAllBonusPointInfo();
  }
}