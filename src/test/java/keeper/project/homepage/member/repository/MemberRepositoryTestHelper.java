package keeper.project.homepage.repository.member;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.repository.MemberHasMemberJobRepository;
import keeper.project.homepage.member.repository.MemberJobRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MemberRepositoryTestHelper {

  @Autowired
  protected EntityManager em;

  @Autowired
  protected MemberRepository memberRepository;

  @Autowired
  protected MemberHasMemberJobRepository memberHasMemberJobRepository;

  @Autowired
  protected MemberJobRepository memberJobRepository;

  protected List<MemberEntity> generateMemberList(int memberCount) {
    List<MemberEntity> result = new ArrayList<>();
    for (int i = 0; i < memberCount; i++) {
      MemberEntity member = generateMember();
      result.add(member);
    }
    return result;
  }

  protected MemberEntity generateMember() {
    final String epochTime = Long.toHexString(System.nanoTime()).substring(0, 10);

    MemberEntity memberEntity = memberRepository.save(
        MemberEntity.builder()
            .loginId("loginId_" + epochTime)
            .emailAddress("emailAddress_" + epochTime)
            .password("password_" + epochTime)
            .realName("realName_" + epochTime)
            .nickName("nickName_" + epochTime)
            .studentId("studentId_" + epochTime)
            .point(1000)
            .level(1)
            .merit(0)
            .demerit(0)
            .build());
    return memberEntity;
  }

  protected MemberHasMemberJobEntity assignJob(MemberEntity memberEntity,
      MemberJobEntity memberJob) {
    MemberHasMemberJobEntity hasMemberJobEntity = memberHasMemberJobRepository.save(
        MemberHasMemberJobEntity.builder()
            .memberJobEntity(memberJob)
            .memberEntity(memberEntity)
            .build());
    memberJob.getMembers().add(hasMemberJobEntity);
    memberEntity.getMemberJobs().add(hasMemberJobEntity);

    return hasMemberJobEntity;
  }

  protected List<MemberJobEntity> generateMemberJobList(String... jobNames) {
    List<MemberJobEntity> result = new ArrayList<>();
    for (String jobName : jobNames) {
      MemberJobEntity generatedJob = generateMemberJob(jobName);
      result.add(generatedJob);
    }
    return result;
  }

  protected MemberJobEntity generateMemberJob(String jobName) {
    MemberJobEntity memberJob = memberJobRepository.save(
        MemberJobEntity.builder()
            .name(jobName)
            .build());
    return memberJob;
  }
}
