package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity.*;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.PERSONAL;
import static keeper.project.homepage.entity.member.MemberTypeEntity.memberType.REGULAR_MEMBER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.admin.dto.clerk.request.MeritAddRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.SeminarCreateRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.SeminarWithAttendancesRequestByPeriodDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarWithAttendancesResponseByPeriodDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceStatusResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarAttendanceUpdateResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarCreateResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarResponseDto;
import keeper.project.homepage.entity.clerk.MeritTypeEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomAttendanceAbsenceExcuseIsNullException;
import keeper.project.homepage.exception.clerk.CustomDuplicateSeminarException;
import keeper.project.homepage.exception.clerk.CustomMeritTypeNotFoundException;
import keeper.project.homepage.exception.clerk.CustomSeminarAttendanceNotFoundException;
import keeper.project.homepage.exception.clerk.CustomSeminarAttendanceStatusNotFoundException;
import keeper.project.homepage.exception.clerk.CustomSeminarNotFoundException;
import keeper.project.homepage.repository.clerk.MeritTypeRepository;
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

  static final Integer ABSENCE_DEMERIT = 3;

  private final SeminarRepository seminarRepository;
  private final SeminarAttendanceRepository seminarAttendanceRepository;
  private final SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;
  private final MemberUtilService memberUtilService;
  private final MemberRepository memberRepository;
  private final MeritTypeRepository meritTypeRepository;
  private final AdminMeritService meritService;

  public List<SeminarResponseDto> getSeminars() {
    return seminarRepository.findAllByOrderByOpenTimeDesc()
        .stream()
        .map(SeminarResponseDto::from)
        .toList();
  }

  public Page<SeminarWithAttendancesResponseByPeriodDto> getAllSeminarAttendances(Pageable pageable,
      SeminarWithAttendancesRequestByPeriodDto requestDto) {
    return seminarRepository.findAllByOpenTimeBetweenOrderByOpenTimeDesc(pageable,
            requestDto.getSeasonStartDate(), requestDto.getSeasonEndDate())
        .map(SeminarWithAttendancesResponseByPeriodDto::from);
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

  private void processAttendance(SeminarAttendanceEntity attendance,
      SeminarAttendanceStatusEntity afterStatusEntity, String absenceExcuse) {
    MemberEntity member = attendance.getMemberEntity();
    String beforeStatus = attendance.getSeminarAttendanceStatusEntity().getType();
    String afterStatus = afterStatusEntity.getType();
    LocalDate attendDate = attendance.getSeminarAttendTime().toLocalDate();

    if (beforeStatus.equals(afterStatus) && !afterStatus.equals(PERSONAL.getType())) {
      return;
    }
    // 이전 출석에 결석 처리를 했으면 해당 결석 처리 내역 삭제
    if (beforeStatus.equals(ABSENCE.getType()) || (beforeStatus.equals(LATENESS.getType()) &&
        getLatenessCount(member) % 2 == 0)) {
      deleteBeforeAbsence(attendance, member);
    }
    if (afterStatus.equals(PERSONAL.getType())) {
      processPersonal(attendance, absenceExcuse);
    }
    if (afterStatus.equals(LATENESS.getType())) {
      processLateness(member, attendDate);
    }
    if (afterStatus.equals(ABSENCE.getType())) {
      processAbsence(member, attendDate);
    }
    attendance.setSeminarAttendanceStatusEntity(afterStatusEntity);
  }

  private void deleteBeforeAbsence(SeminarAttendanceEntity attendance, MemberEntity member) {
    LocalDate attendDate = attendance.getSeminarAttendTime().toLocalDate();
    meritService.deleteAbsenceLog(member, attendDate);
  }

  private void processLateness(MemberEntity member, LocalDate attendDate) {
    // 지각 2회는 결석 처리
    if (getLatenessCount(member) % 2 == 1) {
      processAbsence(member, attendDate);
    }
  }

  private void processAbsence(MemberEntity member, LocalDate attendDate) {
    MeritTypeEntity absence = meritTypeRepository.findByDetail(ABSENCE.getType())
        .orElseThrow(CustomMeritTypeNotFoundException::new);
    MeritAddRequestDto meritLogCreateRequestDto = generateMeritAddRequestDto(
        member, absence, attendDate);
    meritService.addMeritWithLog(meritLogCreateRequestDto);
  }

  private static MeritAddRequestDto generateMeritAddRequestDto(MemberEntity member,
      MeritTypeEntity type, LocalDate date) {
    return MeritAddRequestDto.builder()
        .date(date)
        .memberId(member.getId())
        .meritTypeId(type.getId())
        .build();
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
    checkDuplicateSeminar(request.getOpenTime());
    SeminarEntity seminar = generateSeminar(request.getOpenTime());
    List<MemberEntity> allRegularMembers = memberRepository.findAllByMemberTypeOrderByGenerationAsc(
        memberUtilService.getTypeById(REGULAR_MEMBER.getId()));
    SeminarAttendanceStatusEntity beforeAttendance = seminarAttendanceStatusRepository.findById(
            BEFORE_ATTENDANCE.getId())
        .orElseThrow(CustomSeminarAttendanceStatusNotFoundException::new);

    for (MemberEntity member : allRegularMembers) {
      generateSeminarAttendance(member, seminar, beforeAttendance);
    }

    return SeminarCreateResponseDto.from(seminar);
  }

  private void checkDuplicateSeminar(LocalDateTime openTime) {
    String seminarName = openTime.toLocalDate()
        .toString()
        .replaceAll("-", "");
    if (seminarRepository.existsByName(seminarName)) {
      throw new CustomDuplicateSeminarException();
    }
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

  public Long deleteSeminar(Long seminarId) {
    SeminarEntity find = seminarRepository.findById(seminarId)
        .orElseThrow(CustomSeminarNotFoundException::new);
    seminarRepository.delete(find);
    return find.getId();
  }
}
