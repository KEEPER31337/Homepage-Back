package keeper.project.homepage.repository.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log4j2
public class MemberRepositoryTest extends MemberRepositoryTestHelper {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("LoginId로 유저 찾기")
  public void whenFindByUid_thenReturnUser() {
    // given
    MemberEntity member = generateMember();

    // when
    Optional<MemberEntity> findMember = memberRepository.findByLoginId(member.getLoginId());

    // then
    assertThat(findMember).isNotNull();
    assertThat(findMember).isPresent();
    assertThat(findMember.get().getRealName()).isEqualTo(member.getRealName());
    assertThat(findMember.get().getPassword()).isEqualTo(member.getPassword());
    assertThat(findMember.get().getLoginId()).isEqualTo(member.getLoginId());
    assertThat(findMember.get().getNickName()).isEqualTo(member.getNickName());
    assertThat(findMember.get().getEmailAddress()).isEqualTo(member.getEmailAddress());
  }

  @Test
  @DisplayName("유저 비밀번호 변경")
  public void memberPasswordChange() {
    //given
    MemberEntity member = generateMember();
    String newPassword = member.getPassword() + "1";

    // when
    String newHashPassword = passwordEncoder.encode(newPassword);
    member.changePassword(newHashPassword);
    MemberEntity saveMember = memberRepository.save(member);

    // then
    assertThat(newHashPassword).isEqualTo(saveMember.getPassword());
  }

  @Test
  @DisplayName("[SUCCESS] 회원 역할 삭제")
  public void removeMemberJob() {
    //given
    MemberEntity member = generateMember();
    MemberJobEntity jobToBeDeleted = memberJobRepository.getById(1L);
    MemberJobEntity jobToBeRemained = memberJobRepository.getById(2L);
    assignJob(member, jobToBeDeleted);
    assignJob(member, jobToBeRemained);

    // when
    member.removeMemberJob(jobToBeDeleted);
    em.clear();
    em.flush();

    // then
    List<MemberJobEntity> resultMemberJobs = member.getMemberJobs().stream()
        .map(MemberHasMemberJobEntity::getMemberJobEntity)
        .toList();
    assertThat(resultMemberJobs).doesNotContain(jobToBeDeleted);
    assertThat(resultMemberJobs).contains(jobToBeRemained);
  }

  @Test
  @DisplayName("[FAIL] 회원 역할 삭제 - 회원이 가지고 있지 않은 역할")
  public void removeMemberJob_memberDoesntHaveJob() {
    //given
    MemberEntity member = generateMember();
    MemberJobEntity jobMemberDoesntHave = memberJobRepository.getById(1L);
    MemberJobEntity jobMemberHas = memberJobRepository.getById(2L);
    assignJob(member, jobMemberHas);

    // when
    member.removeMemberJob(jobMemberDoesntHave);
    em.clear();
    em.flush();

    // then
    List<MemberJobEntity> resultMemberJobs = member.getMemberJobs().stream()
        .map(MemberHasMemberJobEntity::getMemberJobEntity)
        .toList();
    assertThat(resultMemberJobs).doesNotContain(jobMemberDoesntHave);
    assertThat(resultMemberJobs).contains(jobMemberHas);
  }
}