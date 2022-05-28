package keeper.project.homepage.admin.controller.util;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.util.ClientUtil;
import keeper.project.homepage.util.image.preprocessing.ImageNoChange;
import keeper.project.homepage.util.image.preprocessing.ImageResizing;
import keeper.project.homepage.util.image.preprocessing.ImageResizing.RESIZE_OPTION;
import keeper.project.homepage.util.image.preprocessing.ImageSize;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/badge")
@Secured({"ROLE_회장", "ROLE_전산관리자"})
public class AdminBadgeController {

  private final ResponseService responseService;
  private final ThumbnailService thumbnailService;

  @PostMapping(
      value = "",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResult saveBadge(@RequestParam("badge") MultipartFile badge,
      HttpServletRequest httpServletRequest) {

    String ip = ClientUtil.getUserIP(httpServletRequest);
    thumbnailService.save(ThumbType.Badge,
        new ImageResizing(RESIZE_OPTION.KEEP_RATIO_IN_OUTER_BOUNDARY, ImageSize.SMALL), badge, ip);

    return responseService.getSuccessResult();
  }

  @PutMapping(value = "/{badgeId}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResult updateBadge(@PathVariable("badgeId") Long badgeId,
      @RequestParam("badge") MultipartFile badge,
      HttpServletRequest httpServletRequest) {

    String ip = ClientUtil.getUserIP(httpServletRequest);
    thumbnailService.update(badgeId, ThumbType.Badge,
        new ImageResizing(RESIZE_OPTION.KEEP_RATIO_IN_OUTER_BOUNDARY, ImageSize.SMALL), badge, ip);

    return responseService.getSuccessResult();
  }

  @GetMapping(
      value = "/{badgeId}",
      produces = MediaType.MULTIPART_FORM_DATA_VALUE)
  public @ResponseBody
  byte[] getBadge(@PathVariable("badgeId") Long badgeId) throws IOException {
    return thumbnailService.getByteArrayFromImage(badgeId, new ImageNoChange());
  }

  @DeleteMapping(value = "/{badgeId}")
  public CommonResult deleteBadge(@PathVariable("badgeId") Long badgeId) {
    thumbnailService.delete(badgeId);
    return responseService.getSuccessResult();
  }
}
