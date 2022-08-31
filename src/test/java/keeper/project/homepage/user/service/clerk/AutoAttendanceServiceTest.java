package keeper.project.homepage.user.service.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.LATENESS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import keeper.project.homepage.controller.clerk.AutoAttendanceSpringTestHelper;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AutoAttendanceServiceTest extends AutoAttendanceSpringTestHelper {

  private MemberEntity user;
  private MemberEntity admin;

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
  }

  @Test
  @DisplayName("가장 최근 세미나 ID 조회")
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
  }

  @Test
  @DisplayName("틀린 출석 코드 입력")
  public void inputInCorrectCode() throws Exception {
    //given
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .attendanceCode("379")
            .attendanceCloseTime(LocalDateTime.now().plusMinutes(5))
            .latenessCloseTime(LocalDateTime.now().plusMinutes(10))
            .build());

    generateSeminarAttendance(seminar, user); // 출석 전으로 등록된 상태
    String attendanceCode = "123";
    SeminarAttendanceEntity seminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, user).orElseThrow();

    //when
    SeminarEntity findSeminar = seminarRepository.getById(seminar.getId());

    //then
    assertThat(findSeminar.getAttendanceCode()).isNotEqualTo(attendanceCode);
    assertThat(seminarAttendanceEntity.getSeminarAttendanceStatusEntity()).isEqualTo(
        seminarAttendanceStatusRepository.getById(
            BEFORE_ATTENDANCE.getId()));
  }

  @Test
  @DisplayName("출석 체크 - 출석")
  public void attendance() throws Exception {
    //given
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .attendanceCloseTime(LocalDateTime.now().plusMinutes(5))
            .latenessCloseTime(LocalDateTime.now().plusMinutes(10))
            .build());

    SeminarAttendanceEntity seminarAttendanceEntity = generateSeminarAttendanceWithAttendTime(
        seminar, user, 2); // 출석 전으로 등록된 상태
    int demerit = judgeAttendanceStatus(seminar, seminarAttendanceEntity,
        seminarAttendanceEntity.getSeminarAttendTime());
    user.getSeminarAttendances().add(seminarAttendanceEntity);

    //when
    SeminarAttendanceEntity findSeminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, user).orElseThrow();

    //then
    assertThat(findSeminarAttendanceEntity.getSeminarAttendanceStatusEntity()).isEqualTo(
        seminarAttendanceStatusRepository.getById(
            ATTENDANCE.getId()));
    assertThat(demerit).isEqualTo(0);

  }

  @Test
  @DisplayName("출석 체크 - 지각 1회")
  public void oneLateness() throws Exception {
    //given
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .attendanceCloseTime(LocalDateTime.now().plusMinutes(5))
            .latenessCloseTime(LocalDateTime.now().plusMinutes(10))
            .build());

    SeminarAttendanceEntity seminarAttendanceEntity = generateSeminarAttendanceWithAttendTime(
        seminar, user, 7); // 출석 전으로 등록된 상태
    int demerit = judgeAttendanceStatus(seminar, seminarAttendanceEntity,
        seminarAttendanceEntity.getSeminarAttendTime());
    user.getSeminarAttendances().add(seminarAttendanceEntity);

    //when
    SeminarAttendanceEntity findSeminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, user).orElseThrow();

    //then
    assertThat(findSeminarAttendanceEntity.getSeminarAttendanceStatusEntity()).isEqualTo(
        seminarAttendanceStatusRepository.getById(
            LATENESS.getId()));
    assertThat(demerit).isEqualTo(0);
  }

  @Test
  @DisplayName("출석 체크 - 지각 2회")
  public void twoLateness() throws Exception {
    //given
    SeminarEntity seminar1 = seminarRepository.save(
        SeminarEntity.builder()
            .name("8월 4주차 세미나")
            .openTime(LocalDateTime.now())
            .attendanceCloseTime(LocalDateTime.now().plusMinutes(5))
            .latenessCloseTime(LocalDateTime.now().plusMinutes(10))
            .build());

    SeminarAttendanceEntity seminarAttendanceEntity1 = generateSeminarAttendanceWithAttendTime(
        seminar1, user, 7); // 출석 전으로 등록된 상태
    judgeAttendanceStatus(seminar1, seminarAttendanceEntity1,
        seminarAttendanceEntity1.getSeminarAttendTime());
    user.getSeminarAttendances().add(seminarAttendanceEntity1);

    SeminarEntity seminar2 = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .attendanceCloseTime(LocalDateTime.now().plusMinutes(5))
            .latenessCloseTime(LocalDateTime.now().plusMinutes(10))
            .build());

    SeminarAttendanceEntity seminarAttendanceEntity2 = generateSeminarAttendanceWithAttendTime(
        seminar2, user, 7); // 출석 전으로 등록된 상태
    int demerit = judgeAttendanceStatus(seminar2, seminarAttendanceEntity2,
        seminarAttendanceEntity2.getSeminarAttendTime());
    user.getSeminarAttendances().add(seminarAttendanceEntity2);

    //when
    SeminarAttendanceEntity findSeminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar2, user).orElseThrow();

    //then
    assertThat(findSeminarAttendanceEntity.getSeminarAttendanceStatusEntity()).isEqualTo(
        seminarAttendanceStatusRepository.getById(
            LATENESS.getId()));
    assertThat(demerit).isEqualTo(3);
  }

  @Test
  @DisplayName("출석 체크 - 결석")
  public void Absence() throws Exception {
    //given
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .attendanceCloseTime(LocalDateTime.now().plusMinutes(5))
            .latenessCloseTime(LocalDateTime.now().plusMinutes(10))
            .build());

    SeminarAttendanceEntity seminarAttendanceEntity = generateSeminarAttendanceWithAttendTime(
        seminar, user, 15); // 출석 전으로 등록된 상태
    int demerit = judgeAttendanceStatus(seminar, seminarAttendanceEntity,
        seminarAttendanceEntity.getSeminarAttendTime());
    user.getSeminarAttendances().add(seminarAttendanceEntity);

    //when
    SeminarAttendanceEntity findSeminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, user).orElseThrow();

    //then
    assertThat(findSeminarAttendanceEntity.getSeminarAttendanceStatusEntity()).isEqualTo(
        seminarAttendanceStatusRepository.getById(
            ABSENCE.getId()));
    assertThat(demerit).isEqualTo(3);
  }
}
