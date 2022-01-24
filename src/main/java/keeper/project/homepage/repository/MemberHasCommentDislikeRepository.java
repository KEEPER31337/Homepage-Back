package keeper.project.homepage.repository;

import java.util.List;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.MemberHasCommentDislikeEntity;
import keeper.project.homepage.entity.MemberHasCommentEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberHasCommentDislikeRepository extends
    JpaRepository<MemberHasCommentDislikeEntity, MemberHasCommentEntityPK> {

  List<MemberHasCommentDislikeEntity> findByMemberHasCommentEntityPK_CommentEntity(
      CommentEntity commentEntity);

  List<MemberHasCommentDislikeEntity> findByMemberHasCommentEntityPK_MemberEntity(
      MemberEntity memberEntity);

  void deleteByMemberHasCommentEntityPK_CommentEntity(CommentEntity commentEntity);

  void deleteByMemberHasCommentEntityPK_MemberEntity(MemberEntity memberEntity);
}
