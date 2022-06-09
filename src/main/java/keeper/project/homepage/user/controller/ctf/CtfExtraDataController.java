package keeper.project.homepage.user.controller.ctf;

import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.dto.ctf.CtfChallengeCategoryDto;
import keeper.project.homepage.user.dto.ctf.CtfChallengeTypeDto;
import keeper.project.homepage.user.dto.ctf.CtfContestDto;
import keeper.project.homepage.user.service.ctf.CtfExtraDataService;
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
