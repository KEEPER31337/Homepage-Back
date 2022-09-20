package keeper.project.homepage.posting.controller;

import keeper.project.homepage.admin.service.posting.AdminCommentService;
import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.common.service.ResponseService;
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

  private final AdminCommentService adminCommentService;

  private final ResponseService responseService;

  @DeleteMapping("/{commentId}")
  public ResponseEntity<CommonResult> deleteComment(@PathVariable("commentId") Long commentId) {
    adminCommentService.deleteByAdmin(commentId);
    return ResponseEntity.ok().body(responseService.getSuccessResult());
  }
}
