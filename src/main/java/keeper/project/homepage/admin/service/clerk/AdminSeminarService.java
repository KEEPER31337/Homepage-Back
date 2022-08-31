package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity.*;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.PERSONAL;
import static keeper.project.homepage.entity.member.MemberTypeEntity.memberType.REGULAR_MEMBER;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.SeminarCreateRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendancesRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.AllSeminarAttendancesResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceStatusResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceUpdateResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarCreateResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarResponseDto;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomAttendanceAbsenceExcuseIsNullException;
import keeper.project.homepage.exception.clerk.CustomSeminarAttendanceNotFoundException;
import keeper.project.homepage.exception.clerk.CustomSeminarAttendanceStatusNotFoundException;
import keeper.project.homepage.exception.clerk.CustomSeminarNotFoundException;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceStatusRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import keeper.project.homepage.repository.member.MemberRepository;
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

  //TODO: 추후 상벌점 관리 구현되면 수정 예정
  static final Integer ABSENCE_DEMERIT = 3;

  private final SeminarRepository seminarRepository;
  private final SeminarAttendanceRepository seminarAttendanceRepository;
  private final SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;
  private final MemberUtilService memberUtilService;
  private final MemberRepository memberRepository;

  public List<SeminarResponseDto> getSeminars() {
    return seminarRepository.findAllByOrderByOpenTimeDesc()
        .stream()
        .map(SeminarResponseDto::from)
        .toList();
  }

  public Page<AllSeminarAttendancesResponseDto> getAllSeminarAttendances(Pageable pageable,
      SeminarAttendancesRequestDto requestDto) {
    return seminarRepository.findAllByOpenTimeBetweenOrderByOpenTimeDesc(pageable,
            requestDto.getSeasonStartDate(), requestDto.getSeasonEndDate())
        .map(AllSeminarAttendancesResponseDto::from);
  }

  @Transactional
  public SeminarAttendanceUpdateResponseDto updateSeminarAttendanceStatus(Long attendanceId,
      SeminarAttendanceUpdateRequestDto requestDto) {
    SeminarAttendanceEntity seminarAttendance = seminarAttendanceRepository.findById(attendanceId)
        .orElseThrow(CustomSeminarAttendanceNotFoundException::new);
    SeminarAttendanceStatusEntity status = seminarAttendanceStatusRepository.findById(
            requestDto.getSeminarAttendanceStatusId())
        .orElseThrow(CustomSeminarAttendanceStatusNotFoundException::new);
    String absenceExcuse = requestDto.getAbsenceExcuse();

    processAttendance(seminarAttendance, status, absenceExcuse);

    return SeminarAttendanceUpdateResponseDto.from(seminarAttendance);
  }

  public List<SeminarAttendanceStatusResponseDto> getSeminarAttendanceStatuses() {
    return seminarAttendanceStatusRepository.findAll()
        .stream()
        .map(SeminarAttendanceStatusResponseDto::from).toList();
  }

  // TODO: 벌점 log 기록하도록 수정
  private void processAttendance(SeminarAttendanceEntity attendance,
      SeminarAttendanceStatusEntity afterStatusEntity, String absenceExcuse) {
    MemberEntity member = attendance.getMemberEntity();
    String beforeStatus = attendance.getSeminarAttendanceStatusEntity().getType();
    String afterStatus = afterStatusEntity.getType();

    if (beforeStatus.equals(afterStatus) && !afterStatus.equals(PERSONAL.getType())) {
      return;
    }
    if (beforeStatus.equals(ABSENCE.getType())) {
      member.changeDemerit(member.getDemerit() - ABSENCE_DEMERIT);
    }
    if (afterStatus.equals(PERSONAL.getType())) {
      processPersonal(attendance, absenceExcuse);
    }
    if (afterStatus.equals(LATENESS.getType())) {
      processLateness(member);
    }
    if (afterStatus.equals(ABSENCE.getType())) {
      processAbsence(member, beforeStatus);
    }
    attendance.setSeminarAttendanceStatusEntity(afterStatusEntity);
  }

  //TODO: 상벌점 구현 시 중복 로직 수정 필요
  private static void processLateness(MemberEntity member) {
    // 지각 2회는 결석 처리
    if (getLatenessCount(member) % 2 == 1) {
      member.changeDemerit(member.getDemerit() + ABSENCE_DEMERIT);
    }
  }

  //TODO: 상벌점 구현 시 로직 수정 필요
  private static void processAbsence(MemberEntity member,
      String beforeStatus) {
    // 이전 상태가 지각이고 짝수번 지각했으므로 결석 처리된 상태이므로 반환
    if (beforeStatus.equals(LATENESS.getType()) && getLatenessCount(member) % 2 == 0) {
      return;
    }
    member.changeDemerit(member.getDemerit() + ABSENCE_DEMERIT);
  }

  private static long getLatenessCount(MemberEntity member) {
    return member.getSeminarAttendances().stream()
        .filter(seminarAttendance ->
            seminarAttendance.getSeminarAttendanceStatusEntity().getType()
                .equals(LATENESS.getType())
        ).count();
  }

  private static void processPersonal(SeminarAttendanceEntity attendance,
      String absenceExcuse) {
    if (absenceExcuse == null) {
      throw new CustomAttendanceAbsenceExcuseIsNullException();
    }
    attendance.setSeminarAttendanceExcuseEntity(
        newInstance(attendance, absenceExcuse));
  }

  @Transactional
  public SeminarCreateResponseDto createSeminar(SeminarCreateRequestDto request) {
    SeminarEntity seminar = generateSeminar(request.getOpenTime());
    List<MemberEntity> allRegularMembers = memberRepository.findAllByMemberTypeOrderByGenerationAsc(
        memberUtilService.getTypeById(REGULAR_MEMBER.getId()));
    SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.findById(
            ATTENDANCE.getId())
        .orElseThrow(CustomSeminarAttendanceStatusNotFoundException::new);

    //TODO: "출석전" 타입이 추가되면 수정
    for (MemberEntity member : allRegularMembers) {
      generateSeminarAttendance(member, seminar, attendance);
    }

    return SeminarCreateResponseDto.from(seminar);
  }

  public List<SeminarAttendanceResponseDto> getSeminarAttendances(Long seminarId) {
    SeminarEntity seminar = seminarRepository.findById(seminarId)
        .orElseThrow(CustomSeminarNotFoundException::new);

    return seminar.getSeminarAttendances()
        .stream()
        .map(SeminarAttendanceResponseDto::from)
        .toList();
  }

  SeminarEntity generateSeminar(LocalDateTime openTime) {
    return seminarRepository.save(SeminarEntity.builder()
        .name(null)
        .openTime(openTime)
        .build()
    );
  }

  SeminarAttendanceEntity generateSeminarAttendance(MemberEntity member, SeminarEntity seminar,
      SeminarAttendanceStatusEntity status) {
    return seminarAttendanceRepository.save(
        SeminarAttendanceEntity.builder()
            .memberEntity(member)
            .seminarEntity(seminar)
            .seminarAttendanceStatusEntity(status)
            .seminarAttendTime(LocalDateTime.now().withNano(0))
            .build()
    );
  }

}
