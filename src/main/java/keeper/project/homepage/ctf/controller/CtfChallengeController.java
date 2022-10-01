package keeper.project.homepage.ctf.controller;

import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.ctf.dto.CtfChallengeDto;
import keeper.project.homepage.ctf.dto.CtfCommonChallengeDto;
import keeper.project.homepage.ctf.dto.CtfFlagDto;
import keeper.project.homepage.ctf.service.CtfChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
      @RequestParam("cid") Long ctfId
  ) {
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
