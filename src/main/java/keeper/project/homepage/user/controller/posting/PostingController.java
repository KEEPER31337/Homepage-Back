package keeper.project.homepage.user.controller.posting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import keeper.project.homepage.user.dto.posting.LikeAndDislikeDto;
import keeper.project.homepage.user.dto.posting.PostingBestDto;
import keeper.project.homepage.user.dto.posting.PostingDto;
import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.ImageCenterCrop;
import keeper.project.homepage.util.service.ThumbnailService.ThumbnailSize;
import keeper.project.homepage.user.service.posting.CommentService;
import keeper.project.homepage.user.service.posting.PostingService;
import keeper.project.homepage.common.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping(value = "/v1/post")
public class PostingController {

  private final PostingService postingService;
  private final ResponseService responseService;
  private final FileService fileService;
  private final ThumbnailService thumbnailService;
  private final AuthService authService;
  private final CommentService commentService;

  /* ex) http://localhost:8080/v1/posts?category=6&page=1
   * page default 0, size default 10
   */
  @GetMapping(value = "/latest")
  public ListResult<PostingEntity> findAllPosting(
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
          Pageable pageable) {

    return responseService.getSuccessListResult(postingService.findAll(pageable));
  }

  @GetMapping(value = "/lists")
  public ListResult<PostingEntity> findAllPostingByCategoryId(
      @RequestParam("category") Long categoryId,
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
          Pageable pageable) {

    return responseService.getSuccessListResult(postingService.findAllByCategoryId(categoryId,
        pageable));
  }

  @GetMapping(value = "/notice")
  public ListResult<PostingEntity> findAllNoticePostingByCategoryId(
      @RequestParam("category") Long categoryId) {

    return responseService.getSuccessListResult(
        postingService.findAllNoticeByCategoryId(categoryId));
  }

  @GetMapping(value = "/best")
  public ListResult<PostingBestDto> findAllBestPosting() {

    return responseService.getSuccessListResult(
        postingService.findAllBest());
  }

