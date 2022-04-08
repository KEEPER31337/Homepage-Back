package keeper.project.homepage.admin.controller.about;

import keeper.project.homepage.admin.service.about.AdminAboutContentService;
import keeper.project.homepage.admin.dto.etc.StaticWriteContentDto;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.admin.dto.etc.StaticWriteContentResult;
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
@RequestMapping("/v1/admin/about/content")
public class AdminAboutContentController {

  private final AdminAboutContentService adminAboutContentService;
  private final ResponseService responseService;

  @Secured("ROLE_회장")
  @PostMapping(value = "/create")
  public SingleResult<StaticWriteContentResult> createContent(
      @RequestBody StaticWriteContentDto staticWriteContentDto
  ) {

    return responseService.getSuccessSingleResult(
        adminAboutContentService.createContent(staticWriteContentDto));
  }

  @Secured("ROLE_회장")
  @DeleteMapping(value = "/delete/{id}")
  public SingleResult<StaticWriteContentResult> deleteContentById(
      @PathVariable("id") Long id
  ) {

    return responseService.getSuccessSingleResult(
        adminAboutContentService.deleteContentById(id)
    );
  }

  @Secured("ROLE_회장")
  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/modify/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<StaticWriteContentResult> modifyContentById(
      @PathVariable("id") Long id,
      @RequestBody StaticWriteContentDto staticWriteContentDto
  ) {

    return responseService.getSuccessSingleResult(
        adminAboutContentService.modifyContentById(staticWriteContentDto, id)
    );
  }

}
