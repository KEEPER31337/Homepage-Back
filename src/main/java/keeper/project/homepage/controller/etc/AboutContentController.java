package keeper.project.homepage.controller.etc;

import keeper.project.homepage.dto.etc.StaticWriteContentDto;
import keeper.project.homepage.dto.etc.StaticWriteSubtitleImageDto;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.result.StaticWriteContentResult;
import keeper.project.homepage.dto.result.StaticWriteSubtitleImageResult;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.etc.AboutContentService;
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
@RequestMapping("/v1/about/content")
public class AboutContentController {

  private final AboutContentService aboutContentService;
  private final ResponseService responseService;

  @Secured("ROLE_회장")
  @PostMapping(value = "/new")
  public SingleResult<StaticWriteContentResult> createContent(
      @RequestBody StaticWriteContentDto staticWriteContentDto
  ) {

    return responseService.getSuccessSingleResult(
        aboutContentService.createContent(staticWriteContentDto));
  }

  @Secured("ROLE_회장")
  @DeleteMapping(value = "/delete/{id}")
  public SingleResult<StaticWriteContentResult> deleteContentById(
      @PathVariable("id") Long id
  ) {

    return responseService.getSuccessSingleResult(
        aboutContentService.deleteContentById(id)
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
        aboutContentService.modifyContentById(staticWriteContentDto, id)
    );
  }

}
