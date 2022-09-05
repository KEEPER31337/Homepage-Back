package keeper.project.homepage.user.controller.clerk;

import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.dto.clerk.request.AttendanceCheckRequestDto;
import keeper.project.homepage.user.dto.clerk.response.AttendanceCheckResponseDto;
import keeper.project.homepage.user.dto.clerk.response.SeminarOngoingAttendanceResponseDto;
import keeper.project.homepage.user.service.clerk.SeminarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Secured("ROLE_회원")
@RequestMapping("/v1/clerk/seminars")
public class SeminarController {

  private final ResponseService responseService;
  private final SeminarService seminarService;

  @GetMapping("/search")
  public SingleResult<SeminarOngoingAttendanceResponseDto> findSeminarOngoingAttendance(
      @RequestParam @NotBlank @DateTimeFormat(pattern = "yyyyMMdd") LocalDate searchDate
  ) {
    return responseService.getSuccessSingleResult(seminarService.findSeminarOngoingAttendance(searchDate));
  }

  @PostMapping("/attendances/check")
  public SingleResult<AttendanceCheckResponseDto> checkSeminarAttendance(
      @RequestBody @Valid AttendanceCheckRequestDto request
  ) {
    return responseService.getSuccessSingleResult(seminarService.checkSeminarAttendance(request));
  }
}
