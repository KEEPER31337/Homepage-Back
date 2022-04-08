package keeper.project.homepage.user.dto.study;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyYearSeasonDto {

  private Integer year;
  private List<Integer> season;
}
