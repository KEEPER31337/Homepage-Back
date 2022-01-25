package keeper.project.homepage.service.member;

import java.util.Collections;
import java.util.Optional;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

  @Autowired
  private MemberRepository memberRepository;

  private MemberEntity memberEntity;

  @BeforeEach
  public void setup() throws Exception {
    memberEntity = memberRepository.save(MemberEntity.builder()
        .loginId("로그인")
        .password("비밀번호")
        .realName("이름")
        .nickName("닉네임")
        .emailAddress("이메일")
        .studentId("학번")
        .roles(Collections.singletonList("ROLE_USER")).build());
  }

  @Test
  public void findByIdTest() {
    Long findId = memberEntity.getId();
    Optional<MemberEntity> findMember = memberRepository.findById(findId);
    Assertions.assertTrue(findMember.isPresent());
  }
}
