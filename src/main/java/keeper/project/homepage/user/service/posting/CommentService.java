package keeper.project.homepage.user.service.posting;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.user.dto.posting.CommentDto;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.posting.entity.CommentEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.sign.exception.CustomAuthenticationEntryPointException;
import keeper.project.homepage.util.exception.CustomNumberOverflowException;
import keeper.project.homepage.posting.exception.CustomCommentEmptyFieldException;
import keeper.project.homepage.posting.exception.CustomCommentNotFoundException;
import keeper.project.homepage.member.repository.MemberHasCommentDislikeRepository;
import keeper.project.homepage.member.repository.MemberHasCommentLikeRepository;
import keeper.project.homepage.posting.repository.CommentRepository;
import keeper.project.homepage.posting.repository.CommentSpec;
import keeper.project.homepage.posting.repository.PostingRepository;
import keeper.project.homepage.user.service.member.MemberHasCommentDislikeService;
import keeper.project.homepage.user.service.member.MemberHasCommentLikeService;
import keeper.project.homepage.user.service.member.MemberUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  public static final String DELETED_COMMENT_CONTENT = "(삭제된 댓글입니다)";
  public static final Long VIRTUAL_PARENT_COMMENT_ID = 0L;

  private final CommentRepository commentRepository;
  private final PostingRepository postingRepository;
  private final PostingService postingService;
  private final MemberUtilService memberUtilService;
  private final MemberHasCommentLikeService memberHasCommentLikeService;
  private final MemberHasCommentDislikeService memberHasCommentDislikeService;
  private final MemberHasCommentLikeRepository memberHasCommentLikeRepository;
  private final MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;

  private CommentEntity getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(CustomCommentNotFoundException::new);
  }

  private void checkCorrectWriter(Long commentId, Long memberId) {
    CommentEntity comment = getComment(commentId);
    if (comment.getMember().getId().equals(memberId) == false) {
      throw new CustomAuthenticationEntryPointException();
    }
  }

  private void checkNotEmptyContent(CommentDto commentDto) {
    if (commentDto.getContent().isEmpty()) {
      throw new CustomCommentEmptyFieldException("댓글의 내용이 비어있습니다.");
    }
  }

  private boolean checkPushLike(MemberEntity member, CommentEntity comment) {
    return memberHasCommentLikeService.findById(member, comment) == null ? false : true;
  }

  private boolean checkPushDislike(MemberEntity member, CommentEntity comment) {
    return memberHasCommentDislikeService.findById(member, comment) == null ? false : true;
  }

  @Transactional
  public CommentDto save(CommentDto commentDto, Long postId, Long memberId) {
    checkNotEmptyContent(commentDto);

    PostingEntity postingEntity = postingService.getPostingById(postId);
    MemberEntity memberEntity = memberUtilService.getById(memberId);
    CommentEntity commentEntity = commentRepository.save(CommentEntity.builder()
        .content(commentDto.getContent())
        .registerTime(LocalDateTime.now())
        .updateTime(LocalDateTime.now())
        .ipAddress(commentDto.getIpAddress())
        .likeCount(0)
        .dislikeCount(0)
        .parentId(commentDto.getParentId() == null ? 0L : commentDto.getParentId())
        .member(memberEntity)
        .postingId(postingEntity)
        .build());
    commentDto.initWithEntity(commentEntity);

    // NOTE: comment 개수 증가
    postingEntity.increaseCommentCount();
    postingRepository.save(postingEntity);
    return commentDto;
  }

  // FIXME : service에서 jwt확인하면 service test가 모두 먹통이 됨
