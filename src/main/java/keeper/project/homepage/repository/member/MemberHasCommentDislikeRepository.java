package keeper.project.homepage.repository.member;

import java.util.List;
import keeper.project.homepage.posting.entity.CommentEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasCommentDislikeEntity;
import keeper.project.homepage.member.entity.MemberHasCommentEntityPK;
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
