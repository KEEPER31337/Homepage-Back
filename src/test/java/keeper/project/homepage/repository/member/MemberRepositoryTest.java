package keeper.project.homepage.repository.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional
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
}