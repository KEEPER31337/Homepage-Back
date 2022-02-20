package keeper.project.homepage.mapper;

import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

// spring component 설정, 변수명이 다르거나 서로 없는 Map되지 않는 value들 IGNORE처리
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PostingMapper {

  // dto-categoryId - Long || entity-categoryId - CategoryEntity -> 다르기에 둘 사이 수동매핑
  @Mapping(target = "categoryId", expression = "java(postingEntity.getCategoryId().getId())")
  public abstract PostingDto toDto(PostingEntity postingEntity);

  // BeforeMapping, AfterMapping -> Builder pattern에서는 사용 불가능..
//  @AfterMapping
//  public void afterToEntity(@MappingTarget PostingEntity postingEntity, @Context CategoryRepository categoryRepository){
//    postingEntity.setRegisterTime(LocalDateTime.now());
//    postingEntity.setUpdateTime(LocalDateTime.now());
//  }

  // dto-categoryId - Long || entity-categoryId - CategoryEntity -> 다르기에 둘 사이 수동매핑
  // repository, service등 spring의 component 사용하고 싶으면 @Context Annotation 필수
  @Mapping(target = "categoryId", expression = "java(categoryRepository.findById(postingDto.getCategoryId()).get())")
  @Mapping(target = "thumbnail", expression = "java(thumbnailRepository.findById(postingDto.getThumbnailId()).get())")
  public abstract PostingEntity toEntity(PostingDto postingDto, @Context CategoryRepository categoryRepository, @Context
      ThumbnailRepository thumbnailRepository);

}
