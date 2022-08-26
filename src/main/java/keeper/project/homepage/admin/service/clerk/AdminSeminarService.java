package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity.*;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.PERSONAL;
import static keeper.project.homepage.entity.member.MemberTypeEntity.REGULAR_MEMBER_ID;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.SeminarCreateRequestDto;
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
        .map(SeminarResponseDto::toDto)
        .toList();
  }

  public Page<SeminarAttendanceResponseDto> getSeminarAttendances(Pageable pageable) {
    return seminarRepository.findAll(pageable).map(SeminarAttendanceResponseDto::toDto);
  }

  // TODO: seminarId, memberId -> attendanceId로 변경
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

  // TODO: 벌점 log 기록하도록 수정, requestDto -> absenceExcuse 수정
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
        .orElseThrow(CustomSeminarAttendanceNotFoundE ception::new);
  }

  @Transactional
  public SeminarCreateResponseDto createSeminar(SeminarCreateRequestDto request) {
    SeminarEntity seminar = generateSeminar(request.getOpenTime());
    List<MemberEntity> allRegularMembers = memberRepository.findAllByMemberTypeOrderByGenerationAsc(
        memberUtilService.getTypeById(REGULAR_MEMBER_ID));
    SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.findById(
        ATTENDANCE.getId()).orElseThrow(CustomSeminarAttendanceStatusNotFoundException::new);

    for (MemberEntity member : allRegularMembers) {
      generateSeminarAttendance(member, seminar, attendance);
    }

    return SeminarCreateResponseDto.toDto(seminar);
  }

  SeminarEntity generateSeminar(LocalDateTime openTime) {
    return seminarRepository.save(SeminarEntity.builder()
        .name(null)
        .openTime(openTime)
        .build()
    );
  }

  SeminarAttendanceEntity generateSeminarAttendance(MemberEntity member, SeminarEntity seminar, SeminarAttendanceStatusEntity status) {
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