//  private MemberEntity getMemberByJWT() {
//    Long memberId = authService.getMemberIdByJWT();
//    return memberUtilService.getById(memberId);
//  }

  public List<CommentDto> findAllByPost(Long memberId, Long postId, Pageable pageable) {
    MemberEntity member = memberUtilService.getById(memberId);
    PostingEntity postingEntity = postingService.getPostingById(postId);

    // 조회 검색 조건
    Specification<CommentEntity> commentSpec = CommentSpec.equalParentId(VIRTUAL_PARENT_COMMENT_ID);
    commentSpec = commentSpec.and(CommentSpec.equalPosting(postingEntity));

    List<CommentEntity> commentPage = new ArrayList<>();
    List<CommentEntity> comments = commentRepository.findAll(commentSpec, pageable);

    for (CommentEntity comment : comments) {
      Specification<CommentEntity> replySpec = CommentSpec.equalParentId(comment.getId());
      List<CommentEntity> replies = commentRepository.findAll(replySpec);
      commentPage.add(comment);
      commentPage.addAll(replies);
    }

    boolean isAnonymousCategory = postingEntity.getCategoryId().getName().equals("익명게시판");

    List<CommentDto> dtoPage = new ArrayList<>();
    for (CommentEntity comment : commentPage) {
      CommentDto dto = CommentDto.builder().build();
      if (isAnonymousCategory) {
        dto.initAnonymousWithEntity(comment);
        dto.setCheckedLike(false);
        dto.setCheckedDislike(false);
      } else {
        dto.initWithEntity(comment);
        dto.setCheckedLike(checkPushLike(member, comment));
        dto.setCheckedDislike(checkPushDislike(member, comment));
      }
      dtoPage.add(dto);
    }

    return dtoPage;
  }

  @Transactional
  public CommentDto updateById(CommentDto commentDto, Long commentId, Long memberId) {
    checkCorrectWriter(commentId, memberId);
    checkNotEmptyContent(commentDto);

    CommentEntity updated = getComment(commentId);
    updated.changeContent(commentDto.getContent());
    updated.changeUpdateTime(LocalDateTime.now());
    commentRepository.save(updated);

    commentDto.initWithEntity(updated);
    return commentDto;
  }

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
    comment.overwriteInfo(virtual, DELETED_COMMENT_CONTENT);
    commentRepository.save(comment);
  }

  private void deleteCommentRow(Long commentId) {
    CommentEntity comment = commentRepository.findById(commentId)
        .orElseThrow(CustomCommentNotFoundException::new);

    deleteCommentFK(comment);
    commentRepository.delete(comment);
  }

  @Transactional
  public void deleteByPostingId(PostingEntity posting) {
    List<CommentEntity> commentEntities = commentRepository.findAllByPostingId(posting);
    for (CommentEntity comment : commentEntities) {
      deleteCommentRow(comment.getId());
    }
  }

  @Transactional
  public void deleteByWriter(Long memberId, Long commentId) {
    checkCorrectWriter(commentId, memberId);
    deleteComment(commentId);
  }

  @Transactional
  public void updateLikeCount(Long memberId, Long commentId) {
    MemberEntity memberEntity = memberUtilService.getById(memberId);
    CommentEntity commentEntity = getComment(commentId);
    if (memberHasCommentLikeService.findById(memberEntity, commentEntity) == null) {
      memberHasCommentLikeService.saveWithMemberAndCommentEntity(memberEntity, commentEntity);
      increaseLikeCount(commentEntity);
    } else {
      memberHasCommentLikeService.deleteByMemberAndCommentEntity(memberEntity, commentEntity);
      decreaseLikeCount(commentEntity);
    }
  }

  @Transactional
  public void updateDislikeCount(Long memberId, Long commentId) {
    MemberEntity memberEntity = memberUtilService.getById(memberId);
    CommentEntity commentEntity = getComment(commentId);
    if (memberHasCommentDislikeService.findById(memberEntity, commentEntity) == null) {
      memberHasCommentDislikeService.saveWithMemberAndCommentEntity(memberEntity, commentEntity);
      increaseDislikeCount(commentEntity);
    } else {
      memberHasCommentDislikeService.deleteByMemberAndCommentEntity(memberEntity, commentEntity);
      decreaseDislikeCount(commentEntity);
    }
  }

  private void checkNotMaxValue(Integer num) {
    if (num == Integer.MAX_VALUE) {
      throw new CustomNumberOverflowException();
    }
  }

  private void increaseLikeCount(CommentEntity updated) {
    checkNotMaxValue(updated.getLikeCount());

    updated.increaseLikeCount();
    commentRepository.save(updated);
  }

  private void decreaseLikeCount(CommentEntity updated) {
    updated.decreaseLikeCount();
    commentRepository.save(updated);
  }

  private void increaseDislikeCount(CommentEntity updated) {
    checkNotMaxValue(updated.getDislikeCount());

    updated.increaseDislikeCount();
    commentRepository.save(updated);
  }

  private void decreaseDislikeCount(CommentEntity updated) {
    updated.decreaseDislikeCount();
    commentRepository.save(updated);
  }
}