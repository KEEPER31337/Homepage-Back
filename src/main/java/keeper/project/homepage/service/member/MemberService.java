package keeper.project.homepage.service.member;

import java.lang.reflect.Member;
import java.util.Optional;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  public MemberEntity findById(Long id) throws RuntimeException {
    return memberRepository.findById(id).orElseThrow(CustomMemberNotFoundException::new);
  }

  // update realName, nickName
  public MemberDto updateNames(MemberDto memberDto, Long memberId) throws RuntimeException {
    MemberEntity updateEntity = memberRepository.findById(memberId)
        .orElseThrow(CustomMemberNotFoundException::new);
    if (updateEntity == null) {
      return null;
    }
    if (!memberDto.getRealName().isEmpty()) {
      updateEntity.changeRealName(memberDto.getRealName());
    }
    if (!memberDto.getNickName().isEmpty()) {
      updateEntity.changeNickName(memberDto.getNickName());
    }
    memberDto.initWithEntity(memberRepository.save(updateEntity));
    return memberDto;
  }

  public MemberDto updateThumbnails(Long memberId,
      ThumbnailEntity thumbnailEntity) throws RuntimeException {
    MemberEntity updateEntity = memberRepository.findById(memberId)
        .orElseThrow(CustomMemberNotFoundException::new);
    if (updateEntity == null) {
      return null;
    }
    updateEntity.changeThumbnail(thumbnailEntity);

    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateEntity));
    return result;
  }
}
