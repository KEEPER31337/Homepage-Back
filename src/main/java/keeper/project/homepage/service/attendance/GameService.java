package keeper.project.homepage.service.attendance;

import static keeper.project.homepage.dto.attendance.GameInfoDto.*;
import static keeper.project.homepage.service.attendance.DateUtils.isToday;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.attendance.GameInfoDto;
import keeper.project.homepage.dto.attendance.RouletteDto;
import keeper.project.homepage.entity.attendance.GameEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.attendance.GameRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GameService {

  private final AuthService authService;
  private final MemberRepository memberRepository;
  private final GameRepository gameRepository;

  public Integer checkDiceTimes() {

    GameEntity gameEntity = getOrResetGameEntity();
    return gameEntity.getDicePerDay();
  }

  @Transactional
  public void playDiceGame(Integer bettingPoint) {

    MemberEntity memberEntity = getMemberEntityWithJWT();
    GameEntity gameEntity = getOrResetGameEntity();
    gameEntity.increaseDiceTimes();
    gameEntity.setLastPlayTime(new Date());
    gameRepository.save(gameEntity);
    memberEntity.updatePoint(memberEntity.getPoint() - bettingPoint);
    memberRepository.save(memberEntity);
  }

  public void saveDiceGame(Integer result, Integer bettingPoint) {

    MemberEntity memberEntity = getMemberEntityWithJWT();
    int updatePoint = 0;
    if (result == 1) {
      updatePoint = 2 * bettingPoint;
    } else if (result == 0) {
      updatePoint = bettingPoint;
    }
    memberEntity.updatePoint(memberEntity.getPoint() + updatePoint);
    memberRepository.save(memberEntity);
  }

  @Transactional
  public RouletteDto playRouletteGame() {

    RouletteDto rouletteDto = new RouletteDto();
    MemberEntity memberEntity = getMemberEntityWithJWT();
    GameEntity gameEntity = getOrResetGameEntity();
    gameEntity.increaseRouletteTimes();
    rouletteDto.setRoulettePerDay(gameEntity.getRoulettePerDay());
    gameEntity.setLastPlayTime(new Date());
    gameRepository.save(gameEntity);

    List<Integer> points = new ArrayList<>();
    List<Integer> restricts = ROULETTE_LIST;
    for (int i = 0; i < restricts.size(); i += 2) {
      points.add(getRandomPoint(restricts.get(i), restricts.get(i + 1)));
    }
    rouletteDto.setRoulettePoints(points);
    int idx = (int) (Math.random() * 8);
    rouletteDto.setRoulettePointIdx(idx);
    memberEntity.updatePoint(memberEntity.getPoint() + points.get(idx) - ROULETTE_FEE);
    memberRepository.save(memberEntity);

    return rouletteDto;
  }

  public int getRandomPoint(int start, int end) {
    if (end == 5000) {
      end += 1000;
    }
    int inc = (end - start) / 5;

    List<Integer> randomLists = List.of(start, start + inc, start + inc * 2, start + inc * 3,
        start + inc * 4);

    return randomLists.get((int) (Math.random() * 5));
  }

  public Integer checkLottoTimes() {

    GameEntity gameEntity = getOrResetGameEntity();

    return gameEntity.getLottoPerDay();
  }

  @Transactional
  public Integer playLottoGame() {

    MemberEntity memberEntity = getMemberEntityWithJWT();
    GameEntity gameEntity = getOrResetGameEntity();
    gameEntity.increaseLottoTimes();
    gameEntity.setLastPlayTime(new Date());
    gameRepository.save(gameEntity);

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

    memberEntity.updatePoint(memberEntity.getPoint() + result - LOTTO_FEE);
    memberRepository.save(memberEntity);

    return idx;
  }

  public GameEntity getOrResetGameEntity() {

    MemberEntity memberEntity = getMemberEntityWithJWT();
    Optional<GameEntity> optionalGameEntity = gameRepository.findByMember(memberEntity);

    if (optionalGameEntity.isEmpty()) {
      GameEntity gameEntity = GameEntity.builder().member(memberEntity).dicePerDay(0).lottoPerDay(0)
          .roulettePerDay(0).lastPlayTime(new Date()).build();
      gameRepository.save(gameEntity);
      return gameEntity;

    } else if (!(isToday(optionalGameEntity.get().getLastPlayTime()))) {
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
