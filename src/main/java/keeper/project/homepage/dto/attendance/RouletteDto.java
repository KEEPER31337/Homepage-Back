package keeper.project.homepage.dto.attendance;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouletteDto {

  private Integer roulettePerDay;
  private List<Integer> roulettePoints;
  private Integer roulettePointIdx;
}
