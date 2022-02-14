package keeper.project.homepage.service.etc;

import keeper.project.homepage.dto.etc.StaticWriteContentDto;
import keeper.project.homepage.dto.result.StaticWriteContentResult;
import keeper.project.homepage.entity.etc.StaticWriteContentEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.repository.etc.StaticWriteContentRepository;
import keeper.project.homepage.repository.etc.StaticWriteSubtitleImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AboutContentService {

  private final StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;
  private final StaticWriteContentRepository staticWriteContentRepository;

  public StaticWriteContentResult createContent(StaticWriteContentDto staticWriteContentDto) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = staticWriteSubtitleImageRepository.findById(
            staticWriteContentDto.getStaticWriteSubtitleImageId())
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 서브 타이틀 ID입니다."));

    StaticWriteContentEntity staticWriteContentEntity = staticWriteContentRepository.save(
        staticWriteContentDto.toEntity(staticWriteSubtitleImageEntity));
    return new StaticWriteContentResult(staticWriteContentEntity);
  }

  public StaticWriteContentResult deleteContentById(Long id) {
    StaticWriteContentEntity staticWriteContentEntity = staticWriteContentRepository.findById(id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 컨텐츠 ID입니다."));
    staticWriteContentRepository.delete(staticWriteContentEntity);
    return new StaticWriteContentResult(staticWriteContentEntity);
  }

  public StaticWriteContentResult modifyContentById(StaticWriteContentDto staticWriteContentDto,
      Long id) {
    StaticWriteContentEntity staticWriteContentEntity = staticWriteContentRepository.findById(id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 컨텐츠 ID입니다."));

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = staticWriteSubtitleImageRepository.findById(
        staticWriteContentDto.getStaticWriteSubtitleImageId())
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 서브 타이틀 ID입니다."));

    staticWriteContentEntity.updateInfo(staticWriteContentDto,staticWriteSubtitleImageEntity);
    StaticWriteContentEntity modifyEntity = staticWriteContentRepository.save(staticWriteContentEntity);

    return new StaticWriteContentResult(modifyEntity);
  }
}
