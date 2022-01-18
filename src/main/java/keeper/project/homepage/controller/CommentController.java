package keeper.project.homepage.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.CommentDto;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.repository.PostingRepository;
import keeper.project.homepage.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/comments")
public class CommentController {

  @Autowired
  private CommentService commentService;

  @Autowired
  private PostingRepository postingRepository;

  @PostMapping(value = "/{postId}", consumes = "multipart/form-data", produces = {
      MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> createComment(
      @PathVariable("postId") Long postId,
      CommentDto commentDto) {

    Optional<PostingEntity> postingEntity = postingRepository.findById(postId);
    if (!postingEntity.isPresent()) {
      return new ResponseEntity<>("postingId not found", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    commentDto.setRegisterTime(LocalDate.now());
    commentDto.setUpdateTime(LocalDate.now());
    commentService.save(commentDto.toEntity(postingEntity.get()));

    return new ResponseEntity<>("success", HttpStatus.OK);
  }

  @GetMapping("/{postId}")
  public ResponseEntity<List<CommentEntity>> findCommentByPostId(Model model,
      @PathVariable("postId") Long postId,
      @SortDefaults({@SortDefault(sort = "id", direction = Direction.ASC),
          @SortDefault(sort = "registerTime", direction = Direction.ASC)})
      @PageableDefault(page = 0, size = 20) Pageable pageable) {

    PostingEntity postingEntity = postingRepository.findById(postId).get();
    Page<CommentEntity> page = commentService.findAllByPost(postingEntity, pageable);

//    int nowPage = page.getPageable().getPageNumber() + 1;
//    int startPage = max(nowPage - 4, 1);
//    int endPage = min(nowPage + 5, page.getTotalPages());
//    model.addAttribute("commentList", commentService.commentViewAll());
//    model.addAttribute("nowPage", nowPage);
//    model.addAttribute("startPage", startPage);
//    model.addAttribute("endPage", endPage);

    return ResponseEntity.status(HttpStatus.OK).body(page.getContent());
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
  public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId) {
    commentService.deleteById(commentId);
    return ResponseEntity.status(HttpStatus.OK).body("success");
  }

  @PatchMapping("/{postId}/{commentId}")
  public ResponseEntity<String> modifyComment(@PathVariable("postId") Long postId,
      @PathVariable("commentId") Long commentId,
      CommentDto commentDto) {

    CommentEntity commentEntity = commentService.findById(commentId);
    commentDto.setLikeCount(commentEntity.getLikeCount());
    commentDto.setDislikeCount(commentEntity.getDislikeCount());
    commentDto.setUpdateTime(LocalDate.now());
    commentDto.setIpAddress(commentEntity.getIpAddress());

    CommentEntity result = commentService.updateById(commentId,
        commentDto.toEntity(postingRepository.findById(postId).get()));

    return result.getId() == commentId ? ResponseEntity.status(HttpStatus.OK).body("success")
        : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            "original id is " + result.getId().toString() + ", but modified id is "
                + commentId.toString());
  }

}
