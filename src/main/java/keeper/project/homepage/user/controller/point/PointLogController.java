package keeper.project.homepage.user.controller.point;

import java.util.Map;
import keeper.project.homepage.user.dto.point.request.PointGiftLogRequestDto;
import keeper.project.homepage.user.dto.point.result.PointGiftLogResultDto;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;

import keeper.project.homepage.user.service.point.PointLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/points")
public class PointLogController {

  private final ResponseService responseService;
  private final PointLogService pointLogService;

  @Secured("ROLE_회원")
  @GetMapping(value = "")
  public SingleResult<Map<String, Object>> getPointLogs(
      @PageableDefault(size = 20, sort = "id", direction = Direction.DESC)Pageable pageable
  ) {
    return responseService.getSuccessSingleResult(
        pointLogService.getPointLogs(pageable));
  }

  @Secured("ROLE_회원")
  @PostMapping(value = "/present")
  public SingleResult<PointGiftLogResultDto> presentingPoint(
      @RequestBody PointGiftLogRequestDto pointGiftLogRequestDto
  ) {
    return responseService.getSuccessSingleResult(
        pointLogService.presentingPoint(pointGiftLogRequestDto));
  }

}
