package keeper.project.homepage.user.controller.point;

import keeper.project.homepage.dto.point.request.PointGiftLogRequest;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.point.result.PointGiftLogResult;
import keeper.project.homepage.dto.point.result.PointLogResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.service.ResponseService;

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
@RequestMapping(value = "/v1/point")
public class PointLogController {

  private final ResponseService responseService;
  private final PointLogService pointLogService;

  @Secured("ROLE_회원")
  @PostMapping(value = "/transfer")
  public SingleResult<PointGiftLogResult> transferPoint(
      @RequestBody PointGiftLogRequest pointGiftLogRequest
  ) {
    return responseService.getSuccessSingleResult(
        pointLogService.transferPoint(pointGiftLogRequest));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/lists/log")
  public ListResult<PointLogResult> findAllPointLogByMember(
      @PageableDefault(size = 20, sort = "time", direction = Direction.DESC)Pageable pageable
  ) {
    return responseService.getSuccessListResult(
        pointLogService.findAllPointLogByMember(pageable));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/lists/gift/sent")
  public ListResult<PointGiftLogResult> findAllSentPointGiftLog(
      @PageableDefault(size = 20, sort = "time", direction = Direction.DESC)Pageable pageable
  ) {
    return responseService.getSuccessListResult(
        pointLogService.findAllSentPointGiftLog(pageable));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/lists/gift/received")
  public ListResult<PointGiftLogResult> findAllReceivedPointGiftLog(
      @PageableDefault(size = 20, sort = "time", direction = Direction.DESC)Pageable pageable
  ) {
    return responseService.getSuccessListResult(
        pointLogService.findAllReceivedPointGiftLog(pageable));
  }
}
