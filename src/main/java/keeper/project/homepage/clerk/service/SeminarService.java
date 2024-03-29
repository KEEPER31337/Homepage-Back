package keeper.project.homepage.clerk.service;

import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.BEFORE_ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.LATENESS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.clerk.entity.SeminarAttendanceEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity;
import keeper.project.homepage.clerk.entity.SeminarEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.clerk.exception.CustomSeminarAttendanceFailException;
import keeper.project.homepage.clerk.exception.CustomSeminarAttendanceNotFoundException;
import keeper.project.homepage.clerk.exception.CustomSeminarAttendanceStatusNotFoundException;
import keeper.project.homepage.clerk.exception.CustomSeminarNotFoundException;
import keeper.project.homepage.clerk.repository.SeminarAttendanceRepository;
import keeper.project.homepage.clerk.repository.SeminarAttendanceStatusRepository;
import keeper.project.homepage.clerk.repository.SeminarRepository;
import keeper.project.homepage.clerk.dto.request.AttendanceCheckRequestDto;
import keeper.project.homepage.clerk.dto.response.AttendanceCheckResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarOngoingAttendanceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeminarService {

  private final SeminarRepository seminarRepository;
  private final SeminarAttendanceRepository seminarAttendanceRepository;
  private final SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;

  private final AuthService authService;
  private final AdminSeminarService adminSeminarService;

  public SeminarOngoingAttendanceResponseDto findSeminarOngoingAttendance(LocalDate searchDate) {
    String seminarName = searchDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return seminarRepository.findSeminarOngoingAttendance(seminarName, LocalDateTime.now())
        .map(SeminarOngoingAttendanceResponseDto::from)
        .orElse(SeminarOngoingAttendanceResponseDto.NONE);
  }

  @Transactional
  public AttendanceCheckResponseDto checkSeminarAttendance(AttendanceCheckRequestDto request) {
    LocalDateTime attendanceTime = LocalDateTime.now();
    SeminarEntity seminar = seminarRepository.findById(request.getSeminarId())
        .orElseThrow(CustomSeminarNotFoundException::new);
    MemberEntity member = authService.getMemberEntityWithJWT();
    SeminarAttendanceEntity seminarAttendance = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow(CustomSeminarAttendanceNotFoundException::new);

    if (!isPossibleAttendance(seminar, seminarAttendance, attendanceTime)) {
      return AttendanceCheckResponseDto.IMPOSSIBLE;
    }

    SeminarAttendanceStatusEntity status = attendanceProcess(seminar, request.getAttendanceCode(),
        attendanceTime);
    adminSeminarService.processAttendance(seminarAttendance, status, "");
    return AttendanceCheckResponseDto.from(status);
  }

  private boolean isPossibleAttendance(SeminarEntity seminar,
      SeminarAttendanceEntity seminarAttendance, LocalDateTime attendanceTime) {
    if (seminar.getAttendanceCode() == null) {
      return false;
    }

    String status = seminarAttendance.getSeminarAttendanceStatusEntity().getType();
    return status.equals(BEFORE_ATTENDANCE.getType()) && attendanceTime.isBefore(seminar.getLatenessCloseTime());
  }

  private SeminarAttendanceStatusEntity attendanceProcess(SeminarEntity seminar,
      String userAttendanceCode, LocalDateTime attendanceTime) {
    if (!userAttendanceCode.equals(seminar.getAttendanceCode())) {
      throw new CustomSeminarAttendanceFailException("출석 코드가 일치하지 않습니다.");
    }

    if (attendanceTime.isBefore(seminar.getAttendanceCloseTime())) {
      return seminarAttendanceStatusRepository.findById(ATTENDANCE.getId())
          .orElseThrow(CustomSeminarAttendanceStatusNotFoundException::new);
    } else if (attendanceTime.isBefore(seminar.getLatenessCloseTime())) {
      return seminarAttendanceStatusRepository.findById(LATENESS.getId())
          .orElseThrow(CustomSeminarAttendanceStatusNotFoundException::new);
    } else {
      throw new CustomSeminarAttendanceFailException();
    }
  }

}
