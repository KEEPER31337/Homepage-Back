package keeper.project.homepage.posting.service;

import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.repository.MemberHasCommentDislikeRepository;
import keeper.project.homepage.member.repository.MemberHasCommentLikeRepository;
import keeper.project.homepage.member.service.MemberUtilService;
import keeper.project.homepage.posting.entity.CommentEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.posting.exception.CustomCommentNotFoundException;
import keeper.project.homepage.posting.repository.CommentRepository;
import keeper.project.homepage.posting.repository.PostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCommentService {

  public static final String DELETED_COMMENT_CONTENT_BY_ADMIN = CommentService.DELETED_COMMENT_CONTENT;

  private final MemberUtilService memberUtilService;
  private final MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;
  private final MemberHasCommentLikeRepository memberHasCommentLikeRepository;
  private final CommentRepository commentRepository;
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
    MemberEntity virtual = memberUtilService.getById(1L);
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
