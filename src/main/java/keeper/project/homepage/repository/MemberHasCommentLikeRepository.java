package keeper.project.homepage.repository;

import java.util.List;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.MemberHasCommentEntityPK;
import keeper.project.homepage.entity.MemberHasCommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberHasCommentLikeRepository extends
    JpaRepository<MemberHasCommentLikeEntity, MemberHasCommentEntityPK> {

  List<MemberHasCommentLikeEntity> findByMemberHasCommentEntityPK_CommentEntity(
      CommentEntity commentEntity);

  List<MemberHasCommentLikeEntity> findByMemberHasCommentEntityPK_MemberEntity(
      MemberEntity memberEntity);

  void deleteByMemberHasCommentEntityPK_CommentEntity(CommentEntity commentEntity);

  void deleteByMemberHasCommentEntityPK_MemberEntity(MemberEntity memberEntity);
}
