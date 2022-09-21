package keeper.project.homepage.study.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyYearSeasonDto {

  private Integer year;
  private List<Integer> season;
}
