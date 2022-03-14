package keeper.project.homepage.user.dto.study;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyStudyRequestDto extends StudyRequestDto {

  private Long studyId;
}
