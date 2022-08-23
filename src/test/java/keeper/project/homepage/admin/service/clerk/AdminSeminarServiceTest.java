package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.admin.service.clerk.AdminSeminarService.ABSENCE_DEMERIT;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.PERSONAL;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomSeminarAttendanceNotFoundException;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class AdminSeminarServiceTest {

  @Autowired
  EntityManager em;
  @Autowired
  AdminSeminarService adminSeminarService;
  @Autowired
  SeminarAttendanceRepository seminarAttendanceRepository;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  SeminarRepository seminarRepository;

  @Transactional
  @Test
  @DisplayName("[SUCCESS] 개인사유 출석 수정")
  void updateSeminarAttendanceTest() {
    // given
    SeminarEntity seminar = seminarRepository.getById(1L);
    MemberEntity member = memberRepository.getById(1L);
    generateSeminarAttendance(member, seminar, ABSENCE);
    em.flush();
    em.clear();
    SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(4L)
        .absenceExcuse("예비군 훈련")
        .build();

    // when
    adminSeminarService.updateSeminarAttendanceStatus(seminar.getId(), member.getId(), request);
    SeminarAttendanceEntity find = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow(CustomSeminarAttendanceNotFoundException::new);

    //then
    Assertions.assertThat(find.getSeminarAttendanceStatusEntity()).isEqualTo(PERSONAL);
    Assertions.assertThat(find.getSeminarAttendanceExcuseEntity().getAbsenceExcuse())
        .isEqualTo("예비군 훈련");
  }

  @Transactional
  @Test
  @DisplayName("[SUCCESS] 결석으로 수정시 벌점 3점 증가")
  void updateAbsenceTest() {
    // given
    SeminarEntity seminar = seminarRepository.getById(1L);
    MemberEntity member = memberRepository.getById(1L);
    generateSeminarAttendance(member, seminar, ATTENDANCE);
    Integer beforeDemerit = member.getDemerit();
    em.flush();
    em.clear();
    SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(3L)
        .build();

    // when
    adminSeminarService.updateSeminarAttendanceStatus(seminar.getId(), member.getId(), request);
    SeminarAttendanceEntity find = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow(CustomSeminarAttendanceNotFoundException::new);
    Integer afterDemerit = find.getMemberEntity().getDemerit();

    //then
    Assertions.assertThat(find.getSeminarAttendanceStatusEntity()).isEqualTo(ABSENCE);
    Assertions.assertThat(beforeDemerit + ABSENCE_DEMERIT).isEqualTo(afterDemerit);
  }

  @Transactional
  @Test
  @DisplayName("[SUCCESS] 이전 상태가 결석인 경우 상태 변경시 벌점 3점 감소")
  void updateStatusWhenBeforeStateIsAbsence() {
    // given
    SeminarEntity seminar = seminarRepository.getById(1L);
    MemberEntity member = memberRepository.getById(1L);
    generateSeminarAttendance(member, seminar, ABSENCE);
    em.flush();
    em.clear();
    Integer beforeDemerit = memberRepository.getById(1L).getDemerit();
    SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(1L)
        .build();
    // when
    adminSeminarService.updateSeminarAttendanceStatus(seminar.getId(), member.getId(), request);
    SeminarAttendanceEntity find = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow(CustomSeminarAttendanceNotFoundException::new);
    Integer afterDemerit = find.getMemberEntity().getDemerit();

    //then
    Assertions.assertThat(find.getSeminarAttendanceStatusEntity()).isEqualTo(ATTENDANCE);
    Assertions.assertThat(beforeDemerit - ABSENCE_DEMERIT).isEqualTo(afterDemerit);
  }

  @Transactional
  @Test
  @DisplayName("[SUCCESS] 지각으로 변경 시 세미나에서 지각을 홀수번 한 적이 있으면 결석으로 간주")
  void updateStatusToLatenessIfEverBeenLate() {
    // given
    SeminarEntity seminar1 = seminarRepository.getById(1L);
    SeminarEntity seminar2 = generateSeminar(LocalDateTime.now());
    MemberEntity member = memberRepository.getById(1L);
    generateSeminarAttendance(member, seminar1, LATENESS);
    generateSeminarAttendance(member, seminar2, ATTENDANCE);
    em.flush();
    em.clear();
    Integer beforeDemerit = memberRepository.getById(1L).getDemerit();
    SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(2L)
        .build();
    // when
    adminSeminarService.updateSeminarAttendanceStatus(seminar2.getId(), member.getId(), request);
    SeminarAttendanceEntity find = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar2, member).orElseThrow(CustomSeminarAttendanceNotFoundException::new);
    Integer afterDemerit = find.getMemberEntity().getDemerit();

    //then
    Assertions.assertThat(find.getSeminarAttendanceStatusEntity()).isEqualTo(LATENESS);
    Assertions.assertThat(beforeDemerit + ABSENCE_DEMERIT).isEqualTo(afterDemerit);
  }

  SeminarEntity generateSeminar(LocalDateTime openTime) {
    return seminarRepository.save(SeminarEntity.builder()
        .name(openTime + "세미나")
        .openTime(openTime)
        .build()
    );
  }

  SeminarAttendanceEntity generateSeminarAttendance(MemberEntity member,
      SeminarEntity seminar, SeminarAttendanceStatusEntity seminarAttendanceStatus) {
    return seminarAttendanceRepository.save(
        SeminarAttendanceEntity.builder()
            .memberEntity(member)
            .seminarEntity(seminar)
            .seminarAttendanceStatusEntity(seminarAttendanceStatus)
            .seminarAttendTime(LocalDateTime.now().withNano(0))
            .build()
    );
  }

}
