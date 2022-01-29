package keeper.project.homepage.service.posting;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.posting.CommentDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
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

  private final CommentRepository commentRepository;
  private final PostingService postingService;
  private final MemberService memberService;
  private final MemberHasCommentLikeService memberHasCommentLikeService;
  private final MemberHasCommentDislikeService memberHasCommentDislikeService;

  @Transactional
  public CommentDto save(CommentDto commentDto, Long postId) {
    PostingEntity postingEntity = postingService.getPostingById(postId);
    MemberEntity memberEntity = memberService.findById(commentDto.getMemberId());
    commentDto.setLikeCount(0);
    commentDto.setDislikeCount(0);
    commentDto.setRegisterTime(LocalDate.now());
    commentDto.setUpdateTime(LocalDate.now());
    if (commentDto.getParentId() == null) {
      commentDto.setParentId(0L);
    }
    CommentEntity commentEntity = commentRepository.save(
        commentDto.toEntity(postingEntity, memberEntity));
    commentDto.setId(commentEntity.getId());
    commentDto.setPostingId(postId);
    return commentDto;
  }

  public List<CommentDto> findAllByPost(Long postId, Pageable pageable) {
    PostingEntity postingEntity = postingService.getPostingById(postId);
    Page<CommentEntity> entityPage = commentRepository.findAllByPostingId(postingEntity, pageable);

    List<CommentDto> dtoPage = new ArrayList<>();
    entityPage.getContent().forEach(content -> dtoPage.add(
        new CommentDto(content.getId(), content.getContent(), content.getRegisterTime(),
            content.getUpdateTime(), content.getIpAddress(), content.getLikeCount(),
            content.getDislikeCount(), content.getParentId(), content.getMemberId().getId(),
            postId)));

    return dtoPage;
  }

  @Transactional
  public CommentEntity findById(Long id) {
    Optional<CommentEntity> commentEntity = commentRepository.findById(id);
    return commentEntity.orElse(null);
  }

  @Transactional
  public CommentDto updateById(CommentDto commentDto, Long commentId) {
    CommentEntity original = this.findById(commentId);
    if (original == null) {
      return null;
    }
    commentDto.setId(commentId);
    commentDto.setLikeCount(original.getLikeCount());
    commentDto.setDislikeCount(original.getDislikeCount());
    commentDto.setUpdateTime(LocalDate.now());
    commentDto.setIpAddress(original.getIpAddress());

    commentRepository.save(
        commentDto.toEntity(original.getPostingId(), original.getMemberId()));

    return commentDto;
  }

  @Transactional
  public void deleteById(Long id) {
    commentRepository.deleteById(id);
  }

  @Transactional
  public void updateLikeCount(Long memberId, Long commentId) {
    MemberEntity memberEntity = memberService.findById(memberId);
    CommentEntity commentEntity = this.findById(commentId);
    if (memberHasCommentLikeService.findById(memberEntity, commentEntity) == null) {
      memberHasCommentLikeService.saveWithMemberAndCommentEntity(memberEntity, commentEntity);
      increaseLikeCount(commentId);
    } else {
      memberHasCommentLikeService.deleteByMemberAndCommentEntity(memberEntity, commentEntity);
      decreaseLikeCount(commentId);
    }
  }

  @Transactional
  public void updateDislikeCount(Long memberId, Long commentId) {
    MemberEntity memberEntity = memberService.findById(memberId);
    CommentEntity commentEntity = this.findById(commentId);
    if (memberHasCommentDislikeService.findById(memberEntity, commentEntity) == null) {
      memberHasCommentDislikeService.saveWithMemberAndCommentEntity(memberEntity, commentEntity);
      increaseDislikeCount(commentId);
    } else {
      memberHasCommentDislikeService.deleteByMemberAndCommentEntity(memberEntity, commentEntity);
      decreaseDislikeCount(commentId);
    }
  }

  private void increaseLikeCount(Long id) {
    CommentEntity updated = this.findById(id);
    updated.increaseLikeCount();
    commentRepository.save(updated);
  }

  private void decreaseLikeCount(Long id) {
    CommentEntity updated = this.findById(id);
    updated.decreaseLikeCount();
    commentRepository.save(updated);
  }

  private void increaseDislikeCount(Long id) {
    CommentEntity updated = this.findById(id);
    updated.increaseDislikeCount();
    commentRepository.save(updated);
  }

  private void decreaseDislikeCount(Long id) {
    CommentEntity updated = this.findById(id);
    updated.decreaseDislikeCount();
    commentRepository.save(updated);
  }
}