package keeper.project.homepage.repository;

import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.MemberHasPostingLikeEntity;
import keeper.project.homepage.entity.PostingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingLikeRepository extends
    JpaRepository<MemberHasPostingLikeEntity, MemberEntity> {

  public void deleteByMemberIdAndPostingId(MemberEntity memberEntity, PostingEntity postingEntity);
}