package keeper.project.homepage.controller.posting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.service.FileService;
import keeper.project.homepage.service.ThumbnailService;
import keeper.project.homepage.common.ImageCenterCrop;
import keeper.project.homepage.service.posting.PostingService;
import keeper.project.homepage.service.util.AuthService;
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
  private final MemberRepository memberRepository;
  private final CategoryRepository categoryRepository;
  private final FileService fileService;
  private final ThumbnailService thumbnailService;
  private final AuthService authService;

  /* ex) http://localhost:8080/v1/posts?category=6&page=1
   * page default 0, size default 10
   */
  @GetMapping(value = "/latest")
  public ResponseEntity<List<PostingEntity>> findAllPosting(
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
          Pageable pageable) {

    return ResponseEntity.status(HttpStatus.OK).body(postingService.findAll(pageable));
  }

  @GetMapping(value = "/lists")
  public ResponseEntity<List<PostingEntity>> findAllPostingByCategoryId(
      @RequestParam("category") Long categoryId,
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
          Pageable pageable) {

    return ResponseEntity.status(HttpStatus.OK).body(postingService.findAllByCategoryId(categoryId,
        pageable));
  }

  @PostMapping(value = "/new", consumes = "multipart/form-data", produces = {
      MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> createPosting(
      @RequestParam(value = "file", required = false) List<MultipartFile> files,
      @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
      PostingDto dto) {

    ThumbnailEntity thumbnailEntity = null;
    FileEntity fileEntity = fileService.saveOriginalThumbnail(thumbnail, dto.getIpAddress());
    thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
        thumbnail, fileEntity, "large");

    if (thumbnailEntity == null) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    dto.setThumbnailId(thumbnailEntity.getId());
    PostingEntity postingEntity = postingService.save(dto);
    fileService.saveFiles(files, dto.getIpAddress(), postingEntity);

    return postingEntity.getId() != null ? new ResponseEntity<>("success", HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/{pid}")
  public ResponseEntity<PostingEntity> getPosting(@PathVariable("pid") Long postingId) {
    PostingEntity postingEntity = postingService.getPostingById(postingId);
    Long visitMemberId = authService.getMemberIdByJWT();
    if (visitMemberId != postingEntity.getMemberId().getId()) {
      // 본인이 아닌 경우
      if (postingEntity.getIsTemp() == PostingService.isTempPosting) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
      }
      postingEntity.increaseVisitCount();
      postingService.updateInfoById(postingEntity, postingId);
    }

    return ResponseEntity.status(HttpStatus.OK).body(postingEntity);
  }

  @GetMapping(value = "/attach/{pid}")
  public ResponseEntity<List<FileEntity>> getAttachList(@PathVariable("pid") Long postingId) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(fileService.findFileEntitiesByPostingId(postingService.getPostingById(postingId)));
  }

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
      MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> modifyPosting(
      @RequestParam(value = "file", required = false) List<MultipartFile> files,
      @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
      PostingDto dto, @PathVariable("pid") Long postingId) {

    ThumbnailEntity prevThumbnail = thumbnailService.findById(dto.getThumbnailId());

    ThumbnailEntity newThumbnail = null;
    FileEntity fileEntity = fileService.saveOriginalThumbnail(thumbnail, dto.getIpAddress());
    if (fileEntity == null) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    newThumbnail = thumbnailService.saveThumbnail(new ImageCenterCrop(),
        thumbnail, fileEntity, "large");
    if (newThumbnail == null) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    Optional<CategoryEntity> categoryEntity = categoryRepository.findById(
        Long.valueOf(dto.getCategoryId()));
    Optional<MemberEntity> memberEntity = memberRepository.findById(
        Long.valueOf(dto.getMemberId()));
    PostingEntity postingEntity = postingService.getPostingById(postingId);
    dto.setUpdateTime(new Date());
    dto.setCommentCount(postingEntity.getCommentCount());
    dto.setLikeCount(postingEntity.getLikeCount());
    dto.setDislikeCount(postingEntity.getDislikeCount());
    dto.setVisitCount(postingEntity.getVisitCount());
    postingService.updateById(
        dto.toEntity(categoryEntity.get(), memberEntity.get(), newThumbnail),
        postingId);
    List<FileEntity> fileEntities = fileService.findFileEntitiesByPostingId(
        postingService.getPostingById(postingId));
    fileService.deleteFiles(fileEntities);
    fileService.saveFiles(files, dto.getIpAddress(), postingEntity);

    thumbnailService.deleteById(prevThumbnail.getId());
    fileService.deleteOriginalThumbnailById(prevThumbnail.getFile().getId());

    return postingEntity.getId() != null ? new ResponseEntity<>("success", HttpStatus.OK) :
        new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @DeleteMapping(value = "/{pid}", produces = {MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> removePosting(@PathVariable("pid") Long postingId) {

    ThumbnailEntity deleteThumbnail = thumbnailService.findById(
        postingService.getPostingById(postingId).getThumbnailId().getId());

    List<FileEntity> fileEntities = fileService.findFileEntitiesByPostingId(
        postingService.getPostingById(postingId));
    fileService.deleteFiles(fileEntities);
    int result = postingService.deleteById(postingId);

    fileService.deleteOriginalThumbnailById(deleteThumbnail.getFile().getId());
    thumbnailService.deleteById(deleteThumbnail.getId());

    return result == 1 ? new ResponseEntity<>("success",
        HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @GetMapping(value = "/search")
  public ResponseEntity<List<PostingEntity>> searchPosting(@RequestParam("type") String type,
      @RequestParam("keyword") String keyword, @RequestParam("category") Long categoryId,
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
          Pageable pageable) {

    List<PostingEntity> postingEntities = postingService.searchPosting(type, keyword,
        categoryId, pageable);

    return ResponseEntity.status(HttpStatus.OK).body(postingEntities);
  }

  @GetMapping(value = "/like")
  public ResponseEntity<String> likePosting(@RequestParam("postingId") Long postingId,
      @RequestParam("type") String type) {

    boolean result = postingService.isPostingLike(postingId, type.toUpperCase(
        Locale.ROOT));

    return result ? new ResponseEntity<>("success",
        HttpStatus.OK) : new ResponseEntity<>("fail", HttpStatus.OK);
  }

  @GetMapping(value = "/dislike")
  public ResponseEntity<String> dislikePosting(@RequestParam("postingId") Long postingId,
      @RequestParam("type") String type) {

    boolean result = postingService.isPostingDislike(postingId, type.toUpperCase(
        Locale.ROOT));

    return result ? new ResponseEntity<>("success",
        HttpStatus.OK) : new ResponseEntity<>("fail", HttpStatus.OK);
  }
}
