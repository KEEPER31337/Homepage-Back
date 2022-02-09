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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  private CommentEntity getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));
  }

  private void isValidMember(Long commentId, Long memberId) {
    CommentEntity comment = getComment(commentId);
    if (comment.getMemberId().getId().equals(memberId) == false) {
      throw new RuntimeException(
          "작성한 회원 : " + comment.getMemberId().getId().toString()
              + ", 요청한 회원 : " + memberId.toString() + ", 해당 댓글에 대한 동작을 수행할 권한이 없습니다.");
    }
  }

  private void isNotEmptyContent(CommentDto commentDto) {
    if (commentDto.getContent().isEmpty()) {
      throw new RuntimeException("댓글의 내용이 비어있습니다.");
    }
  }

  @Transactional
  public CommentDto save(CommentDto commentDto, Long postId, Long memberId) {

    isNotEmptyContent(commentDto);

    PostingEntity postingEntity = postingService.getPostingById(postId);
    MemberEntity memberEntity = memberService.findById(memberId);
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
//    commentDto.setPostingId(postId);
    return commentDto;
  }

  public List<CommentDto> findAllByPost(Long postId, Pageable pageable) {
    PostingEntity postingEntity = postingService.getPostingById(postId);
    Page<CommentEntity> entityPage = commentRepository.findAllByPostingId(postingEntity, pageable);

    List<CommentDto> dtoPage = new ArrayList<>();
    for (CommentEntity comment : entityPage.getContent()) {
      CommentDto dto = CommentDto.builder()
          .content(comment.getContent())
          .registerTime(comment.getRegisterTime())
          .updateTime(comment.getUpdateTime())
          .ipAddress(comment.getIpAddress())
          .likeCount(comment.getLikeCount())
          .dislikeCount(comment.getDislikeCount())
          .parentId(comment.getParentId())
          .build();
      dtoPage.add(dto);
    }

    return dtoPage;
  }

  @Transactional
  public CommentDto updateById(CommentDto commentDto, Long commentId, Long memberId) {
    isValidMember(commentId, memberId);
    isNotEmptyContent(commentDto);

    CommentEntity original = getComment(commentId);
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
  public void deleteById(Long id, Long memberId) {
    isValidMember(id, memberId);

    commentRepository.deleteById(id);
    if (commentRepository.findById(id).isPresent()) {
      throw new RuntimeException("댓글 삭제를 실패했습니다.");
    }
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

  private void increaseLikeCount(CommentEntity updated) {
    updated.increaseLikeCount();
    commentRepository.save(updated);
  }

  private void decreaseLikeCount(CommentEntity updated) {
    updated.decreaseLikeCount();
    commentRepository.save(updated);
  }

  private void increaseDislikeCount(CommentEntity updated) {
    updated.increaseDislikeCount();
    commentRepository.save(updated);
  }

  private void decreaseDislikeCount(CommentEntity updated) {
    updated.decreaseDislikeCount();
    commentRepository.save(updated);
  }
}