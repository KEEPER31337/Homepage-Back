package keeper.project.homepage.user.controller.ctf;

import java.nio.file.AccessDeniedException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.admin.dto.ctf.CtfChallengeAdminDto;
import keeper.project.homepage.admin.dto.ctf.CtfContestDto;
import keeper.project.homepage.admin.dto.ctf.CtfProbMakerDto;
import keeper.project.homepage.admin.dto.ctf.CtfSubmitLogDto;
import keeper.project.homepage.admin.service.ctf.CtfAdminService;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.PageResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.dto.ctf.CtfChallengeDto;
import keeper.project.homepage.user.dto.ctf.CtfCommonChallengeDto;
import keeper.project.homepage.user.dto.ctf.CtfFlagDto;
import keeper.project.homepage.user.service.ctf.CtfChallengeService;
import keeper.project.homepage.util.dto.FileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ctf/prob")
@Secured("ROLE_회원")
public class CtfChallengeController {

  private final ResponseService responseService;
  private final CtfChallengeService ctfChallengeService;

  @GetMapping("")
  public ListResult<CtfCommonChallengeDto> getProblemList(
      @RequestParam("cid") Long ctfId) {
    return responseService.getSuccessListResult(
        ctfChallengeService.getProblemList(ctfId));
  }

  @PostMapping("/{pid}/submit/flag")
  public SingleResult<CtfFlagDto> checkFlag(
      @PathVariable("pid") Long probId,
      @RequestBody CtfFlagDto submitFlag
  ) {
    ctfChallengeService.setLog(probId, submitFlag);
    return responseService.getSuccessSingleResult(
        ctfChallengeService.checkFlag(probId, submitFlag));
  }

  @GetMapping("/{pid}")
  public SingleResult<CtfChallengeDto> getProblemDetail(
      @PathVariable("pid") Long probId
  ) {
    return responseService.getSuccessSingleResult(
        ctfChallengeService.getProblemDetail(probId));
  }
}
