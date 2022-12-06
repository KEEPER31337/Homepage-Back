package keeper.project.homepage.member.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class AdminMemberServiceTest {

  @Autowired
  EntityManager em;

  @Autowired
  AdminMemberService adminMemberService;

  @Autowired
  MemberRepository memberRepository;

  @Test
  @DisplayName("회원 상벌점 초기화 테스트")
  void initMembersMerit() {
    // given
    MemberEntity virtualMember = memberRepository.getById(1L);
    virtualMember.changeMerit(3);
    virtualMember.changeDemerit(2);

    // when
    adminMemberService.initMembersMerit();

    // then
    Assertions.assertThat(virtualMember.getMerit()).isZero();
    Assertions.assertThat(virtualMember.getMerit()).isZero();
  }

}