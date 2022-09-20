package keeper.project.homepage.member.mapper;

import keeper.project.homepage.member.dto.CommonMemberDto;
import keeper.project.homepage.member.entity.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

// spring component 설정, 변수명이 다르거나 서로 없는 Map되지 않는 value들 IGNORE처리
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CommonMemberMapper {

  @Mapping(target = "jobs", expression = "java(memberEntity.getJobs())")
  @Mapping(target = "thumbnailPath", expression = "java(memberEntity.getThumbnailPath())")
  public abstract CommonMemberDto toDto(MemberEntity memberEntity);

  public abstract MemberEntity toEntity(CommonMemberDto memberDto);
}
