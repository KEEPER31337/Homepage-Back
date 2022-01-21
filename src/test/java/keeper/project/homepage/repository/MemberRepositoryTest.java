package keeper.project.homepage.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;
import keeper.project.homepage.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("LoginId로 유저 찾기")
  public void whenFindByUid_thenReturnUser() {
    String loginId = "hyeonmomo";
    String realName = "JeongHyeonMo";
    String nickName = "JeongHyeonMo";
    String emailAddress = "gusah@naver.com";
    String studentId = "201724579";
    // given
    memberRepository.save(MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode("1234"))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .roles(Collections.singletonList("ROLE_USER"))
        .build());
    // when
    Optional<MemberEntity> member = memberRepository.findByLoginId(loginId);
    // then
    assertNotNull(member);// member객체가 null이 아닌지 체크
    assertTrue(member.isPresent()); // member객체가 존재여부 true/false 체크
    assertEquals(member.get().getRealName(), realName); // member객체의 realName과 realName변수 값이 같은지 체크
    assertThat(member.get().getRealName(),
        is(realName)); // member객체의 realName과 realName변수 값이 같은지 체크
  }
}