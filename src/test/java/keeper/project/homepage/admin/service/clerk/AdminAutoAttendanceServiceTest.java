package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.controller.clerk.AutoAttendanceSpringTestHelper;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AdminAutoAttendanceServiceTest extends AutoAttendanceSpringTestHelper {

  private MemberEntity user;
  private MemberEntity admin;

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
  }

  @Test
  @DisplayName("가장 최근 세미나 조회")
  public void getLatestSeminar() throws Exception {
    //given
    SeminarEntity seminar1 = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .build());

    SeminarEntity seminar2 = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 2주차 세미나")
            .openTime(LocalDateTime.now().plusDays(7))
            .build());

    //when
    SeminarEntity findLatestSeminar = seminarRepository.findTop1ByOrderByIdDesc().orElseThrow();

    //then
    assertThat(findLatestSeminar.getId()).isEqualTo(seminar2.getId());
    assertThat(findLatestSeminar.getName()).isEqualTo(seminar2.getName());
  }

  @Test
  @DisplayName("세미나 출석 조건 조회 - 동시에 회장 출석 처리")
  public void getAttendanceConditions() throws Exception {
    //given
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .attendanceCode("357")
            .attendanceCloseTime(LocalDateTime.now().plusMinutes(5))
            .latenessCloseTime(LocalDateTime.now().plusMinutes(10))
            .openTime(LocalDateTime.now())
            .build());

    generateSeminarAttendance(seminar, admin); // 출석 전으로 등록된 상태

    processingAttendance(admin, seminar); // 회장 출석 처리

    //when
    SeminarEntity findSeminar = seminarRepository.getById(seminar.getId());
    SeminarAttendanceEntity seminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        findSeminar, admin).orElseThrow();

    //then
    assertThat(findSeminar.getAttendanceCode()).isEqualTo("357");
    assertThat(seminarAttendanceEntity.getSeminarAttendanceStatusEntity()).isEqualTo(
        seminarAttendanceStatusRepository.getById(ATTENDANCE.getId()));
  }

  @Test
  @DisplayName("세미나 출석 종료시 - 무응답 회원 결석 처리")
  public void attendanceCheckEnd() throws Exception {
    //given
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .build());

    generateSeminarAttendance(seminar, admin); // 출석 전으로 등록된 상태
    generateSeminarAttendance(seminar, user); // 출석 전으로 등록된 상태

    List<SeminarAttendanceEntity> seminarAttendanceEntities = seminarAttendanceRepository
        .findAllBySeminarAttendanceStatusEntity(
            seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId())).orElseThrow();

    processingAbsence(seminarAttendanceEntities); // 무응답 회원들 결석 처리

    //when
    SeminarEntity findSeminar = seminarRepository.getById(seminar.getId());
    SeminarAttendanceEntity seminarAttendanceEntity1 = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        findSeminar, admin).orElseThrow();
    SeminarAttendanceEntity seminarAttendanceEntity2 = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        findSeminar, user).orElseThrow();

    //then
    assertThat(seminarAttendanceEntity1.getSeminarAttendanceStatusEntity()).isEqualTo(
        seminarAttendanceStatusRepository.getById(
            ABSENCE.getId()));
    assertThat(seminarAttendanceEntity2.getSeminarAttendanceStatusEntity()).isEqualTo(
        seminarAttendanceStatusRepository.getById(
            ABSENCE.getId()));
    assertThat(seminarAttendanceEntities.size()).isEqualTo(2);
    assertThat(seminarAttendanceEntity1.getMemberEntity().getDemerit()).isEqualTo(3);
    assertThat(seminarAttendanceEntity2.getMemberEntity().getDemerit()).isEqualTo(3);
  }
}
