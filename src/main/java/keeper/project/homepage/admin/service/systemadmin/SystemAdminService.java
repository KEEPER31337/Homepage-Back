package keeper.project.homepage.admin.service.systemadmin;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.admin.dto.systemadmin.response.JobResponseDto;
import keeper.project.homepage.admin.dto.systemadmin.response.MemberJobTypeResponseDto;
import keeper.project.homepage.admin.dto.systemadmin.response.TypeResponseDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.exception.clerk.CustomClerkInaccessibleJobException;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
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
public class SystemAdminService {

  private final MemberUtilService memberUtilService;
  private final MemberJobRepository memberJobRepository;
  private final MemberTypeRepository memberTypeRepository;
  private final MemberHasMemberJobRepository memberHasMemberJobRepository;

  private static final List<String> INACCESSIBLE_JOB = List.of("ROLE_회원", "ROLE_출제자");

  public List<JobResponseDto> getJobList() {
    return memberJobRepository.findAll()
        .stream()
        .map(JobResponseDto::toDto)
        .filter(jobDto -> !INACCESSIBLE_JOB.contains(jobDto.getName()))
        .toList();
  }

  public List<TypeResponseDto> getTypeList() {
    return memberTypeRepository.findAll()
        .stream()
        .map(TypeResponseDto::toDto)
        .toList();
  }

  @Transactional
  public MemberJobTypeResponseDto assignJob(Long memberId, Long jobId) {
    MemberEntity member = memberUtilService.getById(memberId);
    MemberJobEntity job = memberUtilService.getJobById(jobId);
    checkValidJob(job);
    assignJobToMember(member, job);
    return MemberJobTypeResponseDto.toDto(member);
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
  public MemberJobTypeResponseDto deleteJob(Long memberId, Long jobId) {
    MemberEntity member = memberUtilService.getById(memberId);
    MemberJobEntity job = memberUtilService.getJobById(jobId);
    checkValidJob(job);
    deleteJobToMember(member, job);
    return MemberJobTypeResponseDto.toDto(member);
  }

  private void deleteJobToMember(MemberEntity member, MemberJobEntity job) {
    memberHasMemberJobRepository.deleteAllByMemberEntityAndMemberJobEntity(member, job);
    member.getMemberJobs().removeIf(entity -> entity.getMemberJobEntity().equals(job));
    job.getMembers().removeIf(entity -> entity.getMemberEntity().equals(member));
  }

  public List<MemberJobTypeResponseDto> getMemberListByJob(Long jobId) {
    MemberJobEntity job = memberUtilService.getJobById(jobId);
    List<MemberHasMemberJobEntity> everyoneHasThatRole = memberHasMemberJobRepository
        .findAllByMemberJobEntity(job);
    return everyoneHasThatRole.stream()
        .map(MemberHasMemberJobEntity::getMemberEntity)
        .map(MemberJobTypeResponseDto::toDto)
        .toList();
  }

  public List<MemberJobTypeResponseDto> getMemberListHasJob() {
    List<MemberJobEntity> accessibleJobList = getAccessibleJobList();
    List<MemberJobTypeResponseDto> memberListHasJob = new ArrayList<>();
    for (MemberJobEntity job : accessibleJobList) {
      List<MemberJobTypeResponseDto> memberListHasEachJob = getMemberListHasEachJob(job);
      memberListHasJob.addAll(memberListHasEachJob);
    }
    return memberListHasJob;
  }

  private List<MemberJobEntity> getAccessibleJobList() {
    return memberJobRepository.findAll()
        .stream()
        .filter(job -> !INACCESSIBLE_JOB.contains(job.getName()))
        .toList();
  }

  private List<MemberJobTypeResponseDto> getMemberListHasEachJob(MemberJobEntity job) {
    return memberHasMemberJobRepository.findAllByMemberJobEntity(job)
        .stream()
        .map(MemberHasMemberJobEntity::getMemberEntity)
        .map(MemberJobTypeResponseDto::toDto)
        .toList();
  }
}
