package keeper.project.homepage.repository.member;

import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.DORMANT_MEMBER;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.GRADUATED_MEMBER;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.NON_MEMBER;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.REGULAR_MEMBER;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.WITHDRAWAL_MEMBER;

import java.util.List;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity.memberType;
import keeper.project.homepage.member.repository.MemberTypeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MemberTypeRepositoryTest {
  @Autowired
  MemberTypeRepository memberTypeRepository;

  @Test
  @DisplayName("회원 타입 개수 테스트")
  void SeminarAttendanceStatusTest() {
    // given

    // when
    List<MemberTypeEntity> memberTypeEntities = memberTypeRepository.findAll();

    // then
    Assertions.assertThat(memberTypeEntities.size()).isEqualTo(memberType.values().length);
  }


  @Test
  @DisplayName("회원 타입 테스트")
  void SeminarAttendanceStatusTypeTest() {
    // given

    // when
    MemberTypeEntity non = memberTypeRepository.getById(
        REGULAR_MEMBER.getId());
    MemberTypeEntity regular = memberTypeRepository.getById(
        NON_MEMBER.getId());
    MemberTypeEntity dormant = memberTypeRepository.getById(
        DORMANT_MEMBER.getId());
    MemberTypeEntity graduated = memberTypeRepository.getById(
        GRADUATED_MEMBER.getId());
    MemberTypeEntity withdrawal = memberTypeRepository.getById(
        WITHDRAWAL_MEMBER.getId());

    // then
    Assertions.assertThat(non.getName()).isEqualTo(REGULAR_MEMBER.getName());
    Assertions.assertThat(regular.getName()).isEqualTo(NON_MEMBER.getName());
    Assertions.assertThat(dormant.getName()).isEqualTo(DORMANT_MEMBER.getName());
    Assertions.assertThat(graduated.getName()).isEqualTo(GRADUATED_MEMBER.getName());
    Assertions.assertThat(withdrawal.getName()).isEqualTo(WITHDRAWAL_MEMBER.getName());
  }
}
