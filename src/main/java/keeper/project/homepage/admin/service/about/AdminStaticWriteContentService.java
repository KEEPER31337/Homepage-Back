package keeper.project.homepage.admin.service.about;

import keeper.project.homepage.admin.dto.about.request.StaticWriteContentDto;
import keeper.project.homepage.admin.dto.about.response.StaticWriteContentResponseDto;
import keeper.project.homepage.entity.about.StaticWriteContentEntity;
import keeper.project.homepage.entity.about.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.exception.about.CustomStaticWriteContentNotFoundException;
import keeper.project.homepage.exception.about.CustomStaticWriteSubtitleImageNotFoundException;
import keeper.project.homepage.repository.about.StaticWriteContentRepository;
import keeper.project.homepage.repository.about.StaticWriteSubtitleImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminStaticWriteContentService {

  private final StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;
  private final StaticWriteContentRepository staticWriteContentRepository;

  private StaticWriteSubtitleImageEntity validateSubtitleId(Long id) {

    return staticWriteSubtitleImageRepository.findById(id)
        .orElseThrow(CustomStaticWriteSubtitleImageNotFoundException::new);
  }

  private StaticWriteContentEntity validateContentId(Long id) {

    return staticWriteContentRepository.findById(id)
        .orElseThrow(CustomStaticWriteContentNotFoundException::new);
  }

  public StaticWriteContentResponseDto createContent(StaticWriteContentDto staticWriteContentDto) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = validateSubtitleId(
        staticWriteContentDto.getStaticWriteSubtitleImageId());

    StaticWriteContentEntity savedStaticWriteContent = staticWriteContentRepository.save(
        staticWriteContentDto.toEntity(staticWriteSubtitleImageEntity));
    return new StaticWriteContentResponseDto(savedStaticWriteContent);
  }

  public StaticWriteContentResponseDto modifyContentById(StaticWriteContentDto staticWriteContentDto,
      Long id) {
    StaticWriteContentEntity staticWriteContentEntity = validateContentId(id);

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = validateSubtitleId(
        staticWriteContentDto.getStaticWriteSubtitleImageId());

    staticWriteContentEntity.update(staticWriteContentDto, staticWriteSubtitleImageEntity);
    StaticWriteContentEntity modifiedStaticWriteContent = staticWriteContentRepository.save(
        staticWriteContentEntity);

    return new StaticWriteContentResponseDto(modifiedStaticWriteContent);
  }

}
