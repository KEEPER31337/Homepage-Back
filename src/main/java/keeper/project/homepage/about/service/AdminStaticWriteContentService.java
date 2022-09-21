package keeper.project.homepage.about.service;

import keeper.project.homepage.about.dto.request.StaticWriteContentDto;
import keeper.project.homepage.about.dto.response.StaticWriteContentResponseDto;
import keeper.project.homepage.about.entity.StaticWriteContentEntity;
import keeper.project.homepage.about.entity.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.about.exception.CustomStaticWriteContentNotFoundException;
import keeper.project.homepage.about.exception.CustomStaticWriteSubtitleImageNotFoundException;
import keeper.project.homepage.about.repository.StaticWriteContentRepository;
import keeper.project.homepage.about.repository.StaticWriteSubtitleImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminStaticWriteContentService {

  private final StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;
  private final StaticWriteContentRepository staticWriteContentRepository;

  private StaticWriteSubtitleImageEntity getSubtitleById(Long id) {

    return staticWriteSubtitleImageRepository.findById(id)
        .orElseThrow(CustomStaticWriteSubtitleImageNotFoundException::new);
  }

  private StaticWriteContentEntity getContentById(Long id) {

    return staticWriteContentRepository.findById(id)
        .orElseThrow(CustomStaticWriteContentNotFoundException::new);
  }

  public StaticWriteContentResponseDto createContent(StaticWriteContentDto staticWriteContentDto) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = getSubtitleById(
        staticWriteContentDto.getStaticWriteSubtitleImageId());

    StaticWriteContentEntity savedStaticWriteContent = staticWriteContentRepository.save(
        staticWriteContentDto.toEntity(staticWriteSubtitleImageEntity));
    return new StaticWriteContentResponseDto(savedStaticWriteContent);
  }

  public StaticWriteContentResponseDto modifyContentById(StaticWriteContentDto staticWriteContentDto,
      Long id) {
    StaticWriteContentEntity staticWriteContentEntity = getContentById(id);

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = getSubtitleById(
        staticWriteContentDto.getStaticWriteSubtitleImageId());

    staticWriteContentEntity.update(staticWriteContentDto, staticWriteSubtitleImageEntity);
    StaticWriteContentEntity modifiedStaticWriteContent = staticWriteContentRepository.save(
        staticWriteContentEntity);

    return new StaticWriteContentResponseDto(modifiedStaticWriteContent);
  }

}
