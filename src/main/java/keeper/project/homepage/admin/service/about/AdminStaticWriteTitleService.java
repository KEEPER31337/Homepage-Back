package keeper.project.homepage.admin.service.about;

import keeper.project.homepage.admin.dto.about.request.StaticWriteTitleDto;
import keeper.project.homepage.admin.dto.about.response.StaticWriteTitleResponseDto;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;
import keeper.project.homepage.exception.about.CustomStaticWriteNotFoundException;
import keeper.project.homepage.repository.about.StaticWriteTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminStaticWriteTitleService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;

  public StaticWriteTitleResponseDto updateTitleById(StaticWriteTitleDto titleDto, Long id) {
    StaticWriteTitleEntity staticWriteTitleEntity = staticWriteTitleRepository.findById(id)
        .orElseThrow(() -> new CustomStaticWriteNotFoundException("존재하지 않는 타이틀입니다."));

    staticWriteTitleEntity.updateTitle(titleDto.getTitle());
    StaticWriteTitleEntity updatedTitle = staticWriteTitleRepository.save(staticWriteTitleEntity);

    return new StaticWriteTitleResponseDto(updatedTitle);
  }

}
