package keeper.project.homepage.user.service.about;

import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.admin.dto.about.response.StaticWriteTitleResponseDto;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;
import keeper.project.homepage.exception.about.CustomStaticWriteTypeNotFoundException;
import keeper.project.homepage.repository.about.StaticWriteTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StaticWriteTitleService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;

  public List<StaticWriteTitleResponseDto> findAllByType(String type) {
    List<StaticWriteTitleEntity> titleEntity = staticWriteTitleRepository.findAllByType(type);
    if (titleEntity.isEmpty()) {
      throw new CustomStaticWriteTypeNotFoundException();
    }
    return titleEntity.stream().map(StaticWriteTitleResponseDto::new).collect(Collectors.toList());
  }

  public List<String> getAllDistinctTypes() {
    return staticWriteTitleRepository.getAllDistinctTypes();
  }

}
