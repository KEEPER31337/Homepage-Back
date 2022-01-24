package keeper.project.homepage.repository;

import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.PostingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingDislikeRepository extends
    JpaRepository<MemberHasPostingDislikeEntity, MemberEntity> {

  public void deleteByMemberIdAndPostingId(MemberEntity memberEntity, PostingEntity postingEntity);
}