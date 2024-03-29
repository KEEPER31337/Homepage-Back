package keeper.project.homepage.study.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyStudyRequestDto extends StudyRequestDto {

  private Long studyId;
  private List<Long> memberIdList = new ArrayList<>();
}
