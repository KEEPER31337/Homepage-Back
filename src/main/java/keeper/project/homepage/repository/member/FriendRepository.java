package keeper.project.homepage.repository.member;

import keeper.project.homepage.member.entity.FriendEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

  FriendEntity findByFolloweeAndFollower(MemberEntity followee, MemberEntity follower);
}
