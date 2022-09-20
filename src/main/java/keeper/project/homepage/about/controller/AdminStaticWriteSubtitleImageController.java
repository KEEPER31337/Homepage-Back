package keeper.project.homepage.about.controller;

import keeper.project.homepage.admin.service.about.AdminStaticWriteSubtitleImageService;
import keeper.project.homepage.about.dto.request.StaticWriteSubtitleImageDto;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.about.dto.response.StaticWriteSubtitleImageResponseDto;
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
@RequestMapping("/v1/admin/about/sub-titles")
public class AdminStaticWriteSubtitleImageController {

  private final AdminStaticWriteSubtitleImageService adminStaticWriteSubtitleImageService;
  private final ResponseService responseService;

  @Secured("ROLE_회장")
  @PostMapping(value = "", consumes = "multipart/form-data")
  public SingleResult<StaticWriteSubtitleImageResponseDto> createSubtitle(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto,
      @RequestParam(value = "thumbnail", required = false) MultipartFile image,
      @RequestParam("ipAddress") String ipAddress
  ) {

    return responseService.getSuccessSingleResult(
        adminStaticWriteSubtitleImageService.createSubtitle(staticWriteSubtitleImageDto, image,
            ipAddress));
  }

  @Secured("ROLE_회장")
  @DeleteMapping(value = "/{id}")
  public SingleResult<StaticWriteSubtitleImageResponseDto> deleteSubtitleById(
      @PathVariable("id") Long id

  ) {

    return responseService.getSuccessSingleResult(
        adminStaticWriteSubtitleImageService.deleteSubtitleById(id)
    );
  }

  @Secured("ROLE_회장")
  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<StaticWriteSubtitleImageResponseDto> modifySubtitleById(
      @PathVariable("id") Long id,
      @RequestParam(value = "thumbnail", required = false) MultipartFile image,
      @RequestParam("ipAddress") String ipAddress,
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto
  ) {

    return responseService.getSuccessSingleResult(
        adminStaticWriteSubtitleImageService.updateSubtitleById(staticWriteSubtitleImageDto, id,
            image,
            ipAddress));
  }

}
