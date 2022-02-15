package keeper.project.homepage.service.posting;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.dto.posting.CommentDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasCommentDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasCommentLikeEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.exception.CustomAuthenticationEntryPointException;
import keeper.project.homepage.exception.CustomNumberOverflowException;
import keeper.project.homepage.exception.posting.CustomCommentEmptyFieldException;
import keeper.project.homepage.exception.posting.CustomCommentNotFoundException;
import keeper.project.homepage.repository.member.MemberHasCommentDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasCommentLikeRepository;
import keeper.project.homepage.repository.posting.CommentRepository;
import keeper.project.homepage.service.member.MemberHasCommentDislikeService;
import keeper.project.homepage.service.member.MemberHasCommentLikeService;
import keeper.project.homepage.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  public static final String DELETED_COMMENT_CONTENT = "(삭제된 댓글입니다)";
  private final CommentRepository commentRepository;
  private final PostingService postingService;
  private final MemberService memberService;
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

  @Transactional
  public CommentDto save(CommentDto commentDto, Long postId, Long memberId) {
    checkNotEmptyContent(commentDto);

    PostingEntity postingEntity = postingService.getPostingById(postId);
    MemberEntity memberEntity = memberService.findById(memberId);
    CommentEntity commentEntity = commentRepository.save(CommentEntity.builder()
        .content(commentDto.getContent())
        .registerTime(LocalDate.now())
        .updateTime(LocalDate.now())
        .ipAddress(commentDto.getIpAddress())
        .likeCount(0)
        .dislikeCount(0)
        .parentId(commentDto.getParentId() == null ? 0L : commentDto.getParentId())
        .member(memberEntity)
        .postingId(postingEntity)
        .build());
    commentDto.initWithEntity(commentEntity);
    return commentDto;
  }

  public List<CommentDto> findAllByPost(Long postId, Pageable pageable) {
    PostingEntity postingEntity = postingService.getPostingById(postId);
    Page<CommentEntity> entityPage = commentRepository.findAllByPostingId(postingEntity, pageable);

    List<CommentDto> dtoPage = new ArrayList<>();
    for (CommentEntity comment : entityPage.getContent()) {
      CommentDto dto = CommentDto.builder().build();
      dto.initWithEntity(comment);
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
    updated.changeUpdateTime(LocalDate.now());
    commentRepository.save(updated);

    commentDto.initWithEntity(updated);
    return commentDto;
  }

  public void deleteCommentLike(CommentEntity comment) {
    memberHasCommentLikeRepository.deleteByMemberHasCommentEntityPK_CommentEntity(comment);
  }

  public void deleteCommentDislike(CommentEntity comment) {
    memberHasCommentDislikeRepository.deleteByMemberHasCommentEntityPK_CommentEntity(comment);
  }

  @Transactional
  public void deleteById(Long memberId, Long id) {
    checkCorrectWriter(id, memberId);
    CommentEntity comment = commentRepository.findById(id)
        .orElseThrow(CustomCommentNotFoundException::new);
    MemberEntity virtual = memberService.findById(1L);

    deleteCommentLike(comment);
    deleteCommentDislike(comment);
    comment.initInfo(virtual, DELETED_COMMENT_CONTENT);
    commentRepository.save(comment);
  }

  @Transactional
  public void updateLikeCount(Long memberId, Long commentId) {
    MemberEntity memberEntity = memberService.findById(memberId);
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
    MemberEntity memberEntity = memberService.findById(memberId);
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