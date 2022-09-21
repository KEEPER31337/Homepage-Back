package keeper.project.homepage.study.mapper;

import keeper.project.homepage.study.entity.StudyEntity;
import keeper.project.homepage.study.dto.StudyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

// spring component 설정, 변수명이 다르거나 서로 없는 Map되지 않는 value들 IGNORE처리
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class StudyMapper {

  @Mapping(target = "thumbnailPath", expression = "java(studyEntity.getThumbnailPath())")
  @Mapping(target = "headMember", expression = "java(studyEntity.headMemberToDto())")
  @Mapping(target = "memberList", expression = "java(studyEntity.getStudyMembers())")
  public abstract StudyDto toDto(StudyEntity studyEntity);

  public abstract StudyEntity toEntity(StudyDto studyDto);
}
