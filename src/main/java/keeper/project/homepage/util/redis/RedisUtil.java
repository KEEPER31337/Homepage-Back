package keeper.project.homepage.util.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RedisUtil {

  private final StringRedisTemplate redisTemplate;

  public Long increaseAndGet(String key) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    return valueOperations.increment(key);
  }

  public Long increaseAndGetWithExpire(String key, long timeToLiveSeconds) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    redisTemplate.expire(key, timeToLiveSeconds, TimeUnit.SECONDS);
    return valueOperations.increment(key);
  }

  public String getData(String key) { // key를 통해 value(데이터)를 얻는다.
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    return valueOperations.get(key);
  }

  public void setDataExpire(String key, String value, long duration) {
    //  duration 동안 (key, value)를 저장한다.
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    Duration expireDuration = Duration.ofMillis(duration);
    valueOperations.set(key, value, expireDuration);
  }

  public void deleteData(String key) {
    // 데이터 삭제
    redisTemplate.delete(key);
  }
}
