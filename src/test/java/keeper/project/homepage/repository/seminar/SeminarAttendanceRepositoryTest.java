package keeper.project.homepage.repository.seminar;

import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.seminar.SeminarAttendanceEntity;
import keeper.project.homepage.entity.seminar.SeminarAttendanceExcuseEntity;
import keeper.project.homepage.entity.seminar.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.seminar.SeminarEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class SeminarAttendanceRepositoryTest extends SeminarRepositoryTestHelper {

  @Test
  @DisplayName("세미나 출석 시간 테스트")
  void seminarAttendanceTimeTest() {
    // given
    MemberEntity memberEntity = memberRepository.getById(1L);
    SeminarEntity seminarEntity = seminarRepository.getById(1L);
    SeminarAttendanceEntity seminarAttendanceEntity = generateSeminarAttendance(memberEntity,
        seminarEntity,
        SeminarAttendanceStatusEntity.ATTENDANCE);

    em.flush();
    em.clear();

    // when
    SeminarAttendanceEntity findSeminarAttendanceEntity = seminarAttendanceRepository.getById(
        seminarAttendanceEntity.getId());
    // then
    Assertions.assertThat(seminarAttendanceEntity.getSeminarAttendTime())
        .isEqualTo(findSeminarAttendanceEntity.getSeminarAttendTime());
  }
  @Test
  @DisplayName("세미나, 멤버로 세미나 출석 조회 테스트")
  void findSeminarAttendanceBySeminarAndMemberTest() {
    // given
    MemberEntity memberEntity = memberRepository.getById(1L);
    SeminarEntity seminarEntity = seminarRepository.getById(1L);
    SeminarAttendanceEntity seminarAttendanceEntity = generateSeminarAttendance(memberEntity,
        seminarEntity,
        SeminarAttendanceStatusEntity.ATTENDANCE);

    em.flush();
    em.clear();

    // when
    SeminarAttendanceEntity findSeminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminarEntity, memberEntity);

    // then
    Assertions.assertThat(seminarAttendanceEntity.getId())
        .isEqualTo(findSeminarAttendanceEntity.getId());
  }

  @Test
  @DisplayName("세미나 개인사유 결석 테스트")
  void personalAbsenceTest() {
    // given
    MemberEntity memberEntity = memberRepository.getById(1L);
    SeminarEntity seminarEntity = seminarRepository.getById(1L);
    SeminarAttendanceEntity seminarAttendanceEntity = generateSeminarAttendance(memberEntity,
        seminarEntity, SeminarAttendanceStatusEntity.PERSONAL);

    SeminarAttendanceExcuseEntity seminarAttendanceExcuseEntity = generateSeminarAttendanceExcuse(
        seminarAttendanceEntity);
    em.flush();
    em.clear();

    // when
    SeminarAttendanceEntity findSeminarAttendanceEntity = seminarAttendanceRepository.getById(
        seminarAttendanceEntity.getId());

    // then
    Assertions.assertThat(seminarAttendanceExcuseEntity.getAbsenceExcuse())
        .isEqualTo(
            findSeminarAttendanceEntity.getSeminarAttendanceExcuseEntity().getAbsenceExcuse());
  }


}