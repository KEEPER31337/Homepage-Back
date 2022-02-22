package keeper.project.homepage.service.admin.controller.posting;

import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.posting.CommentService;
import keeper.project.homepage.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@Secured("ROLE_회장")
@RequestMapping(value = "/v1/admin/comment")
public class AdminCommentController {

  private final CommentService commentService;

  private final ResponseService responseService;

  private final AuthService authService;

  @DeleteMapping("/{commentId}")
  public ResponseEntity<CommonResult> deleteComment(@PathVariable("commentId") Long commentId) {
    commentService.deleteByAdmin(commentId);
    return ResponseEntity.ok().body(responseService.getSuccessResult());
  }
}
