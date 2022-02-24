package keeper.project.homepage.service.etc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import keeper.project.homepage.dto.etc.StaticWriteTitleDto;
import keeper.project.homepage.dto.result.StaticWriteTitleResult;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.repository.etc.StaticWriteTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AboutTitleService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;

  public List<StaticWriteTitleResult> findAllByType(String type) {
    List<StaticWriteTitleEntity> titleEntity = staticWriteTitleRepository.findAllByType(type);
    if (titleEntity.isEmpty()) {
      throw new CustomAboutFailedException("존재하지 않는 타입입니다.");
    }
    return titleEntity.stream().map(StaticWriteTitleResult::new).collect(Collectors.toList());
  }

  public StaticWriteTitleResult findByTitle(String title) {
    Optional<StaticWriteTitleEntity> staticWriteTitleEntity = staticWriteTitleRepository.findByTitle(
        title);
    if (staticWriteTitleEntity.isEmpty()) {
      throw new CustomAboutFailedException("존재하지 않는 제목입니다.");
    }
    return new StaticWriteTitleResult(staticWriteTitleEntity.get());
  }

  public StaticWriteTitleResult createTitle(StaticWriteTitleDto titleDto) {

    StaticWriteTitleEntity staticWriteTitleEntity = staticWriteTitleRepository.save(
        titleDto.toEntity());
    return new StaticWriteTitleResult(staticWriteTitleEntity);
  }

  public StaticWriteTitleResult modifyTitleById(StaticWriteTitleDto titleDto, Long id) {

    StaticWriteTitleEntity staticWriteTitleEntity = staticWriteTitleRepository.findById(id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 ID입니다."));
    String title = titleDto.getTitle();
    staticWriteTitleEntity.updateInfo(title);
    staticWriteTitleRepository.save(staticWriteTitleEntity);
    return new StaticWriteTitleResult(staticWriteTitleEntity);
  }

  public StaticWriteTitleResult deleteTitleById(Long id) {
    StaticWriteTitleEntity staticWriteTitleEntity = staticWriteTitleRepository.findById(id)
        .orElseThrow(() -> new CustomAboutFailedException("존재하지 않는 ID입니다."));
    staticWriteTitleRepository.delete(staticWriteTitleEntity);
    return new StaticWriteTitleResult(staticWriteTitleEntity);
  }

}
