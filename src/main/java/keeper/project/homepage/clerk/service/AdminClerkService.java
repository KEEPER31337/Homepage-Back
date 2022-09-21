package keeper.project.homepage.clerk.service;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.clerk.dto.request.ClerkMemberTypeRequestDto;
import keeper.project.homepage.clerk.dto.response.ClerkMemberJobTypeResponseDto;
import keeper.project.homepage.clerk.dto.response.TypeResponseDto;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.member.repository.MemberTypeRepository;
import keeper.project.homepage.member.service.MemberUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminClerkService {

  private final MemberUtilService memberUtilService;
  private final MemberRepository memberRepository;
  private final MemberTypeRepository memberTypeRepository;

  private static final List<String> INACCESSIBLE_JOB = List.of("ROLE_회원", "ROLE_출제자");

  public List<TypeResponseDto> getTypeList() {
    return memberTypeRepository.findAll()
        .stream()
        .map(TypeResponseDto::toDto)
        .toList();
  }

  public List<ClerkMemberJobTypeResponseDto> getClerkMemberListByType(Long typeId) {
    List<MemberEntity> findMembers = memberRepository.findAllByMemberTypeOrderByGenerationAsc(
        memberUtilService.getTypeById(typeId));
    return findMembers.stream()
        .map(ClerkMemberJobTypeResponseDto::toDto)
        .toList();
  }

  @Transactional
  public ClerkMemberJobTypeResponseDto updateMemberType(Long memberId, Long typeId) {
    MemberEntity member = memberUtilService.getById(memberId);
    MemberTypeEntity type = memberUtilService.getTypeById(typeId);

    member.changeMemberType(type);

    return ClerkMemberJobTypeResponseDto.toDto(member);
  }

  @Transactional
  public List<ClerkMemberJobTypeResponseDto> updateMemberTypeAll(
      List<ClerkMemberTypeRequestDto> requestDto) {
    List<ClerkMemberJobTypeResponseDto> result = new ArrayList<>();
    for (ClerkMemberTypeRequestDto clerkMemberTypeRequestDto : requestDto) {
      result.add(updateMemberType(
          clerkMemberTypeRequestDto.getMemberId(), clerkMemberTypeRequestDto.getTypeId())
      );
    }
    return result;
  }
}
