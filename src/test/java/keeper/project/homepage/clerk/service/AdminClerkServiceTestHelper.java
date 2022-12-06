package keeper.project.homepage.clerk.service;

import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.DORMANT_MEMBER;

import java.util.List;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.member.repository.MemberTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class AdminClerkServiceTestHelper {

  @Autowired
  AdminMeritService adminMeritService;

  @Autowired
  MemberTypeRepository memberTypeRepository;

  @Autowired
  MemberRepository memberRepository;
  private MemberEntity clerk;

  @BeforeEach
  void beforeEach() {
    MemberTypeEntity dormantMember = memberTypeRepository.getById(DORMANT_MEMBER.getId());
    clerk = generateMember("서기", 12F, dormantMember);
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(clerk.getId(), clerk.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_서기"))));
  }

  public MemberEntity generateMember(String name, Float generation, MemberTypeEntity type) {
    final long epochTime = System.nanoTime();
    return memberRepository.save(
        MemberEntity.builder()
            .loginId("abcd1234" + epochTime)
            .emailAddress("test1234@keeper.co.kr" + epochTime)
            .password("1234")
            .studentId("1234" + epochTime)
            .nickName("nick" + epochTime)
            .realName(name)
            .generation(generation)
            .memberType(type)
            .build());
  }
}
