package keeper.project.homepage.systemadmin.controller;

import javax.validation.constraints.NotNull;
import keeper.project.homepage.systemadmin.dto.response.JobResponseDto;
import keeper.project.homepage.systemadmin.dto.response.MemberJobTypeResponseDto;
import keeper.project.homepage.admin.service.systemadmin.SystemAdminService;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Secured({"ROLE_회장", "ROLE_부회장", "ROLE_전산관리자"})
@RequestMapping("/v1/admin/system-admin")
public class SystemAdminController {

  private final ResponseService responseService;
  private final SystemAdminService systemAdminService;

  @GetMapping("/jobs")
  public ListResult<JobResponseDto> getJobList() {
    return responseService.getSuccessListResult(systemAdminService.getJobList());
  }

  @PostMapping("/members/{memberId}/jobs/{jobId}")
  public SingleResult<MemberJobTypeResponseDto> assignJob(
      @PathVariable @NotNull Long memberId,
      @PathVariable @NotNull Long jobId) {
    return responseService.getSuccessSingleResult(
        systemAdminService.assignJob(memberId, jobId));
  }

  @DeleteMapping("/members/{memberId}/jobs/{jobId}")
  public SingleResult<MemberJobTypeResponseDto> deleteJob(
      @PathVariable @NotNull Long memberId,
      @PathVariable @NotNull Long jobId) {
    return responseService.getSuccessSingleResult(
        systemAdminService.deleteJob(memberId, jobId));
  }

  @GetMapping("/members/jobs/{jobId}")
  public ListResult<MemberJobTypeResponseDto> getMemberListByJob(
      @PathVariable @NotNull Long jobId) {
    return responseService.getSuccessListResult(systemAdminService.getMemberListByJob(jobId));
  }

  @GetMapping("/jobs/members")
  public ListResult<MemberJobTypeResponseDto> getMemberListHasJob() {
    return responseService.getSuccessListResult(systemAdminService.getMemberListHasJob());
  }
}
