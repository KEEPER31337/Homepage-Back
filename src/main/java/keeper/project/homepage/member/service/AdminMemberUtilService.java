package keeper.project.homepage.member.service;

import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.entity.MemberRankEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.member.exception.CustomMemberInfoNotFoundException;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.member.repository.MemberJobRepository;
import keeper.project.homepage.member.repository.MemberRankRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.member.repository.MemberTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMemberUtilService {

  private final MemberRepository memberRepository;
  private final MemberRankRepository memberRankRepository;
  private final MemberTypeRepository memberTypeRepository;
  private final MemberJobRepository memberJobRepository;

  public MemberEntity getMemberById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomMemberNotFoundException(memberId));
  }

  public MemberEntity getByLoginId(String loginId) {
    return memberRepository.findByLoginId(loginId).orElseThrow(
        () -> new CustomMemberNotFoundException("loginId가 " + loginId + " 인 회원을 찾을 수 없습니다."));
  }

  public MemberRankEntity getByRankName(String name) {
    return memberRankRepository.findByName(name).orElseThrow(
        () -> new CustomMemberInfoNotFoundException(name + "인 MemberRankEntity가 존재하지 않습니다."));
  }

  public MemberTypeEntity getByTypeName(String name) {
    return memberTypeRepository.findByName(name).orElseThrow(
        () -> new CustomMemberInfoNotFoundException(name + "인 MemberTypeEntity가 존재하지 않습니다."));
  }

  public MemberJobEntity getJobById(Long jobId) {
    return memberJobRepository.findById(jobId).orElseThrow(
        () -> new CustomMemberInfoNotFoundException("ID가 " + jobId + "인 MemberJob이 존재하지 않습니다."));
  }

  public MemberJobEntity getByJobName(String name) {
    return memberJobRepository.findByName(name).orElseThrow(
        () -> new CustomMemberInfoNotFoundException(name + "인 MemberJobEntity가 존재하지 않습니다."));
  }

}
