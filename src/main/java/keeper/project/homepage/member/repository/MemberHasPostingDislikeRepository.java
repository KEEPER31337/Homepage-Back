package keeper.project.homepage.member.repository;

import java.util.List;
import keeper.project.homepage.member.entity.MemberHasPostingDislikeEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingDislikeRepository extends
    JpaRepository<MemberHasPostingDislikeEntity, MemberEntity> {

  void deleteByMemberIdAndPostingId(MemberEntity memberEntity, PostingEntity postingEntity);

  List<MemberHasPostingDislikeEntity> findByMemberId(MemberEntity memberEntity);
}