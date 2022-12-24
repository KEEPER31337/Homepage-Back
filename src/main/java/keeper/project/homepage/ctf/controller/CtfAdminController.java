package keeper.project.homepage.ctf.controller;

import java.nio.file.AccessDeniedException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import keeper.project.homepage.ctf.dto.CtfChallengeAdminDto;
import keeper.project.homepage.ctf.dto.CtfContestAdminDto;
import keeper.project.homepage.ctf.dto.CtfProbMakerDto;
import keeper.project.homepage.ctf.dto.CtfSubmitLogDto;
import keeper.project.homepage.ctf.service.CtfAdminService;
import keeper.project.homepage.util.dto.FileDto;
import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.util.dto.result.PageResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/ctf")
public class CtfAdminController {

  private final ResponseService responseService;
  private final CtfAdminService ctfAdminService;

  @Secured("ROLE_회장")
  @PostMapping("/contest")
  public SingleResult<CtfContestAdminDto> createContest(
      @RequestBody CtfContestAdminDto contestDto
  ) {
    return responseService.getSuccessSingleResult(ctfAdminService.createContest(contestDto));
  }

  @Secured("ROLE_회장")
  @GetMapping("/contests")
  public PageResult<CtfContestAdminDto> getContests(
      @PageableDefault Pageable pageable
  ) {
    return responseService.getSuccessPageResult(ctfAdminService.getContests(pageable));
  }

  @Secured("ROLE_회장")
  @PatchMapping("/contest/{cid}/open")
  public SingleResult<CtfContestAdminDto> openContest(
      @PathVariable("cid") Long challengeId
  ) {
    return responseService.getSuccessSingleResult(ctfAdminService.openContest(challengeId));
  }

  @Secured("ROLE_회장")
  @PatchMapping("/contest/{cid}/close")
  public SingleResult<CtfContestAdminDto> closeContest(
      @PathVariable("cid") Long challengeId
  ) {
    return responseService.getSuccessSingleResult(ctfAdminService.closeContest(challengeId));
  }

  @Secured("ROLE_회장")
  @PostMapping("/prob/maker")
  public SingleResult<CtfProbMakerDto> designateProbMaker(
      @RequestBody CtfProbMakerDto probMakerDto
  ) {
    return responseService.getSuccessSingleResult(ctfAdminService.designateProbMaker(probMakerDto));
  }

  @Secured("ROLE_회장")
  @DeleteMapping("/prob/maker")
  public CommonResult disqualifyProbMaker(
      @RequestBody CtfProbMakerDto probMakerDto
  ) {
    ctfAdminService.disqualifyProbMaker(probMakerDto);
    return responseService.getSuccessResult();
  }

  @Secured({"ROLE_회장", "ROLE_출제자"})
  @PostMapping(value = "/prob")
  public SingleResult<CtfChallengeAdminDto> createProblem(
      @Valid @RequestBody CtfChallengeAdminDto challengeAdminDto
  ) {
    return responseService.getSuccessSingleResult(
        ctfAdminService.createChallenge(challengeAdminDto));
  }

  @Secured({"ROLE_회장", "ROLE_출제자"})
  @PostMapping(value = "/prob/file", consumes = "multipart/form-data")
  public SingleResult<FileDto> fileRegistrationInProblem(
      @RequestParam("file") MultipartFile file,
      @RequestParam("challengeId") Long challengeId, HttpServletRequest request
  ) {
    return responseService.getSuccessSingleResult(
        ctfAdminService.saveFileAndRegisterInChallenge(challengeId, request, file));
  }

  @Secured({"ROLE_회장", "ROLE_출제자"})
  @PatchMapping("/prob/{pid}/open")
  public SingleResult<CtfChallengeAdminDto> openProblem(
      @PathVariable("pid") Long problemId
  ) {
    return responseService.getSuccessSingleResult(ctfAdminService.openProblem(problemId));
  }

  @Secured({"ROLE_회장", "ROLE_출제자"})
  @PatchMapping("/prob/{pid}/close")
  public SingleResult<CtfChallengeAdminDto> closeProblem(
      @PathVariable("pid") Long problemId
  ) {
    return responseService.getSuccessSingleResult(ctfAdminService.closeProblem(problemId));
  }

  @Secured({"ROLE_회장", "ROLE_출제자"})
  @DeleteMapping("/prob/{pid}")
  public SingleResult<CtfChallengeAdminDto> deleteProblem(
      @PathVariable("pid") Long problemId
  ) throws AccessDeniedException {
    return responseService.getSuccessSingleResult(ctfAdminService.deleteProblem(problemId));
  }

  @Secured({"ROLE_회장", "ROLE_출제자"})
  @GetMapping("/prob")
  public PageResult<CtfChallengeAdminDto> getProblemList(
      @PageableDefault Pageable pageable,
      @RequestParam Long ctfId
  ) {
    return responseService.getSuccessPageResult(ctfAdminService.getProblemList(pageable, ctfId));
  }

  @Secured({"ROLE_회장", "ROLE_출제자"})
  @GetMapping("/submit-log/{cid}")
  public PageResult<CtfSubmitLogDto> getSubmitLogList(
      @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
      @PathVariable("cid") Long contestId
  ) {
    return responseService.getSuccessPageResult(
        ctfAdminService.getSubmitLogList(pageable, contestId));
  }
}
