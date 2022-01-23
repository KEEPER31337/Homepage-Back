package keeper.project.homepage.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.dto.CommentDto;
import keeper.project.homepage.dto.CommonResult;
import keeper.project.homepage.dto.ListResult;
import keeper.project.homepage.dto.SingleResult;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.service.CommentService;
import keeper.project.homepage.service.MemberHasCommentDislikeService;
import keeper.project.homepage.service.MemberHasCommentLikeService;
import keeper.project.homepage.service.MemberService;
import keeper.project.homepage.service.PostingService;
import keeper.project.homepage.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/comment")
public class CommentController {

  private final CommentService commentService;

  private final PostingService postingService;

  private final MemberService memberService;

  private final ResponseService responseService;

  private final MemberHasCommentLikeService memberHasCommentLikeService;

  private final MemberHasCommentDislikeService memberHasCommentDislikeService;

  @PostMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public SingleResult<String> createComment(
      @PathVariable("postId") Long postId,
      @RequestBody CommentDto commentDto) {

    PostingEntity postingEntity = postingService.getPostingById(postId);
    MemberEntity memberEntity = memberService.findById(commentDto.getMemberId());
    commentDto.setRegisterTime(LocalDate.now());
    commentDto.setUpdateTime(LocalDate.now());
    commentService.save(commentDto.toEntity(postingEntity, memberEntity));

    return responseService.getSingleResult("success");
  }

  @GetMapping(value = "/{postId}")
  public ListResult<CommentDto> findCommentByPostId(
      @PathVariable("postId") Long postId,
      @SortDefaults({@SortDefault(sort = "id", direction = Direction.ASC),
          @SortDefault(sort = "registerTime", direction = Direction.ASC)})
      @PageableDefault(page = 0, size = 10) Pageable pageable) {

    PostingEntity postingEntity = postingService.getPostingById(postId);
    Page<CommentEntity> entityPage = commentService.findAllByPost(postingEntity, pageable);

    List<CommentDto> dtoPage = new ArrayList<>();
    entityPage.getContent().forEach(content -> dtoPage.add(
        new CommentDto(content.getContent(), content.getRegisterTime(), content.getUpdateTime(),
            content.getIpAddress(), content.getLikeCount(), content.getDislikeCount(),
            content.getParentId(), content.getMemberId().getId(), postId)));
    return responseService.getListResult(dtoPage);
  }

  @GetMapping("/{postId}/{parentId}")
  public ResponseEntity<List<CommentEntity>> findCommentByParentId(
      @PathVariable("postId") Long postId, @PathVariable("parentId") Long parentId,
      @SortDefaults({@SortDefault(sort = "id", direction = Direction.ASC),
          @SortDefault(sort = "registerTime", direction = Direction.ASC)})
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    PostingEntity postingEntity = postingService.getPostingById(postId);
    Page<CommentEntity> entityPage = commentService.findAllByParentIdAndPost(parentId,
        postingEntity,
        pageable);

    return ResponseEntity.status(HttpStatus.OK).body(entityPage.getContent());
  }

  @DeleteMapping("/{commentId}")
  public CommonResult deleteComment(@PathVariable("commentId") Long commentId) {
    commentService.deleteById(commentId);
    return responseService.getSuccessResult();
  }

  @PutMapping(value = "/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public SingleResult<String> modifyComment(
      @PathVariable("commentId") Long commentId,
      @RequestBody CommentDto commentDto) {

    CommentEntity original = commentService.findById(commentId);
    commentDto.setLikeCount(original.getLikeCount());
    commentDto.setDislikeCount(original.getDislikeCount());
    commentDto.setUpdateTime(LocalDate.now());
    commentDto.setIpAddress(original.getIpAddress());

    CommentEntity result = commentService.updateById(commentId,
        commentDto.toEntity(original.getPostingId(), original.getMemberId()));

    return result.getId() != null ? responseService.getSingleResult("success")
        : responseService.getSingleResult("fail to update");
  }

  @GetMapping(value = "/like")
  public SingleResult<String> updateLike(@RequestParam("commentId") Long commentId,
      @RequestParam("memberId") Long memberId) {
    MemberEntity memberEntity = memberService.findById(memberId);
    CommentEntity commentEntity = commentService.findById(commentId);
    if (memberHasCommentLikeService.findById(memberEntity, commentEntity) == null) {
      memberHasCommentLikeService.saveWithMemberAndCommentEntity(memberEntity, commentEntity);
      commentService.increaseLikeCount(commentId);
    } else {
      memberHasCommentLikeService.deleteByMemberAndCommentEntity(memberEntity, commentEntity);
      commentService.decreaseLikeCount(commentId);
    }
    return responseService.getSingleResult("success");
  }

  @GetMapping(value = "/dislike")
  public SingleResult<String> updateDislike(@RequestParam("commentId") Long commentId,
      @RequestParam("memberId") Long memberId) {
    MemberEntity memberEntity = memberService.findById(memberId);
    CommentEntity commentEntity = commentService.findById(commentId);
    if (memberHasCommentDislikeService.findById(memberEntity, commentEntity) == null) {
      memberHasCommentDislikeService.saveWithMemberAndCommentEntity(memberEntity, commentEntity);
      commentService.increaseDislikeCount(commentId);
    } else {
      memberHasCommentDislikeService.deleteByMemberAndCommentEntity(memberEntity, commentEntity);
      commentService.decreaseDislikeCount(commentId);
    }
    return responseService.getSingleResult("success");
  }
}
