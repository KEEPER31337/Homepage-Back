package keeper.project.homepage.user.controller.about;

import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.admin.dto.about.response.StaticWriteTitleResponseDto;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.service.about.StaticWriteTitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/about/titles")
public class StaticWriteTitleController {

  private final StaticWriteTitleService staticWriteTitleService;
  private final ResponseService responseService;

  @GetMapping(value = "/types/{type}")
  public ListResult<StaticWriteTitleResponseDto> findAllByType(
      @PathVariable("type") String type) {

    return responseService.getSuccessListResult(staticWriteTitleService.findAllByType(type));
  }

  @GetMapping(value = "/types")
  public ListResult<String> getAllDistinctTypes() {
    return responseService.getSuccessListResult(staticWriteTitleService.getAllDistinctTypes());
  }

}
