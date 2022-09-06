package keeper.project.homepage.admin.service.member;

import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.admin.dto.member.MemberDemeritDto;
import keeper.project.homepage.admin.dto.member.MemberDto;
import keeper.project.homepage.admin.dto.member.MemberGenerationDto;
import keeper.project.homepage.admin.dto.member.MemberJobDto;
import keeper.project.homepage.admin.dto.member.MemberMeritDto;
import keeper.project.homepage.admin.dto.member.MemberRankDto;
import keeper.project.homepage.admin.dto.member.MemberTypeDto;
import keeper.project.homepage.admin.dto.member.response.MemberByRealNameResponseDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.exception.member.CustomMemberEmptyFieldException;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

  private final AdminMemberUtilService adminMemberUtilService;
  private final MemberRepository memberRepository;
  private final MemberHasMemberJobRepository memberHasMemberJobRepository;

  private MemberEntity deleteMemberJob(MemberHasMemberJobEntity mj, MemberEntity member) {
    memberHasMemberJobRepository.delete(mj);
    mj.getMemberJobEntity().getMembers().remove(mj);
    member.getMemberJobs().remove(mj);
    return member;
  }

  private MemberEntity addMemberJob(String jobName, MemberEntity member) {
    MemberJobEntity newJob = adminMemberUtilService.getByJobName(jobName);

    MemberHasMemberJobEntity newMJ = memberHasMemberJobRepository.save(
        MemberHasMemberJobEntity.builder()
            .memberEntity(member)
            .memberJobEntity(newJob)
            .build());
    newJob.getMembers().add(newMJ);
    member.getMemberJobs().add(newMJ);
    return member;
  }

  public List<MemberDto> getMembers() {
    List<MemberEntity> memberEntityList = memberRepository.findAll();

    return memberEntityList.stream()
        .filter(memberEntity -> !(memberEntity.getId().equals(1L)))
        .map(MemberDto::new)
        .collect(Collectors.toList());
  }

  public MemberDto updateMemberRank(MemberRankDto rankDto) {
    if (rankDto.getName().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 등급의 이름이 비어있습니다.");
    }

    String loginId = rankDto.getMemberLoginId();
    MemberEntity updateEntity = adminMemberUtilService.getByLoginId(loginId);
    MemberRankEntity prevRank = updateEntity.getMemberRank();
    if (prevRank != null) {
      prevRank.getMembers().remove(updateEntity);
    }

    MemberRankEntity updateRank = adminMemberUtilService.getByRankName(rankDto.getName());
    updateRank.getMembers().add(updateEntity);
    updateEntity.changeMemberRank(updateRank);
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateEntity));
    return result;
  }

  public MemberDto updateMemberType(MemberTypeDto typeDto) {
    if (typeDto.getName().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 타입의 이름이 비어있습니다.");
    }

    String loginId = typeDto.getMemberLoginId();
    MemberEntity updateEntity = adminMemberUtilService.getByLoginId(loginId);
    MemberTypeEntity prevType = updateEntity.getMemberType();
    if (prevType != null) {
      prevType.getMembers().remove(updateEntity);
    }

    MemberTypeEntity updateType = adminMemberUtilService.getByTypeName(typeDto.getName());
    updateType.getMembers().add(updateEntity);
    updateEntity.changeMemberType(updateType);
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateEntity));
    return result;
  }

  public MemberDto updateMemberJobs(MemberJobDto jobDto) {
    if (jobDto.getNames().isEmpty()) {
      throw new CustomMemberEmptyFieldException("변경할 직책의 이름이 비어있습니다.");
    }

    String loginId = jobDto.getMemberLoginId();
    MemberEntity updateMember = adminMemberUtilService.getByLoginId(loginId);
    List<MemberHasMemberJobEntity> prevMJList = memberHasMemberJobRepository.findAllByMemberEntity_Id(
        updateMember.getId());
    if (!prevMJList.isEmpty()) {
      for (MemberHasMemberJobEntity prevMJ : prevMJList) {
        updateMember = deleteMemberJob(prevMJ, updateMember);
      }
    }

    for (String name : jobDto.getNames()) {
      updateMember = addMemberJob(name, updateMember);
    }
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateMember));
    return result;
  }

  public MemberDto updateGeneration(MemberGenerationDto dto) {
    if (dto.getGeneration() == null) {
      throw new CustomMemberEmptyFieldException("변경할 기수가 비어있습니다.");
    }

    String loginId = dto.getMemberLoginId();
    Float generation = dto.getGeneration();

    MemberEntity member = adminMemberUtilService.getByLoginId(loginId);
    member.changeGeneration(generation);
    memberRepository.save(member);

    MemberDto result = MemberDto.builder().build();
    result.initWithEntity(member);
    return result;
  }

  public MemberDto updateMerit(MemberMeritDto dto) {
    if (dto.getMerit() == null) {
      throw new CustomMemberEmptyFieldException("변경할 상점 값이 비어있습니다.");
    }

    String loginId = dto.getMemberLoginId();
    Integer merit = dto.getMerit();

    MemberEntity member = adminMemberUtilService.getByLoginId(loginId);
    member.changeMerit(merit);
    memberRepository.save(member);

    MemberDto result = MemberDto.builder().build();
    result.initWithEntity(member);
    return result;
  }

  public MemberDto updateDemerit(MemberDemeritDto dto) {
    if (dto.getDemerit() == null) {
      throw new CustomMemberEmptyFieldException("변경할 벌점 값이 비어있습니다.");
    }
    String loginId = dto.getMemberLoginId();
    Integer demerit = dto.getDemerit();

    MemberEntity member = adminMemberUtilService.getByLoginId(loginId);
    member.changeDemerit(demerit);
    memberRepository.save(member);

    MemberDto result = MemberDto.builder().build();
    result.initWithEntity(member);
    return result;
  }

  public List<MemberByRealNameResponseDto> getMembersByRealName(String keyword) {
    List<MemberEntity> members = memberRepository.findByRealNameContaining(keyword);
    return members.stream()
        .map(MemberByRealNameResponseDto::from)
        .toList();
  }

}