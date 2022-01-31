package keeper.project.homepage.controller.etc;

import keeper.project.homepage.dto.etc.StaticWriteTitleDto;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.etc.AboutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
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

  private final AboutService aboutService;
  private final ResponseService responseService;

  @GetMapping(value = "/{title-type}")
  public SingleResult<StaticWriteTitleEntity> getAllContent(
      @PathVariable("title-type") String titleType) {

    return responseService.getSuccessSingleResult(aboutService.findAllByTitleType(titleType));
  }

  @PostMapping(value = "/new")
  public CommonResult createTitle(@RequestBody StaticWriteTitleDto titleDto) {

    aboutService.saveTitle(titleDto);
    return responseService.getSuccessResult();
  }

  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/{title-type}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public CommonResult modifyTitle(
      @PathVariable("title-type") String titleType,
      @RequestBody StaticWriteTitleDto titleDto) {

    aboutService.modifyTitle(titleDto, titleType);
    return responseService.getSuccessResult();
  }
}
