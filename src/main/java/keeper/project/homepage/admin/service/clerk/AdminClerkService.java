package keeper.project.homepage.admin.service.clerk;

import java.util.List;
import keeper.project.homepage.admin.dto.clerk.request.AssignJobRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.DeleteJobRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.ClerkMemberJobTypeResponseDto;
import keeper.project.homepage.admin.dto.member.job.JobDto;
import keeper.project.homepage.admin.dto.member.type.TypeDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.exception.clerk.CustomClerkInaccessibleJobException;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
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
  private final MemberJobRepository memberJobRepository;
  private final MemberTypeRepository memberTypeRepository;
  private final MemberHasMemberJobRepository memberHasMemberJobRepository;

  private static final List<String> INACCESSIBLE_JOB = List.of("ROLE_회원", "ROLE_출제자");

  public List<JobDto> getJobList() {
    return memberJobRepository.findAll()
        .stream()
        .map(JobDto::toDto)
        .filter(jobDto -> !INACCESSIBLE_JOB.contains(jobDto.getName()))
        .toList();
  }

  public List<TypeDto> getTypeList() {
    return memberTypeRepository.findAll()
        .stream()
        .map(TypeDto::toDto)
        .toList();
  }

  @Transactional
  public ClerkMemberJobTypeResponseDto assignJob(Long memberId, AssignJobRequestDto requestDto) {
    MemberEntity member = memberUtilService.getById(memberId);
    MemberJobEntity job = memberUtilService.getJobById(requestDto.getJobId());
    checkValidJob(job);
    assignJobToMember(member, job);
    return ClerkMemberJobTypeResponseDto.toDto(member);
  }

  private static void checkValidJob(MemberJobEntity job) {
    if (INACCESSIBLE_JOB.contains(job.getName())) {
      throw new CustomClerkInaccessibleJobException();
    }
  }

  private void assignJobToMember(MemberEntity member, MemberJobEntity job) {
    MemberHasMemberJobEntity save = memberHasMemberJobRepository.save(
        MemberHasMemberJobEntity.builder()
            .memberEntity(member)
            .memberJobEntity(job)
            .build());
    member.getMemberJobs().add(save);
    job.getMembers().add(save);
  }

  @Transactional
  public ClerkMemberJobTypeResponseDto deleteJob(Long memberId, DeleteJobRequestDto requestDto) {
    MemberEntity member = memberUtilService.getById(memberId);
    MemberJobEntity job = memberUtilService.getJobById(requestDto.getJobId());
    checkValidJob(job);
    deleteJobToMember(member, job);
    return ClerkMemberJobTypeResponseDto.toDto(member);
  }

  private void deleteJobToMember(MemberEntity member, MemberJobEntity job) {
    memberHasMemberJobRepository.deleteAllByMemberEntityAndMemberJobEntity(member, job);
    member.getMemberJobs().removeIf(entity -> entity.getMemberJobEntity().equals(job));
    job.getMembers().removeIf(entity -> entity.getMemberEntity().equals(member));
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
}
