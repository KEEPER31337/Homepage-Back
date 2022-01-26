package keeper.project.homepage.repository.member;

import keeper.project.homepage.entity.member.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingDislikeRepository extends
    JpaRepository<MemberHasPostingDislikeEntity, MemberEntity> {

  public void deleteByMemberIdAndPostingId(MemberEntity memberEntity, PostingEntity postingEntity);
}