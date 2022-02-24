package keeper.project.homepage.repository;

import keeper.project.homepage.entity.member.EmailAuthRedisEntity;
import keeper.project.homepage.repository.member.EmailAuthRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisRepositoryTest {

  @Autowired
  private EmailAuthRedisRepository emailAuthRedisRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private final String email = "gusah009@naver.com";
  private final String authCode = "123456";

  @Test
  @DisplayName("이메일 인증을 위한 Redis 테스트")
  void emailAuthTest() {
    EmailAuthRedisEntity emailAuthRedisEntity = new EmailAuthRedisEntity(email, authCode);

    // 저장
    emailAuthRedisRepository.save(emailAuthRedisEntity);

    // `keyspace:id` 값을 가져옴
    EmailAuthRedisEntity getEmailAuthRedisEntity = emailAuthRedisRepository.findById(
        emailAuthRedisEntity.getEmail()).get();
    assertEquals(getEmailAuthRedisEntity.getEmail(), email);
    assertEquals(getEmailAuthRedisEntity.getAuthCode(), authCode);

    // Email Auth Entity 의 @RedisHash 에 정의되어 있는 keyspace 에 속한 키의 갯수를 구함
//        Long emailAuthCountBeforeDelete = emailAuthRedisRepository.count();
//        assertEquals(1, emailAuthCountBeforeDelete);

    // 삭제
    emailAuthRedisRepository.delete(emailAuthRedisEntity);
  }

  @Test
  void testStrings() {
    // given
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    String key = "stringKey";

    // when
    valueOperations.set(key, "hello");

    // then
    String value = valueOperations.get(key);
    assertThat(value).isEqualTo("hello");

    redisTemplate.delete(key);
  }


  @Test
  void testSet() {
    // given
    SetOperations<String, String> setOperations = redisTemplate.opsForSet();
    String key = "setKey";

    // when
    setOperations.add(key, "h", "e", "l", "l", "o");

    // then
    Set<String> members = setOperations.members(key);
    Long size = setOperations.size(key);

    assertThat(members).containsOnly("h", "e", "l", "o");
    assertThat(size).isEqualTo(4);

    redisTemplate.delete(key);
  }

  @Test
  void testHash() {
    // given
    HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
    String initKey = "hashKey";
    String initHashKey = "hello";
    String initValue = "world";

    // when
    hashOperations.put(initKey, initHashKey, initValue);

    // then
    Object value = hashOperations.get(initKey, "hello");
    assertThat(value).isEqualTo("world");

    Map<Object, Object> entries = hashOperations.entries(initKey);
    assertThat(entries.keySet()).containsExactly("hello");
    assertThat(entries.values()).containsExactly("world");

    Long size = hashOperations.size(initKey);
    assertThat(size).isEqualTo(entries.size());

    hashOperations.delete(initKey, initHashKey);
  }
}