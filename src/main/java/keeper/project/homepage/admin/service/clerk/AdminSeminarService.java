package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity.*;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.PERSONAL;

import java.util.List;
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceStatusResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceUpdateResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarResponseDto;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomAttendanceAbsenceExcuseIsNullException;
import keeper.project.homepage.exception.clerk.CustomSeminarAttendanceNotFoundException;
import keeper.project.homepage.exception.clerk.CustomSeminarNotFoundException;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceStatusRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import keeper.project.homepage.user.service.member.MemberUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSeminarService {

  static final Integer ABSENCE_DEMERIT = 3;
  private final SeminarRepository seminarRepository;
  private final SeminarAttendanceRepository seminarAttendanceRepository;
  private final SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;
  private final MemberUtilService memberUtilService;

  public List<SeminarResponseDto> getSeminars() {
    return seminarRepository.findAllByOrderByOpenTimeDesc()
        .stream()
        .map(SeminarResponseDto::toDto)
        .toList();
  }

  @Transactional
  public Page<SeminarAttendanceResponseDto> getSeminarAttendances(Pageable pageable) {
    return seminarRepository.findAll(pageable).map(SeminarAttendanceResponseDto::toDto);
  }

  @Transactional
  public SeminarAttendanceUpdateResponseDto updateSeminarAttendanceStatus(Long seminarId,
      Long memberId,
      SeminarAttendanceUpdateRequestDto requestDto) {
    SeminarAttendanceEntity seminarAttendance = getSeminarAttendanceBySeminarIdAndMemberId(
        seminarId, memberId);

    processAttendance(seminarAttendance, requestDto);

    return SeminarAttendanceUpdateResponseDto.toDto(seminarAttendance);
  }

  public List<SeminarAttendanceStatusResponseDto> getSeminarAttendanceStatuses() {
    return seminarAttendanceStatusRepository.findAll()
        .stream()
        .map(SeminarAttendanceStatusResponseDto::toDto).toList();
  }

  private void processAttendance(SeminarAttendanceEntity attendance,
      SeminarAttendanceUpdateRequestDto requestDto) {
    MemberEntity member = attendance.getMemberEntity();
    SeminarAttendanceStatusEntity afterStatus = seminarAttendanceStatusRepository.getById(
        requestDto.getSeminarAttendanceStatusId());
    SeminarAttendanceStatusEntity beforeStatus = attendance.getSeminarAttendanceStatusEntity();

    if (beforeStatus.equals(afterStatus) && !afterStatus.equals(PERSONAL)) {
      return;
    }
    if (beforeStatus.equals(ABSENCE)) {
      member.changeDemerit(member.getDemerit() - ABSENCE_DEMERIT);
    }
    if (afterStatus.equals(PERSONAL)) {
      processPersonal(attendance, requestDto);
    }
    if (afterStatus.equals(LATENESS)) {
      processLateness(member);
    }
    if (afterStatus.equals(ABSENCE)) {
      processAbsence(member);
    }
    attendance.setSeminarAttendanceStatusEntity(afterStatus);
  }

  private static void processLateness(MemberEntity member) {
    long latenessCount = member.getSeminarAttendances().stream()
        .filter(seminarAttendance ->
            seminarAttendance.getSeminarAttendanceStatusEntity().getType()
                .equals(LATENESS.getType())
        ).count();
    // 지각 2회는 결석 처리
    if (latenessCount % 2 == 1) {
      processAbsence(member);
    }
  }

  private static void processAbsence(MemberEntity member) {
    member.changeDemerit(member.getDemerit() + ABSENCE_DEMERIT);
  }

  private static void processPersonal(SeminarAttendanceEntity attendance,
      SeminarAttendanceUpdateRequestDto request) {
    if (request.getAbsenceExcuse() == null) {
      throw new CustomAttendanceAbsenceExcuseIsNullException();
    }
    attendance.setSeminarAttendanceExcuseEntity(
        createSeminarAttendanceExcuse(attendance, request.getAbsenceExcuse()));
  }

  private SeminarAttendanceEntity getSeminarAttendanceBySeminarIdAndMemberId(Long seminarId,
      Long memberId) {
    MemberEntity member = memberUtilService.getById(memberId);
    SeminarEntity seminar = seminarRepository.findById(seminarId).orElseThrow(
        CustomSeminarNotFoundException::new);
    return seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(seminar, member)
        .orElseThrow(CustomSeminarAttendanceNotFoundException::new);
  }
}
