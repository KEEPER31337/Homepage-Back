package keeper.project.homepage.member.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.member.dto.response.MemberFollowResponseDto;
import keeper.project.homepage.member.dto.response.UserMemberResponseDto;
import keeper.project.homepage.member.entity.FriendEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.member.repository.FriendRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberFollowService {

  private final MemberRepository memberRepository;
  private final FriendRepository friendRepository;

  private int getFolloweeNumber(MemberEntity member) {
    return member.getFollowee().size();
  }

  private int getFollowerNumber(MemberEntity member) {
    return member.getFollower().size();
  }

  @Transactional
  public void follow(Long myId, Long memberId) {
    MemberEntity me = memberRepository.findById(myId)
        .orElseThrow(() -> new CustomMemberNotFoundException(myId));
    MemberEntity followee = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomMemberNotFoundException(memberId));

    FriendEntity friend = FriendEntity.builder()
        .follower(me)
        .followee(followee)
        .registerDate(LocalDate.now())
        .build();
    friendRepository.save(friend);

    me.getFollowee().add(friend);
    followee.getFollower().add(friend);
  }

  @Transactional
  public void unfollow(Long myId, Long memberId) {
    MemberEntity me = memberRepository.findById(myId)
        .orElseThrow(() -> new CustomMemberNotFoundException(myId));
    MemberEntity followee = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomMemberNotFoundException(memberId));

    FriendEntity friend = friendRepository.findByFolloweeAndFollower(followee, me);
    me.getFollowee().remove(friend);
    followee.getFollower().remove(friend);
    friendRepository.delete(friend);
  }

  public List<UserMemberResponseDto> showFollower(Long myId) {
    MemberEntity me = memberRepository.findById(myId)
        .orElseThrow(() -> new CustomMemberNotFoundException(myId));
    List<FriendEntity> friendList = me.getFollower();

    List<UserMemberResponseDto> followerList = new ArrayList<>();
    for (FriendEntity friend : friendList) {
      UserMemberResponseDto follower = UserMemberResponseDto.builder().build();
      follower.initWithEntity(friend.getFollower());
      followerList.add(follower);
    }
    return followerList;
  }

  public List<UserMemberResponseDto> showFollowee(Long myId) {
    MemberEntity me = memberRepository.findById(myId)
        .orElseThrow(() -> new CustomMemberNotFoundException(myId));
    List<FriendEntity> friendList = me.getFollowee();

    List<UserMemberResponseDto> followeeList = new ArrayList<>();
    for (FriendEntity friend : friendList) {
      UserMemberResponseDto followee = UserMemberResponseDto.builder().build();
      followee.initWithEntity(friend.getFollowee());
      followeeList.add(followee);
    }
    return followeeList;
  }

  public MemberFollowResponseDto getFollowerAndFolloweeNumber(Long id) {
    MemberEntity member = memberRepository.findById(id)
        .orElseThrow(() -> new CustomMemberNotFoundException(id));
    return MemberFollowResponseDto.builder()
        .followeeNumber(getFolloweeNumber(member))
        .followerNumber(getFollowerNumber(member))
        .build();
  }
}
