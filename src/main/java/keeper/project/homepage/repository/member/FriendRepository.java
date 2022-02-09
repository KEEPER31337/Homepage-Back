package keeper.project.homepage.repository.member;

import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

  FriendEntity findByFolloweeAndFollower(MemberEntity followee, MemberEntity follower);
}
