package keeper.project.homepage.controller.posting;

import java.util.List;
import keeper.project.homepage.dto.posting.CommentDto;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.service.posting.CommentService;
import keeper.project.homepage.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

  private final ResponseService responseService;

  @PostMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CommonResult> createComment(
      @PathVariable("postId") Long postId,
      @RequestBody CommentDto commentDto) {

    if (commentDto.getContent().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(responseService.getFailResult(HttpStatus.BAD_REQUEST.value(), "댓글의 내용이 비어있습니다."));
    }

    commentService.save(commentDto, postId);

    return ResponseEntity.ok().body(responseService.getSuccessResult());
  }

  @GetMapping(value = "/{postId}")
  public ResponseEntity<ListResult<CommentDto>> findCommentByPostId(
      @PathVariable("postId") Long postId,
      @SortDefaults({@SortDefault(sort = "id", direction = Direction.ASC),
          @SortDefault(sort = "registerTime", direction = Direction.ASC)})
      @PageableDefault(page = 0, size = 10) Pageable pageable) {

    List<CommentDto> dtoPage = commentService.findAllByPost(postId, pageable);
    return ResponseEntity.ok().body(responseService.getSuccessListResult(dtoPage));
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<CommonResult> deleteComment(@PathVariable("commentId") Long commentId) {
    if (commentService.findById(commentId) == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(responseService.getFailResult(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 댓글입니다."));
    }

    commentService.deleteById(commentId);

    return commentService.findById(commentId) == null ?
        ResponseEntity.ok().body(responseService.getSuccessResult())
        : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(responseService.getFailResult(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "댓글의 삭제가 진행되지 않았습니다."));
  }

  @PutMapping(value = "/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SingleResult<CommentDto>> updateComment(
      @PathVariable("commentId") Long commentId,
      @RequestBody CommentDto commentDto) {

    if (commentService.findById(commentId) == null) {
      return ResponseEntity.badRequest().body(
          responseService.getFailSingleResult(commentDto, HttpStatus.BAD_REQUEST.value(),
              "더이상 존재하지 않는 댓글입니다."));
    }
    if (commentDto.getContent().isEmpty()) {
      return ResponseEntity.badRequest().body(
          responseService.getFailSingleResult(commentDto, HttpStatus.BAD_REQUEST.value(),
              "댓글의 내용이 비어있습니다."));
    }

    CommentDto updateDto = commentService.updateById(commentDto, commentId);
    return ResponseEntity.ok().body(responseService.getSuccessSingleResult(updateDto));
  }

  @GetMapping(value = "/like")
  public ResponseEntity<CommonResult> updateLike(@RequestParam("commentId") Long commentId,
      @RequestParam("memberId") Long memberId) {
    commentService.updateLikeCount(memberId, commentId);
    return ResponseEntity.ok().body(responseService.getSuccessResult());
  }

  @GetMapping(value = "/dislike")
  public ResponseEntity<CommonResult> updateDislike(@RequestParam("commentId") Long commentId,
      @RequestParam("memberId") Long memberId) {
    commentService.updateDislikeCount(memberId, commentId);
    return ResponseEntity.ok().body(responseService.getSuccessResult());
  }
}