  @PostMapping(value = "/new", consumes = "multipart/form-data", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public CommonResult createPosting(
      @RequestParam(value = "file", required = false) List<MultipartFile> files,
      @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
      PostingDto dto) {

    ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
        thumbnail, ThumbnailSize.LARGE, dto.getIpAddress());

    if (thumbnailEntity == null) {
      return responseService.getFailResult();
    }

    dto.setThumbnailId(thumbnailEntity.getId());
    PostingEntity postingEntity = postingService.save(dto);
    fileService.saveFiles(files, dto.getIpAddress(), postingEntity);

    return postingEntity.getId() != null ? responseService.getSuccessResult()
        : responseService.getFailResult();
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/{pid}")
  public SingleResult<PostingEntity> getPosting(@PathVariable("pid") Long postingId,
      @RequestParam(value = "password", required = false) String password) {

    PostingEntity postingEntity = postingService.getPostingById(postingId);
    Long visitMemberId = authService.getMemberIdByJWT();

    if (postingEntity.getIsSecret() == 1) {
      if (!(postingEntity.getPassword().equals(password))) {
        return responseService.getFailSingleResult(null, -11000, "비밀번호가 일치하지 않습니다.");
      }
    }
    if (visitMemberId != postingEntity.getMemberId().getId()) {
      if (postingEntity.getIsTemp() == PostingService.isTempPosting) {
        return responseService.getFailSingleResult(null, -11100,"임시저장 게시물입니다.");
      }
      postingEntity.increaseVisitCount();
      postingService.updateInfoById(postingEntity, postingId);
    }

    return responseService.getSuccessSingleResult(postingEntity);
  }

  @GetMapping(value = "/attach/{pid}")
  public ListResult<FileEntity> getAttachList(@PathVariable("pid") Long postingId) {

    return responseService.getSuccessListResult(
        fileService.findFileEntitiesByPostingId(postingService.getPostingById(postingId)));
  }

  // 다운로드는 ResponseEntity를 사용하는것이 더 용이하여 그대로 두었습니다.
  @GetMapping(value = "/download/{fileId}")
  public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId)
      throws IOException {
    FileEntity fileEntity = fileService.findFileEntityById(fileId);
    Path path = Paths.get(fileEntity.getFilePath());
    Resource resource = new InputStreamResource(Files.newInputStream(path));

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + fileEntity.getFileName() + "\"")
        .body(resource);
  }

  @RequestMapping(method = {RequestMethod.PUT,
      RequestMethod.PATCH}, value = "/{pid}", consumes = "multipart/form-data", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public CommonResult modifyPosting(
      @RequestParam(value = "file", required = false) List<MultipartFile> files,
      @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
      PostingDto dto, @PathVariable("pid") Long postingId) {

    ThumbnailEntity newThumbnail = saveThumbnail(thumbnail, dto);

    PostingEntity postingEntity = postingService.updateById(dto, postingId, newThumbnail);
    deletePrevFiles(postingId);
    fileService.saveFiles(files, dto.getIpAddress(), postingEntity);

    deletePrevThumbnail(dto);

    return responseService.getSuccessResult();
  }

  private void deletePrevFiles(PostingEntity postingEntity) {
    List<FileEntity> fileEntities = fileService.findFileEntitiesByPostingId(
        postingEntity);
    fileService.deleteFiles(fileEntities);
  }

  private void deletePrevFiles(Long postingId) {
    deletePrevFiles(postingService.getPostingById(postingId));
  }

  private ThumbnailEntity saveThumbnail(MultipartFile thumbnail, PostingDto dto) {
    ThumbnailEntity newThumbnail = thumbnailService.saveThumbnail(new ImageCenterCrop(), thumbnail,
        ThumbnailSize.LARGE, dto.getIpAddress());
    if (newThumbnail == null) {
      throw new CustomThumbnailEntityNotFoundException("썸네일 저장 중에 에러가 발생했습니다.");
    }
    return newThumbnail;
  }

  private void deletePrevThumbnail(PostingDto dto) {
    if (dto.getThumbnailId() != null) {
      ThumbnailEntity prevThumbnail = thumbnailService.findById(dto.getThumbnailId());
      thumbnailService.deleteById(prevThumbnail.getId());
      fileService.deleteOriginalThumbnail(prevThumbnail);
    }
  }

  @DeleteMapping(value = "/{pid}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public CommonResult removePosting(@PathVariable("pid") Long postingId) {

    PostingEntity postingEntity = postingService.getPostingById(postingId);

    // NOTE: 게시글에 연결 된 댓글 FK 삭제
    commentService.deleteByPostingId(postingEntity);

    ThumbnailEntity deleteThumbnail = null;
    if (postingEntity.getThumbnail() != null) {
      deleteThumbnail = thumbnailService.findById(
          postingEntity.getThumbnail().getId());
    }
    deletePrevFiles(postingEntity);
    postingService.delete(postingEntity);

    if (postingEntity.getThumbnail() != null) {
      thumbnailService.deleteById(deleteThumbnail.getId());
      fileService.deleteOriginalThumbnail(deleteThumbnail);
    }

    return responseService.getSuccessResult();
  }


  @GetMapping(value = "/search")
  public ListResult<PostingEntity> searchPosting(@RequestParam("type") String type,
      @RequestParam("keyword") String keyword, @RequestParam("category") Long categoryId,
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
          Pageable pageable) {

    List<PostingEntity> postingEntities = postingService.searchPosting(type, keyword,
        categoryId, pageable);

    return responseService.getSuccessListResult(postingEntities);
  }

  @GetMapping(value = "/like")
  public CommonResult likePosting(@RequestParam("postingId") Long postingId,
      @RequestParam("type") String type) {

    boolean result = postingService.isPostingLike(postingId, type.toUpperCase(
        Locale.ROOT));

    return result ? responseService.getSuccessResult() : responseService.getFailResult();
  }

  @GetMapping(value = "/dislike")
  public CommonResult dislikePosting(@RequestParam("postingId") Long postingId,
      @RequestParam("type") String type) {

    boolean result = postingService.isPostingDislike(postingId, type.toUpperCase(
        Locale.ROOT));

    return result ? responseService.getSuccessResult() : responseService.getFailResult();
  }

  @GetMapping(value = "/check")
  public SingleResult<LikeAndDislikeDto> checkMemberLikedAndDisliked(
      @RequestParam("postingId") Long postingId) {

    return responseService.getSuccessSingleResult(postingService.checkLikeAndDisLike(postingId));
  }
}
