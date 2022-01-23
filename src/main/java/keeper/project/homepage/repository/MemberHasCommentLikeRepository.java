package keeper.project.homepage.repository;

import java.util.List;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.MemberHasCommentLikeEntity;
import keeper.project.homepage.entity.identifier.MemberHasCommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberHasCommentLikeRepository extends
    JpaRepository<MemberHasCommentLikeEntity, MemberHasCommentLikeId> {

  List<MemberHasCommentLikeEntity> findByMemberHasCommentLikeId_CommentEntity(
      CommentEntity commentEntity);

  List<MemberHasCommentLikeEntity> findByMemberHasCommentLikeId_MemberEntity(
      MemberEntity memberEntity);

  void deleteByMemberHasCommentLikeId_CommentEntity(CommentEntity commentEntity);

  void deleteByMemberHasCommentLikeId_MemberEntity(MemberEntity memberEntity);
}
