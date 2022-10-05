package keeper.project.homepage.systemadmin.service;

import java.util.List;
import keeper.project.homepage.systemadmin.exception.CustomClerkInaccessibleJobException;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.repository.MemberHasMemberJobRepository;
import keeper.project.homepage.member.repository.MemberJobRepository;
import keeper.project.homepage.member.repository.MemberTypeRepository;
import keeper.project.homepage.member.service.MemberUtilService;
import keeper.project.homepage.systemadmin.dto.response.JobResponseDto;
import keeper.project.homepage.systemadmin.dto.response.MemberJobTypeResponseDto;
import keeper.project.homepage.systemadmin.dto.response.TypeResponseDto;
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
    member.addMemberJob(job);
    return MemberJobTypeResponseDto.toDto(member);
  }

  private static void checkValidJob(MemberJobEntity job) {
    if (INACCESSIBLE_JOB.contains(job.getName())) {
      throw new CustomClerkInaccessibleJobException();
    }
  }

  @Transactional
  public MemberJobTypeResponseDto deleteJob(Long memberId, Long jobId) {
    MemberEntity member = memberUtilService.getById(memberId);
    MemberJobEntity job = memberUtilService.getJobById(jobId);
    checkValidJob(job);
    member.removeMemberJob(job);
    return MemberJobTypeResponseDto.toDto(member);
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
    List<MemberHasMemberJobEntity> memberHasMemberJobEntities = memberHasMemberJobRepository
        .findByMemberJobEntityIn(accessibleJobList);
    return memberHasMemberJobEntities.stream()
        .map(MemberHasMemberJobEntity::getMemberEntity)
        .map(MemberJobTypeResponseDto::toDto)
        .distinct()
        .toList();
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
