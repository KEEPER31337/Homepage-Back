package keeper.project.homepage.common.mapper;

import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.ThumbnailRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

// spring component 설정, 변수명이 다르거나 서로 없는 Map되지 않는 value들 IGNORE처리
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CommonMemberMapper {

  @Mapping(target = "jobs", expression = "java(memberEntity.getJobs())")
  public abstract CommonMemberDto toDto(MemberEntity memberEntity);

  @Mapping(target = "thumbnail", expression = "java(thumbnailRepository.findById(memberDto.getThumbnailId()).get())")
  public abstract MemberEntity toEntity(CommonMemberDto memberDto,
      @Context ThumbnailRepository thumbnailRepository);
}
