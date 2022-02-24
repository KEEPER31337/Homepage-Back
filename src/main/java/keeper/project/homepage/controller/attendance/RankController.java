package keeper.project.homepage.controller.attendance;

import keeper.project.homepage.dto.rank.RankResult;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.attendance.RankService;
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
  public ListResult<RankResult> showRanking(
      @PageableDefault(page = 0, size = RankService.DEFAULT_SIZE, sort = "point", direction = Direction.DESC)
          Pageable pageable) {

    return responseService.getSuccessListResult(rankService.getRankList(pageable));
  }
}
