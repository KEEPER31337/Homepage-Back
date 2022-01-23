package keeper.project.homepage.repository;

import java.util.List;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.MemberHasCommentDislikeEntity;
import keeper.project.homepage.entity.identifier.MemberHasCommentDislikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberHasCommentDislikeRepository extends
    JpaRepository<MemberHasCommentDislikeEntity, MemberHasCommentDislikeId> {

  List<MemberHasCommentDislikeEntity> findByMemberHasCommentDislikeId_CommentEntity(
      CommentEntity commentEntity);

  List<MemberHasCommentDislikeEntity> findByMemberHasCommentDislikeId_MemberEntity(
      MemberEntity memberEntity);

  void deleteByMemberHasCommentDislikeId_CommentEntity(CommentEntity commentEntity);

  void deleteByMemberHasCommentDislikeId_MemberEntity(MemberEntity memberEntity);
}
