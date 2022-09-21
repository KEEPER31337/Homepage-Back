package keeper.project.homepage.repository.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberHasMemberJobRepositoryTest extends MemberRepositoryTestHelper {

  @Test
  @DisplayName("회장, 부회장 ROLE이 포함된 memberHasMemberJobEntity 가져오기 - 개수 검증")
  void findByMemberJobEntityIn_count() {
    //given
    List<MemberEntity> members = generateMemberList(5);
    MemberJobEntity master = generateMemberJob("회장");
    MemberJobEntity subMaster = generateMemberJob("부회장");
    MemberJobEntity general = generateMemberJob("회원");

    assignJob(members.get(0), master); // 포함 O
    assignJob(members.get(1), subMaster); // 포함 O
    assignJob(members.get(2), subMaster); // 포함 O
    assignJob(members.get(3), general); // 회원 포함 X
    assignJob(members.get(4), general); // 회원 포함 X

    //when
    List<MemberHasMemberJobEntity> result = memberHasMemberJobRepository.findByMemberJobEntityIn(
        List.of(master, subMaster));
    List<MemberEntity> resultMembers = result.stream()
        .map(MemberHasMemberJobEntity::getMemberEntity).toList();

    //then
    assertThat(result).hasSize(3);
    assertThat(resultMembers).contains(members.get(0));
    assertThat(resultMembers).contains(members.get(1));
    assertThat(resultMembers).contains(members.get(2));
    assertThat(resultMembers).doesNotContain(members.get(3));
    assertThat(resultMembers).doesNotContain(members.get(4));
  }
}