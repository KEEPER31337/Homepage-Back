package keeper.project.homepage.admin.controller.about;

import keeper.project.homepage.admin.service.about.AdminAboutSubtitleService;
import keeper.project.homepage.admin.dto.etc.StaticWriteSubtitleImageDto;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.admin.dto.etc.StaticWriteSubtitleImageResult;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.ThumbnailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin/about/sub-title")
public class AdminAboutSubtitleController {

  private final AdminAboutSubtitleService adminAboutSubtitleService;
  private final ThumbnailService thumbnailService;
  private final ResponseService responseService;

  @Secured("ROLE_회장")
  @PostMapping(value = "/create")
  public SingleResult<StaticWriteSubtitleImageResult> createSubtitle(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto,
      @RequestParam("thumbnail") MultipartFile image,
      @RequestParam("ipAddress") String ipAddress
  ) {

    return responseService.getSuccessSingleResult(
        adminAboutSubtitleService.createSubtitle(staticWriteSubtitleImageDto, image, ipAddress));
  }

  @Secured("ROLE_회장")
  @DeleteMapping(value = "/delete/{id}")
  public SingleResult<StaticWriteSubtitleImageResult> deleteSubtitleById(
      @PathVariable("id") Long id

  ) {

    return responseService.getSuccessSingleResult(
        adminAboutSubtitleService.deleteSubtitleById(id)
    );
  }

  @Secured("ROLE_회장")
  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/modify/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<StaticWriteSubtitleImageResult> modifySubtitleById(
      @PathVariable("id") Long id,
      @RequestParam("thumbnail") MultipartFile image,
      @RequestParam("ipAddress") String ipAddress,
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto
  ) {

    return responseService.getSuccessSingleResult(
        adminAboutSubtitleService.modifySubtitleById(staticWriteSubtitleImageDto, id, image, ipAddress));
  }

}
