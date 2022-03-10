package keeper.project.homepage.repository.member;

import java.util.List;
import keeper.project.homepage.entity.member.MemberHasPostingLikeEntity;
import keeper.project.homepage.common.entity.posting.PostingEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingLikeRepository extends
    JpaRepository<MemberHasPostingLikeEntity, MemberEntity> {

  void deleteByMemberIdAndPostingId(MemberEntity memberEntity, PostingEntity postingEntity);

  List<MemberHasPostingLikeEntity> findByMemberId(MemberEntity memberEntity);
}