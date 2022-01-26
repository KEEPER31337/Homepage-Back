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
import keeper.project.homepage.service.image.ImageCenterCrop;
import keeper.project.homepage.service.posting.PostingService;
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
    try {
      FileEntity fileEntity = fileService.saveOriginalImage(thumbnail, dto.getIpAddress());
      thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
          thumbnail, fileEntity, 100, 100);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (thumbnailEntity == null) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    dto.setThumbnailId(thumbnailEntity.getId());
    PostingEntity postingEntity = postingService.save(dto);
    fileService.saveFiles(files, dto.getIpAddress(), postingEntity);

    return postingEntity.getId() != null ? new ResponseEntity<>("success", HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @GetMapping(value = "/{pid}")
  public ResponseEntity<PostingEntity> getPosting(@PathVariable("pid") Long postingId) {
    PostingEntity postingEntity = postingService.getPostingById(postingId);
    postingEntity.increaseVisitCount();
    postingService.updateById(postingEntity, postingId);

    return ResponseEntity.status(HttpStatus.OK).body(postingEntity);
  }

  @GetMapping(value = "/attach/{pid}")
  public ResponseEntity<List<FileEntity>> getAttachList(@PathVariable("pid") Long postindId) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(fileService.getFilesByPostingId(postingService.getPostingById(postindId)));
  }

  @GetMapping(value = "/download/{fileId}")
  public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId)
      throws IOException {
    FileEntity fileEntity = fileService.getFileById(fileId);
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

    ThumbnailEntity savedThumbnail = thumbnailService.findById(dto.getThumbnailId());
    fileService.deleteById(savedThumbnail.getFile().getId());
    thumbnailService.deleteById(savedThumbnail.getId());

    ThumbnailEntity thumbnailEntity = null;
    try {
      FileEntity fileEntity = fileService.saveOriginalImage(thumbnail, dto.getIpAddress());
      thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
          thumbnail, fileEntity, 100, 100);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (thumbnailEntity == null) {
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
        dto.toEntity(categoryEntity.get(), memberEntity.get(), thumbnailEntity),
        postingId);
    List<FileEntity> fileEntities = fileService.getFilesByPostingId(
        postingService.getPostingById(postingId));
    fileService.deleteFiles(fileEntities);
    fileService.saveFiles(files, dto.getIpAddress(), postingEntity);

    return postingEntity.getId() != null ? new ResponseEntity<>("success", HttpStatus.OK) :
        new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @DeleteMapping(value = "/{pid}", produces = {MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> removePosting(@PathVariable("pid") Long postingId) {

    ThumbnailEntity deleteThumbnail = thumbnailService.findById(
        postingService.getPostingById(postingId).getThumbnailId().getId());
    fileService.deleteById(deleteThumbnail.getFile().getId());
    thumbnailService.deleteById(deleteThumbnail.getId());

    List<FileEntity> fileEntities = fileService.getFilesByPostingId(
        postingService.getPostingById(postingId));
    fileService.deleteFiles(fileEntities);
    int result = postingService.deleteById(postingId);

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
    for (PostingEntity postingEntity : postingEntities) {
      postingEntity.setWriter(postingEntity.getMemberId().getNickName());
    }

    return ResponseEntity.status(HttpStatus.OK).body(postingEntities);
  }

  @GetMapping(value = "/like")
  public ResponseEntity<String> likePosting(@RequestParam("memberId") Long memberId,
      @RequestParam("postingId") Long postingId, @RequestParam("type") String type) {

    boolean result = postingService.isPostingLike(memberId, postingId, type.toUpperCase(
        Locale.ROOT));

    return result ? new ResponseEntity<>("success",
        HttpStatus.OK) : new ResponseEntity<>("fail", HttpStatus.OK);
  }

  @GetMapping(value = "/dislike")
  public ResponseEntity<String> dislikePosting(@RequestParam("memberId") Long memberId,
      @RequestParam("postingId") Long postingId, @RequestParam("type") String type) {

    boolean result = postingService.isPostingDislike(memberId, postingId, type.toUpperCase(
        Locale.ROOT));

    return result ? new ResponseEntity<>("success",
        HttpStatus.OK) : new ResponseEntity<>("fail", HttpStatus.OK);
  }
}
