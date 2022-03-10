package keeper.project.homepage.common.dto.attendance;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameInfoDto {

  public static final Integer DICE_BET_MAX = 1000;
  public static final Integer ROULETTE_FEE = 100;
  public static final List<Integer> ROULETTE_LIST = List.of(0, 50, 50, 100, 100, 500, 100, 500, 500,
      1000, 500, 1000, 500,
      1000, 1000, 5000);
  public static final Integer LOTTO_FEE = 1000;
  public static final Double FIRST_PROB = 0.000001;
  public static final Double SECOND_PROB = 0.0001;
  public static final Double THIRD_PROB = 0.01;
  public static final Double FOURTH_PROB = 0.3;
  public static final Double FIFTH_PROB = 0.5;
  public static final Integer FIRST_POINT = 1000000;
  public static final Integer SECOND_POINT = 100000;
  public static final Integer THIRD_POINT = 10000;
  public static final Integer FOURTH_POINT = 3000;
  public static final Integer FIFTH_POINT = 2000;
  public static final Integer LAST_POINT = 100;
  public static final Integer DICE_MAX_PLAYTIME = 100;
  public static final Integer ROULETTE_MAX_PLAYTIME = 100;
  public static final Integer LOTTO_MAX_PLAYTIME = 100;

}
