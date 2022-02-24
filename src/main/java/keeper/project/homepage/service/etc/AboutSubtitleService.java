package keeper.project.homepage.service.etc;

import keeper.project.homepage.dto.etc.StaticWriteSubtitleImageDto;
import keeper.project.homepage.dto.result.StaticWriteSubtitleImageResult;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.repository.etc.StaticWriteSubtitleImageRepository;
import keeper.project.homepage.repository.etc.StaticWriteTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AboutSubtitleService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;
  private final StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;
  private final ThumbnailRepository thumbnailRepository;

  public StaticWriteSubtitleImageResult createSubtitle(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto) {

    StaticWriteTitleEntity staticWriteTitle = staticWriteTitleRepository.findById(
            staticWriteSubtitleImageDto.getStaticWriteTitleId())
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 타이틀 ID입니다."));

    ThumbnailEntity thumbnailEntity = thumbnailRepository.findById(
            staticWriteSubtitleImageDto.getThumbnailId())
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 썸네일 ID입니다."));

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageDto.toEntity(staticWriteTitle, thumbnailEntity));

    return new StaticWriteSubtitleImageResult(staticWriteSubtitleImageEntity);
  }

  public StaticWriteSubtitleImageResult deleteSubtitleById(Long id) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = staticWriteSubtitleImageRepository.findById(
            id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 서브 타이틀 ID입니다."));
    staticWriteSubtitleImageRepository.delete(staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResult(staticWriteSubtitleImageEntity);
  }

  public StaticWriteSubtitleImageResult modifySubtitleById(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto, Long id) {

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = staticWriteSubtitleImageRepository.findById(
            id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 ID입니다."));

    StaticWriteTitleEntity staticWriteTitle = staticWriteTitleRepository.findById(
            staticWriteSubtitleImageDto.getStaticWriteTitleId())
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 타이틀 ID입니다."));

    ThumbnailEntity thumbnailEntity = thumbnailRepository.findById(
            staticWriteSubtitleImageDto.getThumbnailId())
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 썸네일 ID입니다."));
    staticWriteSubtitleImageEntity.updateInfo(staticWriteSubtitleImageDto, staticWriteTitle, thumbnailEntity);

    StaticWriteSubtitleImageEntity modifyEntity = staticWriteSubtitleImageRepository.save(staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResult(modifyEntity);
  }
}
