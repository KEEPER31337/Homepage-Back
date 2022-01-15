package keeper.project.homepage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // 직접 달면 단위 테스트 시 오류가 발생하기에 별도 config 클래스 생성 후 어노테이션 추가
public class JpaConfig {
}
