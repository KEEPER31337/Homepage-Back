package keeper.project.homepage.service.attendance;

import static keeper.project.homepage.dto.attendance.GameInfoDto.DICE_BET_MAX;
import static keeper.project.homepage.dto.attendance.GameInfoDto.DICE_MAX_PLAYTIME;
import static keeper.project.homepage.dto.attendance.GameInfoDto.FIFTH_POINT;
import static keeper.project.homepage.dto.attendance.GameInfoDto.FIFTH_PROB;
import static keeper.project.homepage.dto.attendance.GameInfoDto.FIRST_POINT;
import static keeper.project.homepage.dto.attendance.GameInfoDto.FIRST_PROB;
import static keeper.project.homepage.dto.attendance.GameInfoDto.FOURTH_POINT;
import static keeper.project.homepage.dto.attendance.GameInfoDto.FOURTH_PROB;
import static keeper.project.homepage.dto.attendance.GameInfoDto.LAST_POINT;
import static keeper.project.homepage.dto.attendance.GameInfoDto.LOTTO_FEE;
import static keeper.project.homepage.dto.attendance.GameInfoDto.LOTTO_MAX_PLAYTIME;
import static keeper.project.homepage.dto.attendance.GameInfoDto.ROULETTE_FEE;
import static keeper.project.homepage.dto.attendance.GameInfoDto.ROULETTE_LIST;
import static keeper.project.homepage.dto.attendance.GameInfoDto.ROULETTE_MAX_PLAYTIME;
import static keeper.project.homepage.dto.attendance.GameInfoDto.SECOND_POINT;
import static keeper.project.homepage.dto.attendance.GameInfoDto.SECOND_PROB;
import static keeper.project.homepage.dto.attendance.GameInfoDto.THIRD_POINT;
import static keeper.project.homepage.dto.attendance.GameInfoDto.THIRD_PROB;
import static keeper.project.homepage.service.attendance.DateUtils.isToday;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.attendance.GameInfoDto;
import keeper.project.homepage.dto.attendance.LottoDto;
import keeper.project.homepage.dto.attendance.RouletteDto;
import keeper.project.homepage.dto.point.request.PointLogRequest;
import keeper.project.homepage.entity.attendance.GameEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.attendance.GameRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.service.util.AuthService;
import keeper.project.homepage.user.service.point.PointLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GameService {

  private final AuthService authService;
  private final MemberRepository memberRepository;
  private final GameRepository gameRepository;
  private final PointLogService pointLogService;

  public Boolean isOverDiceTimes() {

    GameEntity gameEntity = getOrResetGameEntity();
    return gameEntity.getDicePerDay() > DICE_MAX_PLAYTIME;
  }

  @Transactional
  public boolean playDiceGame(Integer bettingPoint) {

    if (bettingPoint > DICE_BET_MAX) {
      return false;
    }

    MemberEntity memberEntity = getMemberEntityWithJWT();
    GameEntity gameEntity = getOrResetGameEntity();
    gameEntity.increaseDiceTimes();
    gameEntity.setLastPlayTime(LocalDateTime.now());
    gameRepository.save(gameEntity);
    pointLogService.createPointUseLog(memberEntity,
        new PointLogRequest(LocalDateTime.now(), bettingPoint, "주사위 게임 포인트 차감"));
    return true;
  }

  @Transactional
  public Integer saveDiceGame(Integer result, Integer bettingPoint) {

    if (bettingPoint > DICE_BET_MAX) {
      return -9999;
    }

    MemberEntity memberEntity = getMemberEntityWithJWT();
    int updatePoint = 0;
    int diceDayPoint = 0;
    if (result == 1) {
      updatePoint = 2 * bettingPoint;
      diceDayPoint = bettingPoint;
    } else if (result == 0) {
      updatePoint = bettingPoint;
    } else {
      diceDayPoint = -1 * bettingPoint;
    }

    GameEntity gameEntity = getOrResetGameEntity();
    gameEntity.setDiceDayPoint(gameEntity.getDiceDayPoint() + diceDayPoint);
    gameRepository.save(gameEntity);
    pointLogService.createPointSaveLog(memberEntity,
        new PointLogRequest(LocalDateTime.now(), updatePoint, "주사위 게임 결과"));
    return gameEntity.getDiceDayPoint();
  }

  public Boolean isOverRouletteTimes() {

    GameEntity gameEntity = getOrResetGameEntity();
    return gameEntity.getRoulettePerDay() > ROULETTE_MAX_PLAYTIME;
  }

  public RouletteDto checkRoulleteInfo() {
    RouletteDto rouletteDto = new RouletteDto();
    GameEntity gameEntity = getOrResetGameEntity();
    rouletteDto.setRoulettePerDay(gameEntity.getRoulettePerDay());

    return rouletteDto;
  }

  @Transactional
  public RouletteDto playRouletteGame() {

    RouletteDto rouletteDto = new RouletteDto();
    MemberEntity memberEntity = getMemberEntityWithJWT();
    GameEntity gameEntity = getOrResetGameEntity();
    gameEntity.increaseRouletteTimes();
    rouletteDto.setRoulettePerDay(gameEntity.getRoulettePerDay());
    gameEntity.setLastPlayTime(LocalDateTime.now());

    pointLogService.createPointUseLog(memberEntity,
        new PointLogRequest(LocalDateTime.now(), ROULETTE_FEE, "룰렛 게임 포인트 차감"));
    List<Integer> points = new ArrayList<>();
    List<Integer> restricts = ROULETTE_LIST;
    for (int i = 0; i < restricts.size(); i += 2) {
      points.add(getRandomPoint(restricts.get(i), restricts.get(i + 1)));
    }
    rouletteDto.setRoulettePoints(points);
    int idx = (int) (Math.random() * 8);
    rouletteDto.setRoulettePointIdx(idx);
    gameEntity.setRouletteDayPoint(gameEntity.getRouletteDayPoint() + points.get(idx));
    gameRepository.save(gameEntity);

    pointLogService.createPointSaveLog(memberEntity,
        new PointLogRequest(LocalDateTime.now(), points.get(idx), "룰렛 게임 결과"));
    rouletteDto.setTodayResult(gameEntity.getRouletteDayPoint());
    return rouletteDto;
  }

  public int getRandomPoint(int start, int end) {
    // 마지막 범위 1000~5000은 둘다 닫힌구간이기에 필요한 계산식
    if (end == 5000) {
      end += 1000;
    }
    int inc = (end - start) / 5;

    List<Integer> randomLists = List.of(start, start + inc, start + inc * 2, start + inc * 3,
        start + inc * 4);

    return randomLists.get((int) (Math.random() * 5));
  }

  public Boolean isOverLottoTimes() {

    GameEntity gameEntity = getOrResetGameEntity();
    return gameEntity.getRoulettePerDay() > LOTTO_MAX_PLAYTIME;
  }

  public Integer checkLottoTimes() {

    GameEntity gameEntity = getOrResetGameEntity();
    return gameEntity.getLottoPerDay();
  }

  @Transactional
  public LottoDto playLottoGame() {

    MemberEntity memberEntity = getMemberEntityWithJWT();
    GameEntity gameEntity = getOrResetGameEntity();
    gameEntity.increaseLottoTimes();
    gameEntity.setLastPlayTime(LocalDateTime.now());

    pointLogService.createPointUseLog(memberEntity,
        new PointLogRequest(LocalDateTime.now(), LOTTO_FEE, "로또 게임 포인트 차감"));

    int result;
    int idx;
    final double prob = Math.random();
    if (prob <= FIRST_PROB) {
      result = FIRST_POINT;
      idx = 1;
    } else if (prob <= SECOND_PROB) {
      result = SECOND_POINT;
      idx = 2;
    } else if (prob <= THIRD_PROB) {
      result = THIRD_POINT;
      idx = 3;
    } else if (prob <= FOURTH_PROB) {
      result = FOURTH_POINT;
      idx = 4;
    } else if (prob <= FIFTH_PROB) {
      result = FIFTH_POINT;
      idx = 5;
    } else {
      result = LAST_POINT;
      idx = 6;
    }

    gameEntity.setLottoDayPoint(gameEntity.getLottoDayPoint() + result - LOTTO_FEE);
    gameRepository.save(gameEntity);

    LottoDto lottoDto = new LottoDto(idx, gameEntity.getLottoDayPoint());
    pointLogService.createPointSaveLog(memberEntity,
        new PointLogRequest(LocalDateTime.now(), result, "로또 게임 결과"));

    return lottoDto;
  }

  public GameEntity getOrResetGameEntity() {

    MemberEntity memberEntity = getMemberEntityWithJWT();
    Optional<GameEntity> optionalGameEntity = gameRepository.findByMember(memberEntity);

    if (optionalGameEntity.isEmpty()) {
      GameEntity gameEntity = GameEntity.builder().member(memberEntity).dicePerDay(0).lottoPerDay(0)
          .roulettePerDay(0).lastPlayTime(LocalDateTime.now()).diceDayPoint(0).rouletteDayPoint(0)
          .lottoDayPoint(0).build();
      gameRepository.save(gameEntity);
      return gameEntity;

    } else if (!(isToday(Timestamp.valueOf(optionalGameEntity.get().getLastPlayTime())))) {
      optionalGameEntity.get().reset();
      gameRepository.save(optionalGameEntity.get());
    }

    return optionalGameEntity.get();
  }

  private MemberEntity getMemberEntityWithJWT() {
    Long memberId = authService.getMemberIdByJWT();
    Optional<MemberEntity> member = memberRepository.findById(memberId);
    if (member.isEmpty()) {
      throw new CustomMemberNotFoundException();
    }
    return member.get();
  }

  public HashMap<String, Object> getAllGameInfo() throws IllegalAccessException {
    Field[] declaredFields = GameInfoDto.class.getDeclaredFields();
    HashMap<String, Object> staticFields = new HashMap<>();
    for (Field field : declaredFields) {
      if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
        staticFields.put(field.getName(), field.get(null));
      }
    }
    return staticFields;
  }
}
