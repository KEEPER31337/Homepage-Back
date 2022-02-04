package keeper.project.homepage.controller.attendance;

import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
