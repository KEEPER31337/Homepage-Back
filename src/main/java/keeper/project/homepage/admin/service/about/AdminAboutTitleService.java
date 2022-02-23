package keeper.project.homepage.admin.service.about;

import keeper.project.homepage.dto.etc.StaticWriteTitleDto;
import keeper.project.homepage.dto.result.StaticWriteTitleResult;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.repository.etc.StaticWriteTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminAboutTitleService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;

  private StaticWriteTitleEntity checkValidTitleId(Long id) {

    return staticWriteTitleRepository.findById(id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 타이틀 ID 입니다."));
  }

  public StaticWriteTitleResult createTitle(StaticWriteTitleDto titleDto) {

    StaticWriteTitleEntity staticWriteTitleEntity = staticWriteTitleRepository.save(
        titleDto.toEntity());
    return new StaticWriteTitleResult(staticWriteTitleEntity);
  }

  public StaticWriteTitleResult modifyTitleById(StaticWriteTitleDto titleDto, Long id) {

    StaticWriteTitleEntity staticWriteTitleEntity = checkValidTitleId(id);
    String title = titleDto.getTitle();
    staticWriteTitleEntity.updateInfo(title);
    staticWriteTitleRepository.save(staticWriteTitleEntity);
    return new StaticWriteTitleResult(staticWriteTitleEntity);
  }

  public StaticWriteTitleResult deleteTitleById(Long id) {
    StaticWriteTitleEntity staticWriteTitleEntity = checkValidTitleId(id);
    staticWriteTitleRepository.delete(staticWriteTitleEntity);
    return new StaticWriteTitleResult(staticWriteTitleEntity);
  }

}
