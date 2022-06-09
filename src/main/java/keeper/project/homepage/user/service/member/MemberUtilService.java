package keeper.project.homepage.user.service.member;

import java.util.List;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.member.CustomAccessVirtualMemberException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberUtilService {

  public static final int EMAIL_AUTH_CODE_LENGTH = 10;
  public static final Long VIRTUAL_MEMBER_ID = 1L;

  private final AuthService authService;
  private final MemberRepository memberRepository;

  public MemberEntity getById(Long id) {
    return memberRepository.findById(id).orElseThrow(CustomMemberNotFoundException::new);
  }

  public void checkVirtualMember(Long id) {
    if (id.equals(VIRTUAL_MEMBER_ID)) {
      throw new CustomAccessVirtualMemberException();
    }
  }

  public Boolean isMyFollowee(MemberEntity other) {
    MemberEntity me = authService.getMemberEntityWithJWT();
    List<FriendEntity> followeeList = me.getFollowee();
    for (FriendEntity followee : followeeList) {
      if (followee.getFollowee().equals(other)) {
        return true;
      }
    }
    return false;
  }

  public Boolean isMyFollower(MemberEntity other) {
    MemberEntity me = authService.getMemberEntityWithJWT();
    List<FriendEntity> followerList = me.getFollower();
    for (FriendEntity follower : followerList) {
      if (follower.getFollower().equals(other)) {
        return true;
      }
    }
    return false;
  }
}
