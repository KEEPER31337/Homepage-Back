package keeper.project.homepage.about.service;

import keeper.project.homepage.about.dto.request.StaticWriteTitleDto;
import keeper.project.homepage.about.dto.response.StaticWriteTitleResponseDto;
import keeper.project.homepage.about.entity.StaticWriteTitleEntity;
import keeper.project.homepage.about.exception.CustomStaticWriteTitleNotFoundException;
import keeper.project.homepage.about.repository.StaticWriteTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminStaticWriteTitleService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;

  public StaticWriteTitleResponseDto updateTitleById(StaticWriteTitleDto titleDto, Long id) {
    StaticWriteTitleEntity staticWriteTitleEntity = staticWriteTitleRepository.findById(id)
        .orElseThrow(CustomStaticWriteTitleNotFoundException::new);

    staticWriteTitleEntity.updateTitle(titleDto.getTitle());
    StaticWriteTitleEntity updatedTitle = staticWriteTitleRepository.save(staticWriteTitleEntity);

    return new StaticWriteTitleResponseDto(updatedTitle);
  }

}
