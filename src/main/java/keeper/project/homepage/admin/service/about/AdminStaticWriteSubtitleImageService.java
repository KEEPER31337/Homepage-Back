package keeper.project.homepage.admin.service.about;

import keeper.project.homepage.admin.dto.about.request.StaticWriteSubtitleImageDto;
import keeper.project.homepage.admin.dto.about.response.StaticWriteSubtitleImageResponseDto;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.about.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;
import keeper.project.homepage.exception.about.CustomStaticWriteNotFoundException;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.repository.about.StaticWriteSubtitleImageRepository;
import keeper.project.homepage.repository.about.StaticWriteTitleRepository;
import keeper.project.homepage.util.ImageCenterCrop;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbnailSize;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AdminStaticWriteSubtitleImageService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;
  private final StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;
  private final ThumbnailService thumbnailService;

  private StaticWriteTitleEntity validateTitleId(Long id) {

    return staticWriteTitleRepository.findById(id)
        .orElseThrow(() -> new CustomStaticWriteNotFoundException("존재하지 않는 타이틀입니다."));
  }

  private StaticWriteSubtitleImageEntity validateSubTitleId(Long id) {

    return staticWriteSubtitleImageRepository.findById(id)
        .orElseThrow(() -> new CustomStaticWriteNotFoundException("존재하지 않는 서브 타이틀입니다."));
  }

  private ThumbnailEntity validateThumbnail(MultipartFile thumbnail, String ipAddress) {
    if (thumbnail == null) {
      return thumbnailService.findById(9L);
    } else {
      return thumbnailService.saveThumbnail(new ImageCenterCrop(), thumbnail, ThumbnailSize.LARGE,
          ipAddress);
    }
  }

  public StaticWriteSubtitleImageResponseDto createSubtitle(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto, MultipartFile thumbnail,
      String ipAddress) {

    StaticWriteTitleEntity staticWriteTitleEntity = validateTitleId(
        staticWriteSubtitleImageDto.getStaticWriteTitleId());

    ThumbnailEntity thumbnailEntity = validateThumbnail(thumbnail, ipAddress);

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageDto.toEntity(staticWriteTitleEntity, thumbnailEntity));

    return new StaticWriteSubtitleImageResponseDto(staticWriteSubtitleImageEntity);
  }

  public StaticWriteSubtitleImageResponseDto updateSubtitleById(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto, Long id, MultipartFile thumbnail,
      String ipAddress) {

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = validateSubTitleId(id);

    StaticWriteTitleEntity staticWriteTitleEntity = validateTitleId(
        staticWriteSubtitleImageDto.getStaticWriteTitleId());

    ThumbnailEntity prevThumbnail = staticWriteSubtitleImageEntity.getThumbnail();
    ThumbnailEntity newThumbnail = validateThumbnail(thumbnail, ipAddress);

    if (newThumbnail == null) {
      throw new CustomThumbnailEntityNotFoundException("썸네일 저장 중에 에러가 발생했습니다.");
    }

    staticWriteSubtitleImageEntity.update(staticWriteSubtitleImageDto, newThumbnail);

    if (prevThumbnail != null) {
      thumbnailService.deleteById(prevThumbnail.getId());
    }

    StaticWriteSubtitleImageEntity result = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResponseDto(result);
  }

  public StaticWriteSubtitleImageResponseDto deleteSubtitleById(Long id) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = validateSubTitleId(id);

    thumbnailService.deleteById(staticWriteSubtitleImageEntity.getThumbnail().getId());

    staticWriteSubtitleImageRepository.delete(staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResponseDto(staticWriteSubtitleImageEntity);
  }

}
