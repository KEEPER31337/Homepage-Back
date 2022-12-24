package keeper.project.homepage.ctf.controller;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import keeper.project.homepage.ctf.dto.CtfRankingDto;
import keeper.project.homepage.ctf.service.CtfRankingService;
import keeper.project.homepage.util.dto.result.PageResult;
import keeper.project.homepage.util.service.result.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
  public PageResult<CtfRankingDto> getRankingList(
      @RequestParam Long ctfId,
      @PageableDefault
      @SortDefault.SortDefaults({
          @SortDefault(sort = "score", direction = DESC),
          @SortDefault(sort = "lastSolveTime", direction = ASC),
      })
      Pageable pageable
  ) {
    return responseService.getSuccessPageResult(ctfRankingService.getRankingList(ctfId, pageable));
  }
}
