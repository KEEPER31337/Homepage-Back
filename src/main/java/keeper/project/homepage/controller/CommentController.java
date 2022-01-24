package keeper.project.homepage.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.CommentDto;
import keeper.project.homepage.dto.CommonResult;
import keeper.project.homepage.dto.ListResult;
import keeper.project.homepage.dto.SingleResult;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.repository.PostingRepository;
import keeper.project.homepage.service.CommentService;
import keeper.project.homepage.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/comment")
public class CommentController {

  private static final Logger LOGGER = LogManager.getLogger(CommentController.class);

  @Autowired
  private CommentService commentService;

  @Autowired
  private PostingRepository postingRepository;

  private final ResponseService responseService;

  @PostMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public SingleResult<String> createComment(
      @PathVariable("postId") Long postId,
      @RequestBody CommentDto commentDto) {

    Optional<PostingEntity> postingEntity = postingRepository.findById(postId);
    if (!postingEntity.isPresent()) {
      return responseService.getSingleResult("postingId not found");
    }
    commentDto.setRegisterTime(LocalDate.now());
    commentDto.setUpdateTime(LocalDate.now());
    commentService.save(commentDto.toEntity(postingEntity.get()));

    return responseService.getSingleResult("success");
  }

  @GetMapping(value = "/{postId}")
  public ListResult<CommentDto> findCommentByPostId(
      @PathVariable("postId") Long postId,
      @SortDefaults({@SortDefault(sort = "id", direction = Direction.ASC),
          @SortDefault(sort = "registerTime", direction = Direction.ASC)})
      @PageableDefault(page = 0, size = 10) Pageable pageable) {

    PostingEntity postingEntity = postingRepository.findById(postId).get();
    Page<CommentEntity> page = commentService.findAllByPost(postingEntity, pageable);

    List<CommentDto> commentDtos = new ArrayList<>();
    page.getContent().forEach(content -> commentDtos.add(
        new CommentDto(content.getContent(), content.getRegisterTime(), content.getUpdateTime(),
            content.getIpAddress(), content.getLikeCount(), content.getDislikeCount(),
            content.getParentId(), content.getMemberId(), postId.intValue())));
    return responseService.getListResult(commentDtos);
  }

  @GetMapping("/{postId}/{parentId}")
  public ResponseEntity<List<CommentEntity>> findCommentByParentId(
      @PathVariable("postId") Long postId, @PathVariable("parentId") Long parentId,
      @SortDefaults({@SortDefault(sort = "id", direction = Direction.ASC),
          @SortDefault(sort = "registerTime", direction = Direction.ASC)})
      @PageableDefault(page = 0, size = 20) Pageable pageable) {
    PostingEntity postingEntity = postingRepository.findById(postId).get();
    Page<CommentEntity> page = commentService.findAllByParentIdAndPost(parentId, postingEntity,
        pageable);

    return ResponseEntity.status(HttpStatus.OK).body(page.getContent());
  }

  @DeleteMapping("/{commentId}")
  public CommonResult deleteComment(@PathVariable("commentId") Long commentId) {
    commentService.deleteById(commentId);
    return responseService.getSuccessResult();
  }

  @PatchMapping(value = "/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public SingleResult<String> modifyComment(
      @PathVariable("commentId") Long commentId,
      @RequestBody CommentDto commentDto) {

    CommentEntity original = commentService.findById(commentId);
    commentDto.setLikeCount(original.getLikeCount());
    commentDto.setDislikeCount(original.getDislikeCount());
    commentDto.setUpdateTime(LocalDate.now());
    commentDto.setIpAddress(original.getIpAddress());

    CommentEntity result = commentService.updateById(commentId,
        commentDto.toEntity(original.getPostingId()));

    return result.getId().equals(commentId) ? responseService.getSingleResult("success")
        : responseService.getSingleResult("fail to update");
  }

}
