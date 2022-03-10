package keeper.project.homepage.repository.member;

import java.util.List;
import keeper.project.homepage.entity.member.MemberHasPostingDislikeEntity;
import keeper.project.homepage.common.entity.posting.PostingEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingDislikeRepository extends
    JpaRepository<MemberHasPostingDislikeEntity, MemberEntity> {

  void deleteByMemberIdAndPostingId(MemberEntity memberEntity, PostingEntity postingEntity);

  List<MemberHasPostingDislikeEntity> findByMemberId(MemberEntity memberEntity);
}