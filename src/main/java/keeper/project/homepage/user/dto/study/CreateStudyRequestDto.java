package keeper.project.homepage.user.dto.study;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

@Getter
@Setter
public class CreateStudyRequestDto extends StudyRequestDto {

  private List<Long> memberIdList = new ArrayList<>();
}
