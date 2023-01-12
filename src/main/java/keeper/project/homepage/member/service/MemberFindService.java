package keeper.project.homepage.member.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.member.dto.response.MultiMemberResponseDto;
import keeper.project.homepage.member.dto.response.OtherMemberInfoResponseDto;
import keeper.project.homepage.member.dto.response.UserMemberResponseDto;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity.MemberType;
import keeper.project.homepage.member.exception.CustomAccessVirtualMemberException;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberFindService {

  public static final Long VIRTUAL_MEMBER_ID = 1L;

  private final MemberRepository memberRepository;

  public List<OtherMemberInfoResponseDto> getOtherMembers(MemberEntity loginMember) {
    List<OtherMemberInfoResponseDto> results = new ArrayList<>();
    List<MemberEntity> members = memberRepository.findAll();

    for (MemberEntity other : members) {
      if (other.getMemberType() != null
          && other.getMemberType().getId() == MemberType.WITHDRAWAL_MEMBER.getId()) {
        continue;
      }
      if (other.getId().equals(VIRTUAL_MEMBER_ID)) {
        continue;
      }

      OtherMemberInfoResponseDto response = OtherMemberInfoResponseDto.from(other);
      response.setCheckFollow(loginMember.isMyFollowee(other), loginMember.isMyFollower(other));
      results.add(response);
    }

    return results;
  }

  public void checkVirtualMember(Long id) {
    if (id.equals(VIRTUAL_MEMBER_ID)) {
      throw new CustomAccessVirtualMemberException();
    }
  }

  public OtherMemberInfoResponseDto getOtherMember(MemberEntity loginMember, Long otherMemberId) {
    checkVirtualMember(otherMemberId);
    MemberEntity other = memberRepository.findById(otherMemberId)
        .orElseThrow(() -> new CustomMemberNotFoundException(otherMemberId));
    OtherMemberInfoResponseDto response = OtherMemberInfoResponseDto.from(other);
    response.setCheckFollow(loginMember.isMyFollowee(other), loginMember.isMyFollower(other));
    return response;
  }

  public List<MultiMemberResponseDto> getMultiMembers(List<Long> ids) {
    List<MultiMemberResponseDto> results = new ArrayList<>();

    for (Long id : ids) {
      Optional<MemberEntity> findMember = memberRepository.findById(id);
      if (findMember.isPresent()) {
        MemberEntity member = findMember.get();
        if (member.getId().equals(VIRTUAL_MEMBER_ID)) {
          results.add(
              MultiMemberResponseDto.builder().id(id).msg("Fail: Access Virtual Member").build());
        } else {
          results.add(MultiMemberResponseDto.from(member));
        }
      } else {
        results.add(MultiMemberResponseDto.builder().id(id).msg("Fail: Not Exist Member").build());
      }
    }

    return results;
  }

  public UserMemberResponseDto getMember(Long id) {
    MemberEntity memberEntity = memberRepository.findById(id)
        .orElseThrow(() -> new CustomMemberNotFoundException(id));

    return new UserMemberResponseDto(memberEntity);
  }
}
