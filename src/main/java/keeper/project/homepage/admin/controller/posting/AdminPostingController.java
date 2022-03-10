package keeper.project.homepage.admin.controller.posting;


import java.util.List;
import keeper.project.homepage.admin.service.posting.AdminPostingService;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.service.FileService;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.service.ThumbnailService;
import keeper.project.homepage.user.service.posting.PostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@Secured("ROLE_회장")
@RequestMapping(value = "/v1/admin/post")
public class AdminPostingController {

  private final PostingService postingService;
  private final AdminPostingService adminPostingService;
  private final ResponseService responseService;
  private final FileService fileService;
  private final ThumbnailService thumbnailService;

  @DeleteMapping(value = "/{pid}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public CommonResult removePosting(@PathVariable("pid") Long postingId) {

    PostingEntity postingEntity = postingService.getPostingById(postingId);
    ThumbnailEntity deleteThumbnail = null;
    if (postingEntity.getThumbnail() != null) {
      deleteThumbnail = thumbnailService.findById(
          postingEntity.getThumbnail().getId());
    }
    deletePrevFiles(postingEntity);
    adminPostingService.deleteByAdmin(postingEntity);

    if (postingEntity.getThumbnail() != null) {
      thumbnailService.deleteById(deleteThumbnail.getId());
      fileService.deleteOriginalThumbnail(deleteThumbnail);
    }

    return responseService.getSuccessResult();
  }


  private void deletePrevFiles(PostingEntity postingEntity) {
    List<FileEntity> fileEntities = fileService.findFileEntitiesByPostingId(
        postingEntity);
    fileService.deleteFiles(fileEntities);
  }
}
