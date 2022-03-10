package keeper.project.homepage.admin.controller.about;

import keeper.project.homepage.admin.service.about.AdminAboutTitleService;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.service.etc.AboutTitleService;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.admin.dto.etc.StaticWriteTitleDto;
import keeper.project.homepage.admin.dto.etc.StaticWriteTitleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin/about/title")
public class AdminAboutTitleController {

  private final AdminAboutTitleService adminAboutTitleService;
  private final ResponseService responseService;

  @Secured("ROLE_회장")
  @PostMapping(value = "/create")
  public SingleResult<StaticWriteTitleResult> createTitle(@RequestBody StaticWriteTitleDto titleDto) {

    return responseService.getSuccessSingleResult(adminAboutTitleService.createTitle(titleDto));
  }

  @Secured("ROLE_회장")
  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/modify/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<StaticWriteTitleResult> modifyTitleById(
      @PathVariable("id") Long id,
      @RequestBody StaticWriteTitleDto titleDto) {

    return responseService.getSuccessSingleResult(adminAboutTitleService.modifyTitleById(titleDto, id));
  }

  @Secured("ROLE_회장")
  @DeleteMapping(value = "/delete/{id}")
  public SingleResult<StaticWriteTitleResult> deleteTitleById(
      @PathVariable("id") Long id
  ) {
    return responseService.getSuccessSingleResult(adminAboutTitleService.deleteTitleById(id));
  }

}
