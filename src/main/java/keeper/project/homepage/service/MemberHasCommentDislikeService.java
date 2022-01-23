package keeper.project.homepage.service;

import java.util.Optional;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.MemberHasCommentDislikeEntity;
import keeper.project.homepage.entity.identifier.MemberHasCommentDislikeId;
import keeper.project.homepage.repository.MemberHasCommentDislikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberHasCommentDislikeService {

  @Autowired
  private MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;

  public MemberHasCommentDislikeEntity save(
      MemberHasCommentDislikeEntity memberHasCommentDislikeEntity) {
    return memberHasCommentDislikeRepository.save(memberHasCommentDislikeEntity);
  }

  public MemberHasCommentDislikeEntity saveWithMemberAndCommentEntity(MemberEntity memberEntity,
      CommentEntity commentEntity) {
    return memberHasCommentDislikeRepository.save(MemberHasCommentDislikeEntity.builder()
        .memberHasCommentDislikeId(new MemberHasCommentDislikeId(memberEntity, commentEntity))
        .build());
  }

  public MemberHasCommentDislikeEntity findById(MemberEntity memberEntity,
      CommentEntity commentEntity) {
    Optional<MemberHasCommentDislikeEntity> findMHCD = memberHasCommentDislikeRepository.findById(
        new MemberHasCommentDislikeId(memberEntity, commentEntity));
    return findMHCD.orElse(null);
  }

  public void deleteByMemberAndCommentEntity(MemberEntity memberEntity,
      CommentEntity commentEntity) {
    memberHasCommentDislikeRepository.deleteById(
        new MemberHasCommentDislikeId(memberEntity, commentEntity));
  }
}
