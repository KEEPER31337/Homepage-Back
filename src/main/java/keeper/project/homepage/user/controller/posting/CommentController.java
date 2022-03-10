package keeper.project.homepage.user.controller.posting;

import java.util.List;
import keeper.project.homepage.user.dto.posting.CommentDto;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.user.service.posting.CommentService;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.common.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
@Secured("ROLE_회원")
public class CommentController {

  private final CommentService commentService;

  private final ResponseService responseService;

  private final AuthService authService;

  @PostMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CommonResult> createComment(
      @PathVariable("postId") Long postId,
      @RequestBody CommentDto commentDto) {
    Long memberId = authService.getMemberIdByJWT();
    commentService.save(commentDto, postId, memberId);
    return ResponseEntity.ok().body(responseService.getSuccessResult());
  }

  @GetMapping(value = "/{postId}")
  public ResponseEntity<ListResult<CommentDto>> showCommentByPostId(
      @PathVariable("postId") Long postId,
      @SortDefaults({@SortDefault(sort = "id", direction = Direction.ASC),
          @SortDefault(sort = "registerTime", direction = Direction.ASC)})
      @PageableDefault(page = 0, size = 10) Pageable pageable) {

    Long memberId = authService.getMemberIdByJWT();
    List<CommentDto> dtoPage = commentService.findAllByPost(memberId, postId, pageable);
    return ResponseEntity.ok().body(responseService.getSuccessListResult(dtoPage));
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<CommonResult> deleteComment(@PathVariable("commentId") Long commentId) {
    Long memberId = authService.getMemberIdByJWT();
    commentService.deleteByWriter(memberId, commentId);
    return ResponseEntity.ok().body(responseService.getSuccessResult());
  }

  @PutMapping(value = "/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SingleResult<CommentDto>> updateComment(
      @PathVariable("commentId") Long commentId,
      @RequestBody CommentDto commentDto) {
    Long memberId = authService.getMemberIdByJWT();
    CommentDto updateDto = commentService.updateById(commentDto, commentId, memberId);
    return ResponseEntity.ok().body(responseService.getSuccessSingleResult(updateDto));
  }

  @GetMapping(value = "/like")
  public ResponseEntity<CommonResult> updateLike(@RequestParam("commentId") Long commentId) {
    Long memberId = authService.getMemberIdByJWT();
    commentService.updateLikeCount(memberId, commentId);
    return ResponseEntity.ok().body(responseService.getSuccessResult());
  }

  @GetMapping(value = "/dislike")
  public ResponseEntity<CommonResult> updateDislike(@RequestParam("commentId") Long commentId) {
    Long memberId = authService.getMemberIdByJWT();
    commentService.updateDislikeCount(memberId, commentId);
    return ResponseEntity.ok().body(responseService.getSuccessResult());
  }
}
