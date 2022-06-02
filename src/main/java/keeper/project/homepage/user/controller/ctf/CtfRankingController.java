package keeper.project.homepage.user.controller.ctf;

import keeper.project.homepage.common.dto.result.PageResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.dto.ctf.CtfTeamDto;
import keeper.project.homepage.user.service.ctf.CtfRankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ctf/ranking")
@Secured("ROLE_회원")
public class CtfRankingController {

  private final ResponseService responseService;
  private final CtfRankingService ctfRankingService;

  @GetMapping("")
  public PageResult<CtfTeamDto> getRankingList(
      @RequestParam Long ctfId,
      @PageableDefault(sort = "score", direction = Direction.DESC) Pageable pageable) {
    return responseService.getSuccessPageResult(ctfRankingService.getRankingList(ctfId, pageable));
  }
}
