package keeper.project.homepage.repository.clerk;

import javax.persistence.EntityManager;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.user.service.member.MemberUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MeritRepositoryTestHelper {

  @Autowired
  EntityManager em;
  @Autowired
  MeritLogRepository meritLogRepository;

  @Autowired
  MeritTypeRepository meritTypeRepository;

  @Autowired
  MemberRepository memberRepository;

  protected MemberEntity generateMember() {
    final long epochTime = System.nanoTime();
    return memberRepository.save(
        MemberEntity.builder()
            .loginId("abcd1234" + epochTime)
            .emailAddress("test1234@keeper.co.kr" + epochTime)
            .password("1234")
            .studentId("1234" + epochTime)
            .nickName("nick" + epochTime)
            .realName("name" + epochTime)
            .build());
  }

}