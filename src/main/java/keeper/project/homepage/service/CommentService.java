package keeper.project.homepage.service;

import java.util.List;
import java.util.Optional;
import javax.xml.stream.events.Comment;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.exception.CustomCommentNotFoundException;
import keeper.project.homepage.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

  @Autowired
  private CommentRepository commentRepository;

  public CommentEntity save(CommentEntity commentEntity) {
    return commentRepository.save(commentEntity);
  }

  public List<CommentEntity> commentViewAll() {
    return commentRepository.findAll();
  }

  public Page<CommentEntity> findAllByPost(PostingEntity postingEntity, Pageable pageable) {
    return commentRepository.findAllByPostingId(postingEntity, pageable);
  }

  public Page<CommentEntity> findAllByParentIdAndPost(Long parentId, PostingEntity postingEntity,
      Pageable pageable) {
    return commentRepository.findAllByParentIdAndPostingId(parentId, postingEntity, pageable);
  }

  public CommentEntity findById(Long id) throws RuntimeException {
    Optional<CommentEntity> commentEntity = commentRepository.findById(id);
    if (commentEntity.isPresent()) {
      return commentEntity.get();
    }
    throw new CustomCommentNotFoundException("Can't find any comment under giver ID");
  }

  public CommentEntity updateById(Long requestId, CommentEntity request) {
    CommentEntity tempComment = findById(requestId);
    tempComment.changeProperties(request);
    return commentRepository.save(tempComment);
  }

  public void deleteById(Long id) {
    commentRepository.deleteById(id);
  }

  public CommentEntity increaseLikeCount(Long id) {
    CommentEntity updated = this.findById(id);
    updated.increaseLikeCount();
    return commentRepository.save(updated);
  }

  public CommentEntity decreaseLikeCount(Long id) {
    CommentEntity updated = this.findById(id);
    updated.decreaseLikeCount();
    return commentRepository.save(updated);
  }

  public CommentEntity increaseDislikeCount(Long id) {
    CommentEntity updated = this.findById(id);
    updated.increaseDislikeCount();
    return commentRepository.save(updated);
  }

  public CommentEntity decreaseDislikeCount(Long id) {
    CommentEntity updated = this.findById(id);
    updated.decreaseDislikeCount();
    return commentRepository.save(updated);
  }
}