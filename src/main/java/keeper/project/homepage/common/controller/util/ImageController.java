package keeper.project.homepage.common.controller.util;


import java.io.IOException;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.ThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/util")
public class ImageController {

  private final FileService fileService;
  private final ThumbnailService thumbnailService;

  @GetMapping(
      value = "/image/{fileId}",
      produces = MediaType.IMAGE_JPEG_VALUE)
  public @ResponseBody
  byte[] getImage(@PathVariable("fileId") Long fileId) throws IOException {

    return fileService.getImage(fileId);
  }

  @GetMapping(
      value = "/thumbnail/{thumbnailId}",
      produces = MediaType.IMAGE_JPEG_VALUE)
  public @ResponseBody
  byte[] getThumbnail(@PathVariable("thumbnailId") Long thumbnailId) throws IOException {

    return thumbnailService.getThumbnail(thumbnailId);
  }
}
