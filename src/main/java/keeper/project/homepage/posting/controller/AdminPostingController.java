package keeper.project.homepage.posting.controller;


import java.util.List;
import keeper.project.homepage.admin.service.posting.AdminPostingService;
import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.posting.service.PostingService;
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
      deleteThumbnail = thumbnailService.find(
          postingEntity.getThumbnail().getId());
    }
    deletePrevFiles(postingEntity);
    adminPostingService.deleteByAdmin(postingEntity);

    if (postingEntity.getThumbnail() != null) {
      thumbnailService.delete(deleteThumbnail.getId());
    }

    return responseService.getSuccessResult();
  }


  private void deletePrevFiles(PostingEntity postingEntity) {
    List<FileEntity> fileEntities = fileService.findAllByPostingId(
        postingEntity);
    fileService.deleteFiles(fileEntities);
  }
}
