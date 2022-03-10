package keeper.project.homepage.user.controller.about;

import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.admin.dto.etc.StaticWriteTitleResult;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.user.service.about.AboutTitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/about/title")
public class AboutTitleController {

  private final AboutTitleService aboutTitleService;
  private final ResponseService responseService;

  @GetMapping(value = "type/{type}")
  public ListResult<StaticWriteTitleResult> findAllByType(
      @PathVariable("type") String type) {

    return responseService.getSuccessListResult(aboutTitleService.findAllByType(type));
  }

  @GetMapping(value = "/{title}")
  public SingleResult<StaticWriteTitleResult> findByTitle(
      @PathVariable("title") String title) {

    return responseService.getSuccessSingleResult(aboutTitleService.findByTitle(title));
  }

}
