package keeper.project.homepage.ctf.controller;

import keeper.project.homepage.ctf.service.CtfExtraDataService;
import keeper.project.homepage.member.dto.CommonMemberDto;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.ctf.dto.CtfChallengeCategoryDto;
import keeper.project.homepage.ctf.dto.CtfChallengeTypeDto;
import keeper.project.homepage.ctf.dto.CtfContestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ctf/extra/data")
@Secured("ROLE_회원")
public class CtfExtraDataController {

  private final ResponseService responseService;
  private final CtfExtraDataService ctfExtraDataService;

  @GetMapping("/challenge-maker")
  public ListResult<CommonMemberDto> getChallengeMakerList() {
    return responseService.getSuccessListResult(ctfExtraDataService.getChallengeMakerList());
  }

  @GetMapping("/challenge-type")
  public ListResult<CtfChallengeTypeDto> getChallengeTypeList() {
    return responseService.getSuccessListResult(ctfExtraDataService.getChallengeTypeList());
  }

  @GetMapping("/challenge-category")
  public ListResult<CtfChallengeCategoryDto> getChallengeCategoryList() {
    return responseService.getSuccessListResult(ctfExtraDataService.getChallengeCategoryList());
  }

  @GetMapping("/contests")
  public ListResult<CtfContestDto> getContestList() {
    return responseService.getSuccessListResult(ctfExtraDataService.getContestList());
  }
}
