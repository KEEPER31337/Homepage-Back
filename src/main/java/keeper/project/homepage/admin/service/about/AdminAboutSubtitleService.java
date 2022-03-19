package keeper.project.homepage.admin.service.about;

import keeper.project.homepage.admin.dto.etc.StaticWriteSubtitleImageDto;
import keeper.project.homepage.admin.dto.etc.StaticWriteSubtitleImageResult;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.repository.etc.StaticWriteSubtitleImageRepository;
import keeper.project.homepage.repository.etc.StaticWriteTitleRepository;
import keeper.project.homepage.util.ImageCenterCrop;
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

  private void deleteThumbnail(ThumbnailEntity thumbnailEntity) {
    if (thumbnailEntity != null) {
      thumbnailService.deleteById(thumbnailEntity.getId());
      fileService.deleteOriginalThumbnail(thumbnailEntity);
    }
  }

  public StaticWriteSubtitleImageResult createSubtitle(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto, MultipartFile thumbnail,
      String ipAddress) {

    StaticWriteTitleEntity staticWriteTitle = checkValidTitleId(
        staticWriteSubtitleImageDto.getStaticWriteTitleId());

    ThumbnailEntity thumbnailEntity;

    if(thumbnail == null) {
      thumbnailEntity = thumbnailService.findById(1L);
      System.out.println("디폴트 이미지로 설정되었습니다.");
    } else {
      thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(), thumbnail,
          ThumbnailSize.LARGE, ipAddress);
      System.out.println("새로운 이미지가 생성되었습니다.");
    }

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageDto.toEntity(staticWriteTitle, thumbnailEntity));

    return new StaticWriteSubtitleImageResult(staticWriteSubtitleImageEntity);
  }

  public StaticWriteSubtitleImageResult deleteSubtitleById(Long id) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = checkValidSubTitleId(id);

    if(!(staticWriteSubtitleImageEntity.getThumbnail().getId() == 1L)) {
      deleteThumbnail(staticWriteSubtitleImageEntity.getThumbnail());
      System.out.println("이전 이미지가 삭제되었습니다.");
    }

    staticWriteSubtitleImageRepository.delete(staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResult(staticWriteSubtitleImageEntity);
  }

  public StaticWriteSubtitleImageResult modifySubtitleById(
      StaticWriteSubtitleImageDto staticWriteSubtitleImageDto, Long id, MultipartFile thumbnail,
      String ipAddress) {

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = checkValidSubTitleId(id);

    StaticWriteTitleEntity staticWriteTitle = checkValidTitleId(
        staticWriteSubtitleImageDto.getStaticWriteTitleId());

    ThumbnailEntity prevThumbnail = staticWriteSubtitleImageEntity.getThumbnail();
    ThumbnailEntity newThumbnail;
    if(thumbnail == null) {
      newThumbnail = thumbnailService.findById(1L);
      System.out.println("디폴트 이미지로 설정되었습니다.");
    } else {
      newThumbnail = thumbnailService.saveThumbnail(new ImageCenterCrop(), thumbnail,
          ThumbnailSize.LARGE, ipAddress);
      System.out.println("새로운 이미지가 생성되었습니다.");
    }
    if(newThumbnail == null) throw new CustomThumbnailEntityNotFoundException("썸네일 저장 중에 에러가 발생했습니다.");

    staticWriteSubtitleImageEntity.updateInfo(staticWriteSubtitleImageDto, staticWriteTitle,
        newThumbnail);

    if(!(prevThumbnail.getId().equals(1L))) {
      deleteThumbnail(prevThumbnail);
      System.out.println("이전 이미지가 삭제되었습니다.");
    }

    StaticWriteSubtitleImageEntity modifyEntity = staticWriteSubtitleImageRepository.save(
        staticWriteSubtitleImageEntity);
    return new StaticWriteSubtitleImageResult(modifyEntity);
  }

}
