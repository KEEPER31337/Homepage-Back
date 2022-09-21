package keeper.project.homepage.study.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

@Getter
@Setter
public class StudyRequestDto {

  @Nullable
  private MultipartFile thumbnail;
  private StudyDto studyDto;
}
