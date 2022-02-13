package keeper.project.homepage.controller.attendance;

import java.util.HashMap;
import keeper.project.homepage.dto.attendance.RouletteDto;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.attendance.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/game")
public class GameController {

  private final GameService gameService;
  private final ResponseService responseService;

  @Secured("ROLE_회원")
  @GetMapping(value = "/dice/info")
  public SingleResult<Integer> checkDiceInfo() {

    return responseService.getSuccessSingleResult(gameService.checkDiceTimes());
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/dice/play")
  public CommonResult playDice(@RequestParam("bet") Integer bettingPoint) {

    gameService.playDiceGame(bettingPoint);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/dice/save")
  public CommonResult saveResult(@RequestParam("result") Integer result,
      @RequestParam("bet") Integer bettingPoint) {

    gameService.saveDiceGame(result, bettingPoint);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/roulette/play")
  public SingleResult<RouletteDto> playRoulette() {

    return responseService.getSuccessSingleResult(gameService.playRouletteGame());
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/lotto/info")
  public SingleResult<Integer> checkLottoInfo() {

    return responseService.getSuccessSingleResult(gameService.checkLottoTimes());
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/lotto/play")
  public SingleResult<Integer> playLotto() {

    return responseService.getSuccessSingleResult(gameService.playLottoGame());
  }

  @GetMapping(value = "/info")
  public SingleResult<HashMap<String, Object>> getGameInfo() throws IllegalAccessException {

    return responseService.getSuccessSingleResult(gameService.getAllGameInfo());
  }
}
