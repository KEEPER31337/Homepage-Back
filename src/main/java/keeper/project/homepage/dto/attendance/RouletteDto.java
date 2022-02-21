package keeper.project.homepage.dto.attendance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RouletteDto {

  private Integer roulettePerDay;
  private List<Integer> roulettePoints;
  private Integer roulettePointIdx;
  private Integer todayResult;
}
