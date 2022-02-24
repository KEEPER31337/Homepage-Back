package keeper.project.homepage.entity.member;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "emailAuth", timeToLive = 300) // 유지시간 300s
public class EmailAuthRedisEntity {

  @Id
  private final String email;
  private final String authCode;

  public EmailAuthRedisEntity(String email, String authCode) {
    this.email = email;
    this.authCode = authCode;
  }
}
