package keeper.project.homepage.repository.clerk;

import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.exception.clerk.CustomSeminarAttendanceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SeminarAttendanceRepositoryTest extends SeminarRepositoryTestHelper {

  @Test
  @DisplayName("세미나 출석 시간 테스트")
  void seminarAttendanceTimeTest() {
    // given
    MemberEntity memberEntity = memberRepository.getById(1L);
    SeminarEntity seminarEntity = seminarRepository.getById(1L);
    SeminarAttendanceStatusEntity ATTENDANCE = seminarAttendanceStatusRepository.getById(1L);
    SeminarAttendanceEntity seminarAttendanceEntity = generateSeminarAttendance(memberEntity,
        seminarEntity,
        ATTENDANCE);

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
    SeminarAttendanceStatusEntity ATTENDANCE = seminarAttendanceStatusRepository.getById(1L);
    SeminarAttendanceEntity seminarAttendanceEntity = generateSeminarAttendance(memberEntity,
        seminarEntity, ATTENDANCE);

    em.flush();
    em.clear();

    // when
    SeminarAttendanceEntity findSeminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminarEntity, memberEntity).orElseThrow(CustomSeminarAttendanceNotFoundException::new);

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
    SeminarAttendanceStatusEntity PERSONAL = seminarAttendanceStatusRepository.getById(4L);

    SeminarAttendanceEntity seminarAttendanceEntity = generateSeminarAttendance(memberEntity,
        seminarEntity, PERSONAL);

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