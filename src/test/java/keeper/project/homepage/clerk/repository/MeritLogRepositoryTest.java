package keeper.project.homepage.clerk.repository;

import java.time.LocalDate;
import keeper.project.homepage.clerk.entity.MeritLogEntity;
import keeper.project.homepage.clerk.entity.MeritTypeEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MeritLogRepositoryTest extends MeritRepositoryTestHelper {

  @Test
  @DisplayName("[SUCCESS] 상벌점 내역 추가 테스트")
  void addMeritLogTest() {
    // given
    MemberEntity awarder = generateMember();
    MemberEntity giver = generateMember();
    MeritTypeEntity type = generateMeritType(3, true, "각종대외발표");
    MeritLogEntity meritLog = MeritLogEntity.builder()
        .awarder(awarder)
        .giver(giver)
        .meritType(type)
        .date(LocalDate.now())
        .build();

    MeritLogEntity save = meritLogRepository.save(meritLog);
    em.flush();
    em.clear();

    // when
    MeritLogEntity find = meritLogRepository.findById(save.getId()).orElseThrow();

    // then
    Assertions.assertThat(find.getAwarder().getId()).isEqualTo(save.getAwarder().getId());
    Assertions.assertThat(find.getGiver().getId()).isEqualTo(save.getGiver().getId());
    Assertions.assertThat(find.getMeritType().getId()).isEqualTo(save.getMeritType().getId());
    Assertions.assertThat(find.getDate()).isEqualTo(save.getDate());
  }

  private MeritTypeEntity generateMeritType(Integer merit, Boolean isMerit, String detail) {
    return meritTypeRepository.save(MeritTypeEntity.newInstance(merit, isMerit, detail));
  }
}
