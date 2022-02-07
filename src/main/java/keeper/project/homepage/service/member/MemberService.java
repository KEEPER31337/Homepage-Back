package keeper.project.homepage.service.member;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.FriendRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final FriendRepository friendRepository;

  public MemberEntity findById(Long id) throws RuntimeException {
    Optional<MemberEntity> memberEntity = memberRepository.findById(id);
    return memberEntity.orElse(null);
  }

  public void follow(Long myId, String followLoginId) {
    MemberEntity me = memberRepository.findById(myId)
        .orElseThrow(() -> new CustomMemberNotFoundException(
            myId.toString() + "인 id를 가진 member를 찾지 못했습니다."));
    MemberEntity followee = memberRepository.findByLoginId(followLoginId)
        .orElseThrow(() -> new CustomMemberNotFoundException(
            followLoginId + "인 login id를 가진 member를 찾지 못했습니다."));

    FriendEntity friend = FriendEntity.builder()
        .follower(me)
        .followee(followee)
        .registerDate(new Date())
        .build();
    friendRepository.save(friend);

    me.getFollowee().add(friend);
    followee.getFollower().add(friend);
  }

  public void unfollow(Long myId, String followLoginId) {
    MemberEntity me = memberRepository.findById(myId)
        .orElseThrow(CustomMemberNotFoundException::new);
    MemberEntity followee = memberRepository.findByLoginId(followLoginId)
        .orElseThrow(CustomMemberNotFoundException::new);

    FriendEntity friend = friendRepository.findByFolloweeAndFollower(followee, me);
    me.getFollowee().remove(friend);
    followee.getFollower().remove(friend);
    friendRepository.delete(friend);
  }

  public List<MemberDto> showFollower(Long myId) {
    MemberEntity me = memberRepository.findById(myId)
        .orElseThrow(CustomMemberNotFoundException::new);
    List<FriendEntity> friendList = me.getFollower();

    List<MemberDto> followerList = friendList.stream()
        .map(friend -> MemberDto.initWithEntity(friend.getFollower())).toList();
    return followerList;
  }

  public List<MemberDto> showFollowee(Long myId) {
    MemberEntity me = memberRepository.findById(myId)
        .orElseThrow(CustomMemberNotFoundException::new);
    List<FriendEntity> friendList = me.getFollowee();

    List<MemberDto> followeeList = friendList.stream()
        .map(friend -> MemberDto.initWithEntity(friend.getFollowee())).toList();
    return followeeList;
  }
}
