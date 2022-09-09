package keeper.project.homepage.admin.service.clerk;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.admin.dto.clerk.request.ClerkMemberTypeRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.ClerkMemberJobTypeResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.TypeResponseDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import keeper.project.homepage.user.service.member.MemberUtilService;
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
