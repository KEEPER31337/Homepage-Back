package keeper.project.homepage.admin.controller.about;

import keeper.project.homepage.admin.service.about.AdminStaticWriteContentService;
import keeper.project.homepage.admin.dto.about.request.StaticWriteContentDto;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.admin.dto.about.response.StaticWriteContentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin/about/contents")
public class AdminStaticWriteContentController {

  private final AdminStaticWriteContentService adminStaticWriteContentService;
  private final ResponseService responseService;

  @Secured("ROLE_회장")
  @PostMapping(value = "/new")
  public SingleResult<StaticWriteContentResponseDto> createContent(
      @RequestBody StaticWriteContentDto staticWriteContentDto
  ) {

    return responseService.getSuccessSingleResult(
        adminStaticWriteContentService.createContent(staticWriteContentDto));
  }

  @Secured("ROLE_회장")
  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<StaticWriteContentResponseDto> modifyContentById(
      @PathVariable("id") Long id,
      @RequestBody StaticWriteContentDto staticWriteContentDto
  ) {

    return responseService.getSuccessSingleResult(
        adminStaticWriteContentService.modifyContentById(staticWriteContentDto, id)
    );
  }

}
