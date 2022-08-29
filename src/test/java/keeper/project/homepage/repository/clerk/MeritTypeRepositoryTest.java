package keeper.project.homepage.repository.clerk;

import static keeper.project.homepage.entity.clerk.MeritTypeEntity.MeritType.ABSENCE;
import static keeper.project.homepage.entity.clerk.MeritTypeEntity.MeritType.ATTENDANCE_AWARD;
import static keeper.project.homepage.entity.clerk.MeritTypeEntity.MeritType.BEST_STUDY;
import static keeper.project.homepage.entity.clerk.MeritTypeEntity.MeritType.BEST_TECH_DOC;
import static keeper.project.homepage.entity.clerk.MeritTypeEntity.MeritType.MANY_TECH_DOC;
import static keeper.project.homepage.entity.clerk.MeritTypeEntity.MeritType.PUBLIC_ANNOUNCEMENT;
import static keeper.project.homepage.entity.clerk.MeritTypeEntity.MeritType.WIN_A_CONTEST;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import keeper.project.homepage.entity.clerk.MeritTypeEntity;
import keeper.project.homepage.entity.clerk.MeritTypeEntity.MeritType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MeritTypeRepositoryTest extends MeritRepositoryTestHelper{
  @Test
  @DisplayName("상벌점 종류 개수 테스트")
  void numberOfMeritTypeTest() {
    // given

    // when
    List<MeritTypeEntity> meritTypes = meritTypeRepository.findAll();

    // then
    assertThat(meritTypes.size()).isEqualTo(MeritType.values().length);
  }

  @Test
  @DisplayName("상벌점 종류 테스트")
  void meritTypeTest() {
    // given

    // when
    MeritTypeEntity public_announcement = meritTypeRepository.getById(PUBLIC_ANNOUNCEMENT.getId());
    MeritTypeEntity bestTechDoc = meritTypeRepository.getById(BEST_TECH_DOC.getId());
    MeritTypeEntity manyTechDoc = meritTypeRepository.getById(MANY_TECH_DOC.getId());
    MeritTypeEntity bestStudy = meritTypeRepository.getById(BEST_STUDY.getId());
    MeritTypeEntity winAContest = meritTypeRepository.getById(WIN_A_CONTEST.getId());
    MeritTypeEntity attendanceAward = meritTypeRepository.getById(ATTENDANCE_AWARD.getId());
    MeritTypeEntity absence = meritTypeRepository.getById(ABSENCE.getId());

    // then
    assertThat(public_announcement.getDetail()).isEqualTo(PUBLIC_ANNOUNCEMENT.getDetail());
    assertThat(bestTechDoc.getDetail()).isEqualTo(BEST_TECH_DOC.getDetail());
    assertThat(manyTechDoc.getDetail()).isEqualTo(MANY_TECH_DOC.getDetail());
    assertThat(bestStudy.getDetail()).isEqualTo(BEST_STUDY.getDetail());
    assertThat(winAContest.getDetail()).isEqualTo(WIN_A_CONTEST.getDetail());
    assertThat(attendanceAward.getDetail()).isEqualTo(ATTENDANCE_AWARD.getDetail());
    assertThat(absence.getDetail()).isEqualTo(ABSENCE.getDetail());
  }
}
