package keeper.project.homepage.admin.service.posting;

import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.exception.posting.CustomCommentNotFoundException;
import keeper.project.homepage.repository.member.MemberHasCommentDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasCommentLikeRepository;
import keeper.project.homepage.repository.posting.CommentRepository;
import keeper.project.homepage.repository.posting.PostingRepository;
import keeper.project.homepage.user.service.member.MemberService;
import keeper.project.homepage.user.service.posting.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCommentService {

  public static final String DELETED_COMMENT_CONTENT_BY_ADMIN = CommentService.DELETED_COMMENT_CONTENT;

  private final MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;
  private final MemberHasCommentLikeRepository memberHasCommentLikeRepository;
  private final CommentRepository commentRepository;
  private final MemberService memberService;
  private final PostingRepository postingRepository;

  private void deleteCommentLike(CommentEntity comment) {
    memberHasCommentLikeRepository.deleteByMemberHasCommentEntityPK_CommentEntity(comment);
  }

  private void deleteCommentDislike(CommentEntity comment) {
    memberHasCommentDislikeRepository.deleteByMemberHasCommentEntityPK_CommentEntity(comment);
  }

  private void deleteCommentFK(CommentEntity comment) {

    deleteCommentLike(comment);
    deleteCommentDislike(comment);
  }

  private void deleteComment(Long commentId) {
    CommentEntity comment = commentRepository.findById(commentId)
        .orElseThrow(CustomCommentNotFoundException::new);

    deleteCommentFK(comment);
    MemberEntity virtual = memberService.findById(1L);
    comment.overwriteInfo(virtual, DELETED_COMMENT_CONTENT_BY_ADMIN);
    commentRepository.save(comment);

    PostingEntity postingEntity = postingRepository.findById(comment.getPostingId().getId())
        .orElseThrow(() -> new CustomCommentNotFoundException("댓글에 해당하는 게시글이 존재하지 않습니다."));
    postingEntity.decreaseCommentCount();
    postingRepository.save(postingEntity);
  }

  @Transactional
  public void deleteByAdmin(Long commentId) {
    deleteComment(commentId);
  }
}
