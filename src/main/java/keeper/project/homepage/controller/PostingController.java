package keeper.project.homepage.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.PostingDto;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.repository.CategoryRepository;
import keeper.project.homepage.service.FileService;
import keeper.project.homepage.service.PostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
  private final CategoryRepository categoryRepository;
  private final FileService fileService;

  /* ex) http://localhost:8080/v1/posts?category=6&page=1
   * page default 0, size default 10
   */
  @GetMapping(value = "/latest")
  public ResponseEntity<List<PostingEntity>> findAllPosting(
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
          Pageable pageable) {

    List<PostingEntity> postingEntities = postingService.findAll(pageable);

    return ResponseEntity.status(HttpStatus.OK).body(postingEntities);
  }

  @GetMapping(value = "/lists")
  public ResponseEntity<List<PostingEntity>> findAllPostingByCategoryId(
      @RequestParam("category") Long categoryId,
      @PageableDefault(size = 10, sort = "registerTime", direction = Direction.DESC)
          Pageable pageable) {

    Optional<CategoryEntity> categoryEntity = categoryRepository.findById(Long.valueOf(categoryId));
    List<PostingEntity> postingEntities = postingService.findAllByCategoryId(categoryEntity.get(),
        pageable);
    /* 이후 처리할 code
     * if (익명게시판 카테고리 id == categoryId) {
     *  postingEntities.forEach(postingEntity -> postingEntity.makeAnonymous());
     * }
     */
    return ResponseEntity.status(HttpStatus.OK).body(postingEntities);
  }

  @PostMapping(value = "/new", consumes = "multipart/form-data", produces = {
      MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> createPosting(
      @RequestParam(value = "file", required = false) List<MultipartFile> files,
      PostingDto dto) {

    Optional<CategoryEntity> categoryEntity = categoryRepository.findById(
        Long.valueOf(dto.getCategoryId()));
    PostingEntity postingEntity = postingService.save(dto.toEntity(categoryEntity.get()));
    fileService.saveFiles(files, dto, postingEntity);

    return postingEntity.getId() != null ? new ResponseEntity<>("success", HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 글 조회시 글 정보 json 이후 첨부파일이 있으면 첨부파일들 전부 담은 json 리스트 반환
  @GetMapping(value = "/{pid}")
  public ResponseEntity<List<Object>> getPosting(@PathVariable("pid") Long postingId) {

    List<Object> entityList = new ArrayList<>();
    entityList.add(postingService.getPostingById(postingId));
    entityList.addAll(fileService.getFilesByPostingId((PostingEntity) entityList.get(0)));

    return ResponseEntity.status(HttpStatus.OK).body(entityList);
  }

  @RequestMapping(method = {RequestMethod.PUT,
      RequestMethod.PATCH}, value = "/{pid}", consumes = "multipart/form-data", produces = {
      MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> modifyPosting(
      @RequestParam(value = "file", required = false) List<MultipartFile> files,
      PostingDto dto, @PathVariable("pid") Long postingId) {

    Optional<CategoryEntity> categoryEntity = categoryRepository.findById(
        Long.valueOf(dto.getCategoryId()));
    PostingEntity postingEntity = postingService.updateById(dto.toEntity(categoryEntity.get()),
        postingId);
    List<FileEntity> fileEntities = fileService.getFilesByPostingId(
        postingService.getPostingById(postingId));
    fileService.deleteFiles(fileEntities);
    fileService.saveFiles(files, dto, postingEntity);

    return postingEntity.getId() != null ? new ResponseEntity<>("success", HttpStatus.OK) :
        new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @DeleteMapping(value = "/{pid}", produces = {MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> removePosting(@PathVariable("pid") Long postingId) {

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

    Optional<CategoryEntity> categoryEntity = categoryRepository.findById(Long.valueOf(categoryId));
    List<PostingEntity> postingEntities = postingService.searchPosting(type, keyword,
        categoryEntity.get(), pageable);
    return ResponseEntity.status(HttpStatus.OK).body(postingEntities);
  }
}
