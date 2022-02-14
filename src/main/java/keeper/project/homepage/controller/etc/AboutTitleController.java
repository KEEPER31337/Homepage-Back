package keeper.project.homepage.controller.etc;

import keeper.project.homepage.dto.etc.StaticWriteTitleDto;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.result.StaticWriteTitleResult;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.etc.AboutTitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Role;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

  @Secured("ROLE_회장")
  @PostMapping(value = "/new")
  public SingleResult<StaticWriteTitleResult> createTitle(@RequestBody StaticWriteTitleDto titleDto) {

    return responseService.getSuccessSingleResult(aboutTitleService.createTitle(titleDto));
  }

  @Secured("ROLE_회장")
  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/modify/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<StaticWriteTitleResult> modifyTitleById(
      @PathVariable("id") Long id,
      @RequestBody StaticWriteTitleDto titleDto) {

    return responseService.getSuccessSingleResult(aboutTitleService.modifyTitleById(titleDto, id));
  }

  @Secured("ROLE_회장")
  @DeleteMapping(value = "/delete/{id}")
  public SingleResult<StaticWriteTitleResult> deleteTitleById(
      @PathVariable("id") Long id
  ) {
    return responseService.getSuccessSingleResult(aboutTitleService.deleteTitleById(id));
  }
}
