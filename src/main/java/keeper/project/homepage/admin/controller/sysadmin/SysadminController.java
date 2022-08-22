package keeper.project.homepage.admin.controller.sysadmin;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.admin.dto.sysadmin.response.JobResponseDto;
import keeper.project.homepage.admin.dto.sysadmin.response.MemberJobTypeResponseDto;
import keeper.project.homepage.admin.service.sysadmin.SysadminService;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Secured({"ROLE_회장", "ROLE_부회장", "ROLE_전산관리자"})
@RequestMapping("/v1/admin/sysadmin")
public class SysadminController {

  private final ResponseService responseService;
  private final SysadminService sysadminService;

  @GetMapping("/jobs")
  public ListResult<JobResponseDto> getJobList() {
    return responseService.getSuccessListResult(sysadminService.getJobList());
  }

  @PostMapping("/members/{memberId}/jobs/{jobId}")
  public SingleResult<MemberJobTypeResponseDto> assignJob(
      @PathVariable @NotNull Long memberId,
      @PathVariable @NotNull Long jobId) {
    return responseService.getSuccessSingleResult(
        sysadminService.assignJob(memberId, jobId));
  }

  @DeleteMapping("/members/{memberId}/jobs/{jobId}")
  public SingleResult<MemberJobTypeResponseDto> deleteJob(
      @PathVariable @NotNull Long memberId,
      @PathVariable @NotNull Long jobId) {
    return responseService.getSuccessSingleResult(
        sysadminService.deleteJob(memberId, jobId));
  }

  @GetMapping("/members/jobs/{jobId}")
  public ListResult<MemberJobTypeResponseDto> getMemberListByJob(
      @PathVariable @NotNull Long jobId) {
    return responseService.getSuccessListResult(sysadminService.getMemberListByJob(jobId));
  }

  @GetMapping("/jobs/members")
  public ListResult<MemberJobTypeResponseDto> getMemberListHasJob() {
    return responseService.getSuccessListResult(sysadminService.getMemberListHasJob());
  }
}
