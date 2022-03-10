package keeper.project.homepage.admin.service.about;

import keeper.project.homepage.common.ImageCenterCrop;
import keeper.project.homepage.dto.etc.StaticWriteSubtitleImageDto;
import keeper.project.homepage.dto.result.StaticWriteSubtitleImageResult;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.repository.etc.StaticWriteSubtitleImageRepository;
import keeper.project.homepage.repository.etc.StaticWriteTitleRepository;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbnailSize;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AdminAboutSubtitleService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;
  private final StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;
  private final ThumbnailService thumbnailService;
  private final FileService fileService;

  private StaticWriteSubtitleImageEntity checkValidSubTitleId(Long id) {

    return staticWriteSubtitleImageRepository.findById(id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 서브 타이틀 ID 입니다."));
  }

  private StaticWriteTitleEntity checkValidTitleId(Long id) {

    return staticWriteTitleRepository.findById(id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 타이틀 ID 입니다."));
  }

  private void deleteRelatedThumbnail(ThumbnailEntity thumbnailEntity) {
    if(thumbnailEntity != null) {
      thumbnailService.deleteById(thumbnailEntity.getId());
      fileService.deleteOriginalThumbnail(thumbnailEntity);
    }
  }

  public StaticWriteSubtitleImageResult createSubtitle(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto, MultipartFile image,
      String ipAddress) {

    StaticWriteTitleEntity staticWriteTitle = checkValidTitleId(
        staticWriteSubtitleImageDto.getStaticWriteTitleId());

    ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(), image,
        ThumbnailSize.LARGE, ipAddress);

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageDto.toEntity(staticWriteTitle, thumbnailEntity));

    return new StaticWriteSubtitleImageResult(staticWriteSubtitleImageEntity);
  }

  public StaticWriteSubtitleImageResult deleteSubtitleById(Long id) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = checkValidSubTitleId(id);
    deleteRelatedThumbnail(staticWriteSubtitleImageEntity.getThumbnail());
    staticWriteSubtitleImageRepository.delete(staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResult(staticWriteSubtitleImageEntity);
  }

  public StaticWriteSubtitleImageResult modifySubtitleById(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto, Long id, MultipartFile image,
      String ipAddress) {

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = checkValidSubTitleId(id);

    StaticWriteTitleEntity staticWriteTitle = checkValidTitleId(
        staticWriteSubtitleImageDto.getStaticWriteTitleId());

    ThumbnailEntity prevThumbnail = staticWriteSubtitleImageEntity.getThumbnail();
    ThumbnailEntity newThumbnail = thumbnailService.saveThumbnail(new ImageCenterCrop(), image,
        ThumbnailSize.LARGE, ipAddress);

    staticWriteSubtitleImageEntity.updateInfo(staticWriteSubtitleImageDto, staticWriteTitle,
        newThumbnail);

    deleteRelatedThumbnail(prevThumbnail);

    StaticWriteSubtitleImageEntity modifyEntity = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResult(modifyEntity);
  }

}
