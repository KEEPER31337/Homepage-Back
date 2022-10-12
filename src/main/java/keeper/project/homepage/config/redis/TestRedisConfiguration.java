package keeper.project.homepage.config.redis;

import java.io.IOException;
import java.net.ServerSocket;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import redis.embedded.RedisServer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
@Profile("test")
public class TestRedisConfiguration {

  private final RedisConfig redisConfig;

  private RedisServer redisServer;

  @PostConstruct
  public void postConstruct() {
    this.redisServer = RedisServer.builder()
        .port(redisConfig.getPort())
        .setting("maxmemory 128M")
        .build();
    redisServer.start();
  }

  @PreDestroy
  public void preDestroy() {
    if (redisServer != null) {
      redisServer.stop();
    }
  }
}