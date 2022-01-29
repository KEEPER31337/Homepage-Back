package keeper.project.homepage.service.etc;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.etc.StaticWriteTitleDto;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.repository.etc.StaticWriteSubtitleImageRepository;
import keeper.project.homepage.repository.etc.StaticWriteTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AboutService {

  private final StaticWriteTitleRepository titleRepository;

  public StaticWriteTitleEntity findAllByTitleType(String titleType) {
    Optional<StaticWriteTitleEntity> titleEntity = titleRepository.findByType(titleType);
    if (titleEntity.isEmpty()) {
      throw new CustomAboutFailedException("title type이 존재하지 않습니다.");
    }
    return titleEntity.get();
  }

  public void saveTitle(StaticWriteTitleDto titleDto) {

    titleRepository.save(titleDto.toEntity());
  }

  public void modifyTitle(StaticWriteTitleDto titleDto, String titleType) {

    StaticWriteTitleEntity titleEntity = titleRepository.findByType(titleType).
        orElseThrow(() -> new CustomAboutFailedException("title을 찾을 수 없습니다."));
    String title = titleDto.getTitle();
    titleEntity.updateInfo(title);
    titleRepository.save(titleEntity);
  }

}
