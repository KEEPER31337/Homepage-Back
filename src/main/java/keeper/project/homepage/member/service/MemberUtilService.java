package keeper.project.homepage.member.service;

import java.util.List;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.member.entity.FriendEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.member.exception.CustomAccessVirtualMemberException;
import keeper.project.homepage.member.exception.CustomMemberInfoNotFoundException;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.member.repository.MemberJobRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.member.repository.MemberTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberUtilService {

  public static final int EMAIL_AUTH_CODE_LENGTH = 10;
  public static final Long VIRTUAL_MEMBER_ID = 1L;

  private final AuthService authService;
  private final MemberRepository memberRepository;
  private final MemberJobRepository memberJobRepository;
  private final MemberTypeRepository memberTypeRepository;

  public MemberEntity getById(Long id) {
    return memberRepository.findById(id).orElseThrow(() -> new CustomMemberNotFoundException(id));
  }

  public MemberJobEntity getJobById(Long jobId) {
    return memberJobRepository.findById(jobId).orElseThrow(
        () -> new CustomMemberInfoNotFoundException("ID가 " + jobId + "인 MemberJob이 존재하지 않습니다."));
  }

  public MemberTypeEntity getTypeById(Long typeId) {
    return memberTypeRepository.findById(typeId).orElseThrow(
        () -> new CustomMemberInfoNotFoundException("ID가 " + typeId + "인 MemberType이 존재하지 않습니다."));
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
