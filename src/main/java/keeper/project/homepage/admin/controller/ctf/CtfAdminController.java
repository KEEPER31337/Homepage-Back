package keeper.project.homepage.admin.controller.ctf;

import java.util.List;
import keeper.project.homepage.admin.dto.ctf.CtfContestDto;
import keeper.project.homepage.admin.service.ctf.CtfAdminService;
import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/ctf")
@Secured("ROLE_회장")
public class CtfAdminController {

  private final ResponseService responseService;
  private final CtfAdminService ctfAdminService;

  @PostMapping("/contest")
  public SingleResult<CtfContestDto> createContest(@RequestBody CtfContestDto contestDto) {
    return responseService.getSuccessSingleResult(ctfAdminService.createContest(contestDto));
  }

  @GetMapping("/contests")
  public ListResult<CtfContestDto> getContests() {
    List<CtfContestDto> contestList = ctfAdminService.getContests();
    return responseService.getSuccessListResult(contestList);
  }

  @PatchMapping("/contest/open")
  public SingleResult<CtfContestDto> openContest(@RequestParam Long ctfId) {
    return responseService.getSuccessSingleResult(ctfAdminService.openContest(ctfId));
  }

  @PatchMapping("/contest/close")
  public SingleResult<CtfContestDto> closeContest(@RequestParam Long ctfId) {
    return responseService.getSuccessSingleResult(ctfAdminService.closeContest(ctfId));
  }
}
