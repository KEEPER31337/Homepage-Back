package keeper.project.homepage.mapper;

import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.service.ThumbnailService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PostingMapper {

  @Autowired
  protected MemberRepository memberRepository;
  @Autowired
  protected CategoryRepository categoryRepository;
  @Autowired
  protected ThumbnailService thumbnailService;

  @Mapping(target = "categoryId", expression = "java(postingEntity.getCategoryId().getId())")
  public abstract PostingDto toDto(PostingEntity postingEntity);

  @Mapping(target = "writerThumbnailId", ignore = true)
  @Mapping(target = "writerId", ignore = true)
  @Mapping(target = "writer", ignore = true)
  @Mapping(target = "thumbnail", expression = "java(thumbnailService.findById(postingDto.getThumbnailId()))")
  @Mapping(target = "memberHasPostingLikeEntities", ignore = true)
  @Mapping(target = "memberHasPostingDislikeEntities", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "files", ignore = true)
  @Mapping(target = "categoryId", expression = "java(categoryRepository.findById(postingDto.getCategoryId()).get())")
  public abstract PostingEntity toEntity(PostingDto postingDto);
}
