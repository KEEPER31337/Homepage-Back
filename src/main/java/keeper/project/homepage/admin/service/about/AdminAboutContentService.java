package keeper.project.homepage.admin.service.about;

import keeper.project.homepage.admin.dto.etc.StaticWriteContentDto;
import keeper.project.homepage.admin.dto.etc.StaticWriteContentResult;
import keeper.project.homepage.entity.etc.StaticWriteContentEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.repository.etc.StaticWriteContentRepository;
import keeper.project.homepage.repository.etc.StaticWriteSubtitleImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminAboutContentService {

  private final StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;
  private final StaticWriteContentRepository staticWriteContentRepository;

  private StaticWriteSubtitleImageEntity checkValidSubtitleId(Long id) {

    return staticWriteSubtitleImageRepository.findById(id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 서브 타이틀 ID 입니다."));
  }

  private StaticWriteContentEntity checkValidContentId(Long id) {

    return staticWriteContentRepository.findById(id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 컨텐츠 ID 입니다."));
  }

  public StaticWriteContentResult createContent(StaticWriteContentDto staticWriteContentDto) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = checkValidSubtitleId(
        staticWriteContentDto.getStaticWriteSubtitleImageId());

    StaticWriteContentEntity staticWriteContentEntity = staticWriteContentRepository.save(
        staticWriteContentDto.toEntity(staticWriteSubtitleImageEntity));
    return new StaticWriteContentResult(staticWriteContentEntity);
  }

  public StaticWriteContentResult deleteContentById(Long id) {
    StaticWriteContentEntity staticWriteContentEntity = checkValidContentId(id);
    staticWriteContentRepository.delete(staticWriteContentEntity);
    return new StaticWriteContentResult(staticWriteContentEntity);
  }

  public StaticWriteContentResult modifyContentById(StaticWriteContentDto staticWriteContentDto,
      Long id) {
    StaticWriteContentEntity staticWriteContentEntity = checkValidContentId(id);

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = checkValidSubtitleId(
        staticWriteContentDto.getStaticWriteSubtitleImageId());

    staticWriteContentEntity.updateInfo(staticWriteContentDto, staticWriteSubtitleImageEntity);
    StaticWriteContentEntity modifyEntity = staticWriteContentRepository.save(
        staticWriteContentEntity);

    return new StaticWriteContentResult(modifyEntity);
  }

}
