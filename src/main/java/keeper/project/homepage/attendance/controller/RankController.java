package keeper.project.homepage.attendance.controller;

import keeper.project.homepage.util.dto.result.PageResult;
import keeper.project.homepage.attendance.dto.RankDto;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.attendance.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/rank")
public class RankController {

  private final RankService rankService;
  private final ResponseService responseService;

  @Secured("ROLE_회원")
  @GetMapping(value = "")
  public PageResult<RankDto> getRankings(
      @PageableDefault(sort = "point", direction = Direction.DESC) Pageable pageable) {
    return responseService.getSuccessPageResult(rankService.getRankings(pageable));
  }
}
