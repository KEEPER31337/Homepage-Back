package keeper.project.homepage.posting.controller;

import static keeper.project.homepage.util.ClientUtil.getUserIP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.posting.dto.LikeAndDislikeDto;
import keeper.project.homepage.posting.dto.PostingBestDto;
import keeper.project.homepage.posting.dto.PostingDto;
import keeper.project.homepage.posting.dto.PostingImageUploadResponseDto;
import keeper.project.homepage.posting.dto.PostingResponseDto;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.posting.service.CommentService;
import keeper.project.homepage.posting.service.PostingService;
import keeper.project.homepage.util.ClientUtil;
import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.util.image.preprocessing.ImageCenterCropping;
import keeper.project.homepage.util.image.preprocessing.ImageSize;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.util.service.result.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

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

  @GetMapping(value = "/latest")
  public ListResult<PostingResponseDto> findAllPosting(
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
      Pageable pageable) {

    return responseService.getSuccessListResult(postingService.findAll(pageable));
  }

  @GetMapping(value = "/lists")
  public ListResult<PostingResponseDto> findAllPostingByCategoryId(
      @RequestParam("category") Long categoryId,
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
      Pageable pageable) {

    return responseService.getSuccessListResult(postingService.findAllByCategoryId(categoryId,
        pageable));
  }

  @GetMapping(value = "/notice")
  public ListResult<PostingResponseDto> findAllNoticePostingByCategoryId(
      @SortDefault(sort = "registerTime", direction = Direction.DESC)
      @RequestParam(value = "category", required = false) Long categoryId) {

    if (categoryId == null) {
      return responseService.getSuccessListResult(postingService.findAllNotice());
    } else {
      return responseService.getSuccessListResult(
          postingService.findAllNoticeByCategoryId(categoryId));
    }
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
      PostingDto dto, HttpServletRequest httpServletRequest) {

    dto.setIpAddress(getUserIP(httpServletRequest));
    ThumbnailEntity thumbnailEntity = thumbnailService.save(ThumbType.PostThumbnail,
        new ImageCenterCropping(
            ImageSize.LARGE), thumbnail, dto.getIpAddress());
    dto.setThumbnailId(thumbnailEntity.getId());
    PostingEntity postingEntity = postingService.save(dto);
    fileService.saveFiles(files, dto.getIpAddress(), postingEntity);

    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/{pid}")
  public SingleResult<PostingResponseDto> getPosting(@PathVariable("pid") Long postingId,
      @RequestParam(value = "password", required = false) String password) {

    PostingResponseDto postingResponseDto = postingService.getPostingResponseById(postingId,
        authService.getMemberIdByJWT(), password);

    return responseService.getSuccessSingleResult(postingResponseDto);
  }

  @GetMapping(value = "/attach/{pid}")
  public ListResult<FileEntity> getAttachList(@PathVariable("pid") Long postingId) {

    return responseService.getSuccessListResult(
        fileService.findAllByPostingId(postingService.getPostingById(postingId)));
  }

  // 다운로드는 ResponseEntity를 사용하는것이 더 용이하여 그대로 두었습니다.
  @GetMapping(value = "/download/{fileId}")
  public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId)
      throws IOException {
    FileEntity fileEntity = fileService.find(fileId);
    Path path = Paths.get(fileEntity.getFilePath());
    Resource resource = new InputStreamResource(Files.newInputStream(path));
    String encodedFileName = UriUtils.encode(fileEntity.getFileName(), StandardCharsets.UTF_8);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + encodedFileName + "\"")
        .body(resource);
  }

  @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/{pid}",
      consumes = "multipart/form-data",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public CommonResult modifyPosting(
      @RequestParam(value = "file", required = false) List<MultipartFile> files,
      PostingDto dto, HttpServletRequest httpServletRequest, @PathVariable("pid") Long postingId) {

    dto.setIpAddress(getUserIP(httpServletRequest));
    PostingEntity postingEntity = postingService.updateById(dto, postingId);
    fileService.saveFiles(files, dto.getIpAddress(), postingEntity);
    deletePrevThumbnail(dto);

    return responseService.getSuccessResult();
  }

  @PatchMapping(value = "/{postId}/thumbnail")
  public CommonResult modifyPostingThumbnail(
      @PathVariable long postId,
      @RequestParam(required = false) MultipartFile thumbnail,
      HttpServletRequest request
  ) {
    postingService.modifyPostingThumbnail(postId, thumbnail, ClientUtil.getUserIP(request));
    return responseService.getSuccessResult();
  }

  @PostMapping(value = "/{pid}/image",
      consumes = "multipart/form-data",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<PostingImageUploadResponseDto> uploadPostingImage(
      @PathVariable("pid") Long postingId,
      @RequestParam(value = "postingImage") MultipartFile postingImage,
      HttpServletRequest request) {

    return responseService.getSuccessSingleResult(
        postingService.uploadPostingImage(postingId, postingImage, request));
  }

  @GetMapping(value = "/delete/{fileId}")
  public CommonResult deleteFile(@PathVariable("fileId") Long fileId) {
    fileService.deleteFile(fileId);

    return responseService.getSuccessResult();
  }

  @DeleteMapping(value = "/files")
  public CommonResult deleteFiles(@RequestParam(value = "fileIdList") List<Long> fileIdList) {
    fileService.deleteFilesByIdList(fileIdList);
    return responseService.getSuccessResult();
  }

  private void deletePrevThumbnail(PostingDto dto) {
    if (dto.getThumbnailId() != null) {
      ThumbnailEntity prevThumbnail = thumbnailService.find(dto.getThumbnailId());
      thumbnailService.delete(prevThumbnail.getId());
    }
  }

  @DeleteMapping(value = "/{pid}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public CommonResult removePosting(@PathVariable("pid") Long postingId) {

    PostingEntity postingEntity = postingService.getPostingById(postingId);
    // NOTE: 게시글에 연결 된 댓글 FK 삭제
    commentService.deleteByPostingId(postingEntity);

    ThumbnailEntity deleteThumbnail = null;
    if (postingEntity.getThumbnail() != null) {
      deleteThumbnail = thumbnailService.find(
          postingEntity.getThumbnail().getId());
    }
    deletePrevFiles(postingEntity);
    postingService.delete(postingEntity);

    if (deleteThumbnail != null) {
      thumbnailService.delete(deleteThumbnail.getId());
    }

    return responseService.getSuccessResult();
  }

  private void deletePrevFiles(PostingEntity postingEntity) {
    List<FileEntity> fileEntities = fileService.findAllByPostingId(
        postingEntity);
    fileService.deleteFiles(fileEntities);
  }


  @GetMapping(value = "/search")
  public ListResult<PostingResponseDto> searchPosting(@RequestParam("type") String type,
      @RequestParam("keyword") String keyword, @RequestParam("category") Long categoryId,
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
      Pageable pageable) {

    return responseService.getSuccessListResult(postingService.searchPosting(type, keyword,
        categoryId, pageable));
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
