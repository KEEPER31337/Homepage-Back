package keeper.project.homepage.user.service.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.LATENESS;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceStatusRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import keeper.project.homepage.user.dto.clerk.request.AttendanceCheckRequestDto;
import keeper.project.homepage.user.dto.clerk.response.AttendanceCheckResponseDto;
import keeper.project.homepage.user.service.member.MemberUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoAttendanceService {

  private static final Integer ABSENCE_DEMERIT = 3;
  private static final Integer NO_DEMERIT = 0;
  private final SeminarRepository seminarRepository;
  private final SeminarAttendanceRepository seminarAttendanceRepository;
  private final SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;
  private final MemberUtilService memberUtilService;

  public Long getLatestSeminarId() {
    SeminarEntity seminar = seminarRepository.findTop1ByOrderByIdDesc().orElseThrow();
    return seminar.getId();
  }

  public AttendanceCheckResponseDto attendanceCheck(
      AttendanceCheckRequestDto attendanceCheckRequestDto) {
    SeminarEntity seminar = seminarRepository.findById(attendanceCheckRequestDto.getSeminarId())
        .orElseThrow();
    MemberEntity member = memberUtilService.getById(attendanceCheckRequestDto.getMemberId());
    String attendanceCode = attendanceCheckRequestDto.getAttendanceCode();
    LocalDateTime attendTime = attendanceCheckRequestDto.getAttendTime();

    SeminarAttendanceEntity seminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow();

    if (!attendanceCode.equals(seminar.getAttendanceCode())) {
      return AttendanceCheckResponseDto.getInstance(AttendanceCheckOption.NOT_CORRECT);
    }

    int demerit = judgeAttendanceStatus(seminar, member, seminarAttendanceEntity, attendTime);
    seminarAttendanceEntity.setSeminarAttendTime(attendTime);
    member.getSeminarAttendances().add(seminarAttendanceEntity);

    return AttendanceCheckResponseDto.from(seminarAttendanceEntity, true, demerit);
  }

  private Integer judgeAttendanceStatus(SeminarEntity seminar, MemberEntity member,
      SeminarAttendanceEntity seminarAttendanceEntity, LocalDateTime attendTime) {

    int demerit = NO_DEMERIT;

    if (seminar.getAttendanceCloseTime().isAfter(attendTime)) { // 출석
      seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
          seminarAttendanceStatusRepository.getById(ATTENDANCE.getId()));
    } else if (seminar.getLatenessCloseTime().isAfter(attendTime)) { // 지각
      // 지각 짝수 회는 결석에 해당하는 벌점 부여
      if (getLatenessCount(member) % 2 == 1) {
        member.changeDemerit(member.getDemerit() + ABSENCE_DEMERIT);
        demerit = ABSENCE_DEMERIT;
      }
      seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
          seminarAttendanceStatusRepository.getById(LATENESS.getId()));
    } else if (seminar.getLatenessCloseTime().isBefore(attendTime)) { // 결석
      seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
          seminarAttendanceStatusRepository.getById(ABSENCE.getId()));

      member.changeDemerit(member.getDemerit() + ABSENCE_DEMERIT);
      demerit = ABSENCE_DEMERIT;
    }
    return demerit;
  }

  private long getLatenessCount(MemberEntity member) {
    return member.getSeminarAttendances().stream()
        .filter(seminarAttendance ->
            seminarAttendance.getSeminarAttendanceStatusEntity().getType()
                .equals(LATENESS.getType())
        ).count();
  }
}
