package keeper.project.homepage.about.service;

import keeper.project.homepage.about.dto.request.StaticWriteSubtitleImageDto;
import keeper.project.homepage.about.dto.response.StaticWriteSubtitleImageResponseDto;
import keeper.project.homepage.about.entity.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.about.entity.StaticWriteTitleEntity;
import keeper.project.homepage.about.exception.CustomStaticWriteSubtitleImageNotFoundException;
import keeper.project.homepage.about.exception.CustomStaticWriteTitleNotFoundException;
import keeper.project.homepage.about.repository.StaticWriteSubtitleImageRepository;
import keeper.project.homepage.about.repository.StaticWriteTitleRepository;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.util.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.util.image.preprocessing.ImageCenterCropping;
import keeper.project.homepage.util.image.preprocessing.ImageSize;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AdminStaticWriteSubtitleImageService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;
  private final StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;
  private final ThumbnailService thumbnailService;

  private StaticWriteTitleEntity getTitleById(Long id) {

    return staticWriteTitleRepository.findById(id)
        .orElseThrow(CustomStaticWriteTitleNotFoundException::new);
  }

  private StaticWriteSubtitleImageEntity getSubTitleById(Long id) {

    return staticWriteSubtitleImageRepository.findById(id)
        .orElseThrow(CustomStaticWriteSubtitleImageNotFoundException::new);
  }

  private ThumbnailEntity validateThumbnail(MultipartFile thumbnail, String ipAddress) {
    return thumbnailService.save(ThumbType.InfoThumbnail, new ImageCenterCropping(ImageSize.LARGE),
        thumbnail, ipAddress);
  }

  public StaticWriteSubtitleImageResponseDto createSubtitle(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto, MultipartFile thumbnail,
      String ipAddress) {

    StaticWriteTitleEntity staticWriteTitleEntity = getTitleById(
        staticWriteSubtitleImageDto.getStaticWriteTitleId());

    ThumbnailEntity thumbnailEntity = validateThumbnail(thumbnail, ipAddress);

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageDto.toEntity(staticWriteTitleEntity, thumbnailEntity));

    return new StaticWriteSubtitleImageResponseDto(staticWriteSubtitleImageEntity);
  }

  public StaticWriteSubtitleImageResponseDto updateSubtitleById(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto, Long id, MultipartFile thumbnail,
      String ipAddress) {

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = getSubTitleById(id);

    StaticWriteTitleEntity staticWriteTitleEntity = getTitleById(
        staticWriteSubtitleImageDto.getStaticWriteTitleId());

    ThumbnailEntity prevThumbnail = staticWriteSubtitleImageEntity.getThumbnail();
    ThumbnailEntity newThumbnail = validateThumbnail(thumbnail, ipAddress);

    if (newThumbnail == null) {
      throw new CustomThumbnailEntityNotFoundException("썸네일 저장 중에 에러가 발생했습니다.");
    }

    staticWriteSubtitleImageEntity.update(staticWriteSubtitleImageDto, newThumbnail);

    if (prevThumbnail != null) {
      thumbnailService.delete(prevThumbnail.getId());
    }

    StaticWriteSubtitleImageEntity result = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResponseDto(result);
  }

  public StaticWriteSubtitleImageResponseDto deleteSubtitleById(Long id) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = getSubTitleById(id);

    thumbnailService.delete(staticWriteSubtitleImageEntity.getThumbnail().getId());

    staticWriteSubtitleImageRepository.delete(staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResponseDto(staticWriteSubtitleImageEntity);
  }

}
