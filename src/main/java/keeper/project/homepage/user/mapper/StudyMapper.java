package keeper.project.homepage.user.mapper;

import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.study.StudyEntity;
import keeper.project.homepage.user.dto.study.StudyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

// spring component 설정, 변수명이 다르거나 서로 없는 Map되지 않는 value들 IGNORE처리
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class StudyMapper {

  public abstract StudyDto toDto(StudyEntity studyEntity);

  public abstract StudyEntity toEntity(StudyDto studyDto);
}
