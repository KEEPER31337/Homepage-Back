package keeper.project.homepage.clerk.service;

import static keeper.project.homepage.clerk.service.AdminSeminarService.ABSENCE_DEMERIT;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.BEFORE_ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.LATENESS;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.PERSONAL;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.DORMANT_MEMBER;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.REGULAR_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.clerk.dto.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.clerk.dto.request.SeminarCreateRequestDto;
import keeper.project.homepage.clerk.dto.response.SeminarCreateResponseDto;
import keeper.project.homepage.clerk.entity.MeritLogEntity;
import keeper.project.homepage.clerk.entity.MeritTypeEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus;
import keeper.project.homepage.clerk.entity.SeminarEntity;
import keeper.project.homepage.clerk.exception.CustomAttendanceAbsenceExcuseIsNullException;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.clerk.exception.CustomDuplicateSeminarException;
import keeper.project.homepage.clerk.exception.CustomMeritTypeNotFoundException;
import keeper.project.homepage.clerk.exception.CustomSeminarAttendanceNotFoundException;
import keeper.project.homepage.clerk.repository.MeritLogRepository;
import keeper.project.homepage.clerk.repository.MeritTypeRepository;
import keeper.project.homepage.clerk.repository.SeminarAttendanceRepository;
import keeper.project.homepage.clerk.repository.SeminarAttendanceStatusRepository;
import keeper.project.homepage.clerk.repository.SeminarRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.member.repository.MemberTypeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class AdminSeminarServiceTest extends AdminClerkServiceTestHelper {

  @Nested
  class SuccessTest {

    @Test
    @DisplayName("[SUCCESS] 세미나 생성 테스트")
    void seminarCreateTest() {
      // given
      MemberTypeEntity regularMember = memberTypeRepository.getById(REGULAR_MEMBER.getId());
      MemberTypeEntity dormantMember = memberTypeRepository.getById(DORMANT_MEMBER.getId());

      SeminarCreateRequestDto request = SeminarCreateRequestDto.builder()
          .openTime(LocalDateTime.now().plusWeeks(1L).withNano(0))
          .build();
      MemberEntity member1 = generateMember("이정학", 12.5F, dormantMember);
      MemberEntity member2 = generateMember("최우창", 12.5F, regularMember);
      MemberEntity member3 = generateMember("정현모", 8F, regularMember);
      MemberEntity member4 = generateMember("손현경", 13F, regularMember);

      // when
      SeminarCreateResponseDto responseDto = adminSeminarService.createSeminar(request);
      em.flush();
      em.clear();

      SeminarEntity seminar = seminarRepository.getById(responseDto.getId());
      List<SeminarAttendanceEntity> seminarAttendances = seminar.getSeminarAttendances();
      List<Long> memberIds = seminarAttendances.stream()
          .map(SeminarAttendanceEntity::getMemberEntity)
          .map(MemberEntity::getId)
          .toList();

      // then
      assertThat(responseDto.getId()).isEqualTo(seminar.getId());
      for (SeminarAttendanceEntity attendance : seminarAttendances) {
        assertThat(attendance.getSeminarAttendanceStatusEntity().getType()).isEqualTo(
            BEFORE_ATTENDANCE.getType());
      }
      assertThat(memberIds.size()).isEqualTo(3);
      assertThat(memberIds).contains(member2.getId(), member3.getId(), member4.getId());
    }

    @Test
    @DisplayName("[SUCCESS] 이전 출석 상태가 출석 전이고 결석을 하면 벌점 내역의 날짜는 당일이어야 한다.")
    void absenceLogDateTest() {

      // given
      SeminarEntity seminar = seminarRepository.getById(1L);
      LocalDateTime seminarInitTime = LocalDateTime.now().minusDays(6);
      MemberEntity member = memberRepository.getById(1L);
      SeminarAttendanceStatusEntity before = getSeminarAttendanceStatus(BEFORE_ATTENDANCE);
      SeminarAttendanceStatusEntity absence = getSeminarAttendanceStatus(ABSENCE);
      MeritTypeEntity absenceMeritType = getMeritType(ABSENCE);
      SeminarAttendanceEntity seminarAttendance =
          generateSeminarAttendance(member, seminar, before, seminarInitTime);

      // when
      adminSeminarService.processAttendance(seminarAttendance, absence, "");
      SeminarAttendanceEntity afterAttendance =
          seminarAttendanceRepository.findById(seminarAttendance.getId())
              .orElseThrow(CustomSeminarAttendanceNotFoundException::new);
      MeritLogEntity absenceLog = getMeritLog(member, absenceMeritType);

      //then
      assertThat(afterAttendance.getSeminarAttendanceStatusEntity().getType())
          .isEqualTo(ABSENCE.getType());
      assertThat(absenceLog.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("[SUCCESS] 이전 출석 상태가 출석 전이 아니면 출석 상태가 변해도 출석 시간은 변경되지 않는다.")
    void didNotChangeAttendTime() {

      // given
      SeminarEntity seminar = seminarRepository.getById(1L);
      LocalDateTime seminarInitTime = LocalDateTime.now().minusDays(6);
      MemberEntity member = memberRepository.getById(1L);
      SeminarAttendanceStatusEntity lateness = getSeminarAttendanceStatus(LATENESS);
      SeminarAttendanceStatusEntity attendance = getSeminarAttendanceStatus(ATTENDANCE);
      SeminarAttendanceEntity seminarAttendance =
          generateSeminarAttendance(member, seminar, lateness, seminarInitTime);

      // when
      adminSeminarService.processAttendance(seminarAttendance, attendance, "");
      SeminarAttendanceEntity afterAttendance =
          seminarAttendanceRepository.findById(seminarAttendance.getId())
              .orElseThrow(CustomSeminarAttendanceNotFoundException::new);

      //then
      assertThat(afterAttendance.getSeminarAttendanceStatusEntity().getType())
          .isEqualTo(ATTENDANCE.getType());
      assertThat(afterAttendance.getSeminarAttendTime()).isEqualTo(seminarInitTime);
    }

    @Test
    @DisplayName("[SUCCESS] 개인사유 출석 수정")
    void updateSeminarAttendanceTest() {
      // given
      SeminarEntity seminar = seminarRepository.getById(1L);
      MemberEntity member = memberRepository.getById(1L);
      SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.getById(
          ATTENDANCE.getId());
      SeminarAttendanceEntity seminarAttendance = adminSeminarService.generateSeminarAttendance(
          member,
          seminar, attendance);

      em.flush();
      em.clear();

      SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
          .seminarAttendanceStatusId(4L)
          .absenceExcuse("예비군 훈련")
          .build();

      // when
      adminSeminarService.updateSeminarAttendanceStatus(seminarAttendance.getId(), request);
      SeminarAttendanceEntity find = seminarAttendanceRepository.findById(seminarAttendance.getId())
          .orElseThrow(CustomSeminarAttendanceNotFoundException::new);

      //then
      assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(PERSONAL.getType());
      assertThat(find.getSeminarAttendanceExcuseEntity().getAbsenceExcuse())
          .isEqualTo("예비군 훈련");
    }

    @Test
    @DisplayName("[SUCCESS] 결석으로 수정시 벌점 내역 추가")
    void updateAbsenceTest() {
      // given
      SeminarEntity seminar = seminarRepository.getById(1L);
      MemberEntity member = memberRepository.getById(1L);
      SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.getById(
          ATTENDANCE.getId());
      SeminarAttendanceEntity seminarAttendance = adminSeminarService.generateSeminarAttendance(
          member, seminar, attendance);
      MeritTypeEntity absenceMeritType = getMeritType(ABSENCE);
      Integer beforeDemerit = member.getDemerit();
      SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
          .seminarAttendanceStatusId(ABSENCE.getId())
          .build();
      em.flush();
      em.clear();

      // when
      adminSeminarService.updateSeminarAttendanceStatus(seminarAttendance.getId(), request);
      SeminarAttendanceEntity find = seminarAttendanceRepository.findById(seminarAttendance.getId())
          .orElseThrow(CustomSeminarAttendanceNotFoundException::new);
      Integer afterDemerit = find.getMemberEntity().getDemerit();
      MeritLogEntity meritLog = getMeritLog(member, absenceMeritType);

      //then
      assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(ABSENCE.getType());
      assertThat(beforeDemerit + ABSENCE_DEMERIT).isEqualTo(afterDemerit);
      assertThat(meritLogRepository.existsById(meritLog.getId())).isTrue();
    }

    private MeritLogEntity getMeritLog(MemberEntity member, MeritTypeEntity meritType) {
      List<MeritLogEntity> meritLogs = meritLogRepository.findByAwarderAndMeritTypeAndDate(
          member, meritType, LocalDate.now());
      if (meritLogs.isEmpty()) {
        throw new IllegalStateException("찾는 상벌점 내역이 없습니다.");
      }
      return meritLogs.get(0);
    }

    @Test
    @DisplayName("[SUCCESS] 이전 상태가 결석인 경우 다른 상태로 수정시 벌점 내역 제거")
    void updateStatusWhenBeforeStateIsAbsence() {
      // given
      SeminarEntity seminar = seminarRepository.getById(1L);
      MemberEntity member = memberRepository.getById(1L);
      SeminarAttendanceStatusEntity absence = seminarAttendanceStatusRepository.getById(
          ABSENCE.getId());
      SeminarAttendanceEntity seminarAttendance = adminSeminarService.generateSeminarAttendance(
          member,
          seminar, absence);
      MeritTypeEntity meritTypeOfAbsence = meritTypeRepository.findByDetail(ABSENCE.getType())
          .orElseThrow(CustomMeritTypeNotFoundException::new);
      MeritLogEntity meritLog = generateMeritLog(member, meritTypeOfAbsence);

      em.flush();
      em.clear();

      Integer beforeDemerit = memberRepository.getById(1L).getDemerit();
      SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
          .seminarAttendanceStatusId(ATTENDANCE.getId())
          .build();

      // when
      adminSeminarService.updateSeminarAttendanceStatus(seminarAttendance.getId(), request);
      SeminarAttendanceEntity find = seminarAttendanceRepository.findById(seminarAttendance.getId())
          .orElseThrow(CustomSeminarAttendanceNotFoundException::new);
      Integer afterDemerit = find.getMemberEntity().getDemerit();

      //then
      assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(ATTENDANCE.getType());
      assertThat(beforeDemerit - meritTypeOfAbsence.getMerit()).isEqualTo(afterDemerit);
      assertThat(meritLogRepository.existsById(meritLog.getId())).isFalse();
    }

    @Test
    @DisplayName("[SUCCESS] 이전 상태가 결석이고 지각으로 수정 - 수정 후 짝수번 지각인 경우 결석처리")
    void updateStatusToEvenNumberedLatenessWhenBeforeStateIsAbsence() {
      // given
      SeminarEntity seminar1 = seminarRepository.getById(1L);
      SeminarEntity seminar2 = adminSeminarService.generateSeminar(LocalDateTime.now());
      MemberEntity member = memberRepository.getById(1L);
      SeminarAttendanceStatusEntity lateness = seminarAttendanceStatusRepository.getById(
          LATENESS.getId());
      SeminarAttendanceStatusEntity absence = seminarAttendanceStatusRepository.getById(
          ABSENCE.getId());
      adminSeminarService.generateSeminarAttendance(member, seminar1, lateness);
      SeminarAttendanceEntity seminar2Attendance = adminSeminarService.generateSeminarAttendance(
          member,
          seminar2, absence);
      MeritTypeEntity meritTypeOfAbsence = meritTypeRepository.findByDetail(ABSENCE.getType())
          .orElseThrow(CustomMeritTypeNotFoundException::new);
      MeritLogEntity beforeMeritLog = generateMeritLog(member, meritTypeOfAbsence);

      em.flush();
      em.clear();

      Integer beforeDemerit = memberRepository.getById(1L).getDemerit();
      SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
          .seminarAttendanceStatusId(LATENESS.getId())
          .build();

      // when
      adminSeminarService.updateSeminarAttendanceStatus(seminar2Attendance.getId(), request);
      SeminarAttendanceEntity find = seminarAttendanceRepository.findById(
              seminar2Attendance.getId())
          .orElseThrow(CustomSeminarAttendanceNotFoundException::new);
      Integer afterDemerit = find.getMemberEntity().getDemerit();
      MeritLogEntity afterMeritLog = getMeritLog(member, meritTypeOfAbsence);

      //then
      assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(LATENESS.getType());
      assertThat(beforeDemerit).isEqualTo(afterDemerit);
      assertThat(meritLogRepository.existsById(beforeMeritLog.getId())).isFalse();
      assertThat(meritLogRepository.existsById(afterMeritLog.getId())).isTrue();
    }

    @Test
    @DisplayName("[SUCCESS] 지각으로 변경 시 이전 세미나에서 지각을 홀수번 한 적이 있으면 결석처리")
    void updateStatusToLatenessIfEverBeenLate() {
      // given
      SeminarEntity seminar1 = seminarRepository.getById(1L);
      SeminarEntity seminar2 = adminSeminarService.generateSeminar(LocalDateTime.now());
      MemberEntity member = memberRepository.getById(1L);
      SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.getById(
          ATTENDANCE.getId());
      SeminarAttendanceStatusEntity lateness = seminarAttendanceStatusRepository.getById(
          LATENESS.getId());

      adminSeminarService.generateSeminarAttendance(member, seminar1, lateness);
      SeminarAttendanceEntity seminar2Attendance = adminSeminarService.generateSeminarAttendance(
          member, seminar2, attendance);

      em.flush();
      em.clear();

      Integer beforeDemerit = memberRepository.getById(1L).getDemerit();
      SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
          .seminarAttendanceStatusId(2L)
          .build();

      // when
      adminSeminarService.updateSeminarAttendanceStatus(seminar2Attendance.getId(), request);
      SeminarAttendanceEntity find = seminarAttendanceRepository.findById(
              seminar2Attendance.getId())
          .orElseThrow(CustomSeminarAttendanceNotFoundException::new);
      Integer afterDemerit = find.getMemberEntity().getDemerit();
      MeritTypeEntity meritTypeOfAbsence = meritTypeRepository.findByDetail(ABSENCE.getType())
          .orElseThrow(CustomMeritTypeNotFoundException::new);
      MeritLogEntity meritLog = getMeritLog(member, meritTypeOfAbsence);

      //then
      assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(LATENESS.getType());
      assertThat(beforeDemerit + meritTypeOfAbsence.getMerit()).isEqualTo(afterDemerit);
      assertThat(meritLogRepository.existsById(meritLog.getId())).isTrue();
    }

    @Test
    @DisplayName("[SUCCESS] 지각을 홀수번 한 상태에서 결석으로 수정")
    void updateStatusOddNumberedLatenessToAbsence() {
      // given
      SeminarEntity seminar = seminarRepository.getById(1L);
      MemberEntity member = memberRepository.getById(1L);
      SeminarAttendanceStatusEntity lateness = seminarAttendanceStatusRepository.getById(
          LATENESS.getId());

      SeminarAttendanceEntity seminarAttendanceEntity = adminSeminarService.generateSeminarAttendance(
          member, seminar, lateness);

      MeritTypeEntity meritTypeOfAbsence = meritTypeRepository.findByDetail(ABSENCE.getType())
          .orElseThrow(CustomMeritTypeNotFoundException::new);

      em.flush();
      em.clear();

      Integer beforeDemerit = memberRepository.getById(1L).getDemerit();
      SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
          .seminarAttendanceStatusId(ABSENCE.getId())
          .build();

      // when
      adminSeminarService.updateSeminarAttendanceStatus(seminarAttendanceEntity.getId(), request);
      SeminarAttendanceEntity find = seminarAttendanceRepository.findById(
              seminarAttendanceEntity.getId())
          .orElseThrow(CustomSeminarAttendanceNotFoundException::new);
      Integer afterDemerit = find.getMemberEntity().getDemerit();
      MeritLogEntity afterMeritLog = getMeritLog(member, meritTypeOfAbsence);

      //then
      assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(ABSENCE.getType());
      assertThat(beforeDemerit + meritTypeOfAbsence.getMerit()).isEqualTo(afterDemerit);
      assertThat(meritLogRepository.existsById(afterMeritLog.getId())).isTrue();
    }

    @Test
    @DisplayName("[SUCCESS] 지각을 짝수번 한 상태에서 결석으로 수정시 벌점 내역 갱신")
    void updateStatusEvenNumberedLatenessToAbsence() {
      // given
      SeminarEntity seminar1 = seminarRepository.getById(1L);
      SeminarEntity seminar2 = adminSeminarService.generateSeminar(LocalDateTime.now());
      MemberEntity member = memberRepository.getById(1L);
      SeminarAttendanceStatusEntity lateness = seminarAttendanceStatusRepository.getById(
          LATENESS.getId());

      adminSeminarService.generateSeminarAttendance(member, seminar1, lateness);
      SeminarAttendanceEntity seminar2Attendance = adminSeminarService.generateSeminarAttendance(
          member, seminar2, lateness);
      MeritTypeEntity meritTypeOfAbsence = meritTypeRepository.findByDetail(ABSENCE.getType())
          .orElseThrow(CustomMeritTypeNotFoundException::new);
      MeritLogEntity beforeMeritLog = generateMeritLog(member, meritTypeOfAbsence);

      em.flush();
      em.clear();

      Integer beforeDemerit = memberRepository.getById(1L).getDemerit();
      SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
          .seminarAttendanceStatusId(ABSENCE.getId())
          .build();

      // when
      adminSeminarService.updateSeminarAttendanceStatus(seminar2Attendance.getId(), request);
      SeminarAttendanceEntity find = seminarAttendanceRepository.findById(
              seminar2Attendance.getId())
          .orElseThrow(CustomSeminarAttendanceNotFoundException::new);
      Integer afterDemerit = find.getMemberEntity().getDemerit();
      MeritLogEntity afterMeritLog = getMeritLog(member, meritTypeOfAbsence);

      //then
      assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(ABSENCE.getType());
      assertThat(beforeDemerit).isEqualTo(afterDemerit);
      assertThat(meritLogRepository.existsById(beforeMeritLog.getId())).isFalse();
      assertThat(meritLogRepository.existsById(afterMeritLog.getId())).isTrue();
    }
  }

  @Nested
  class FailTest {

    @Test
    @DisplayName("[FAIL] 중복된 날짜(이름) 세미나 생성")
    void createDuplicateSeminarTest() {
      seminarRepository.save(SeminarEntity.builder()
          .openTime(LocalDateTime.now())
          .build());
      SeminarCreateRequestDto request = SeminarCreateRequestDto.builder()
          .openTime(LocalDateTime.now())
          .build();
      em.flush();
      em.clear();

      Assertions.assertThrows(CustomDuplicateSeminarException.class,
          () -> adminSeminarService.createSeminar(request));
    }

    @Test
    @DisplayName("[FAIL] 개인 사유 걸석시 결석 사유가 없으면 예외가 발생한다.")
    void notExistExcuseWhenPersonalAbsence() {
      // given
      SeminarAttendanceEntity attendance = new SeminarAttendanceEntity();

      // then
      assertThatThrownBy(() -> adminSeminarService.processPersonal(attendance, null))
          .isInstanceOf(CustomAttendanceAbsenceExcuseIsNullException.class);
    }
  }


  SeminarAttendanceStatusEntity getSeminarAttendanceStatus(SeminarAttendanceStatus status) {
    return seminarAttendanceStatusRepository.getById(status.getId());
  }

  MeritTypeEntity getMeritType(SeminarAttendanceStatus status) {
    return meritTypeRepository.findByDetail(
        status.getType()).orElseThrow(CustomMeritTypeNotFoundException::new);
  }
}

