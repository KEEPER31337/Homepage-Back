package keeper.project.homepage.clerk.service;

import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.seminarAttendanceStatus.LATENESS;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.seminarAttendanceStatus.PERSONAL;
import static keeper.project.homepage.member.entity.MemberTypeEntity.memberType.REGULAR_MEMBER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import keeper.project.homepage.clerk.dto.request.AttendanceStartRequestDto;
import keeper.project.homepage.clerk.dto.request.MeritAddRequestDto;
import keeper.project.homepage.clerk.dto.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.clerk.dto.request.SeminarCreateRequestDto;
import keeper.project.homepage.clerk.dto.response.AttendanceStartResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarAttendanceResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarAttendanceStatusResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarAttendanceUpdateResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarCreateResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarSearchByDateResponseDto;
import keeper.project.homepage.clerk.dto.response.SeminarWithAttendancesResponseByPeriodDto;
import keeper.project.homepage.clerk.entity.MeritTypeEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceExcuseEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity;
import keeper.project.homepage.clerk.entity.SeminarEntity;
import keeper.project.homepage.clerk.exception.CustomAttendanceAbsenceExcuseIsNullException;
import keeper.project.homepage.clerk.exception.CustomDuplicateSeminarException;
import keeper.project.homepage.clerk.exception.CustomMeritTypeNotFoundException;
import keeper.project.homepage.clerk.exception.CustomSeminarAttendanceNotFoundException;
import keeper.project.homepage.clerk.exception.CustomSeminarAttendanceStatusNotFoundException;
import keeper.project.homepage.clerk.exception.CustomSeminarNotFoundException;
import keeper.project.homepage.clerk.repository.MeritTypeRepository;
import keeper.project.homepage.clerk.repository.SeminarAttendanceRepository;
import keeper.project.homepage.clerk.repository.SeminarAttendanceStatusRepository;
import keeper.project.homepage.clerk.repository.SeminarRepository;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.member.service.MemberUtilService;
import keeper.project.homepage.util.service.SchedulerService;
import keeper.project.homepage.util.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSeminarService {

  static final Integer ABSENCE_DEMERIT = 3;
  static final int ATTENDANCE_CODE_LENGTH = 4;

  private final Random random = new Random();
  private final TransactionTemplate transactionTemplate;

  private final AdminMeritService meritService;
  private final MemberUtilService memberUtilService;
  private final AuthService authService;
  private final SchedulerService schedulerService;

  private final MeritTypeRepository meritTypeRepository;
  private final SeminarRepository seminarRepository;
  private final SeminarAttendanceRepository seminarAttendanceRepository;
  private final SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;
  private final MemberRepository memberRepository;

  public List<SeminarResponseDto> getSeminars() {
    return seminarRepository.findAllByOrderByOpenTimeDesc()
        .stream()
        .map(SeminarResponseDto::from)
        .toList();
  }

  public Page<SeminarWithAttendancesResponseByPeriodDto> getSeminarWithAttendancesByPeriod(
      Pageable pageable, LocalDate seasonStartDate, LocalDate seasonEndDate) {
    return seminarRepository.findAllByOpenTimeBetweenOrderByOpenTime(pageable,
            seasonStartDate.atStartOfDay(),
            seasonEndDate.plusDays(1).atStartOfDay())
        .map(SeminarWithAttendancesResponseByPeriodDto::from);
  }

  public List<SeminarAttendanceStatusResponseDto> getSeminarAttendanceStatuses() {
    return seminarAttendanceStatusRepository.findAll()
        .stream()
        .map(SeminarAttendanceStatusResponseDto::from).toList();
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

  @Transactional
  public void processAttendance(SeminarAttendanceEntity attendance,
      SeminarAttendanceStatusEntity afterStatusEntity, String absenceExcuse) {
    MemberEntity member = attendance.getMemberEntity();
    String beforeStatus = attendance.getSeminarAttendanceStatusEntity().getType();
    String afterStatus = afterStatusEntity.getType();
    LocalDate attendDate = attendance.getSeminarAttendTime().toLocalDate();
    validateNoChange(beforeStatus, afterStatus);
    initAttendTime(attendance, beforeStatus);

    // 이전 출석에 결석 처리를 했으면 해당 결석 처리 내역 삭제
    if (beforeStatus.equals(ABSENCE.getType()) || (beforeStatus.equals(LATENESS.getType()) &&
        getLatenessCount(member) % 2 == 0)) {
      deleteBeforeAbsence(attendance, member);
    }
    if (afterStatus.equals(PERSONAL.getType())) {
      processPersonal(attendance, absenceExcuse);
    }
    if (afterStatus.equals(LATENESS.getType())) {
      processLateness(member, attendance);
    }
    if (afterStatus.equals(ABSENCE.getType())) {
      processAbsence(member, attendance);
    }
    attendance.setSeminarAttendanceStatusEntity(afterStatusEntity);
  }

  private static void validateNoChange(String beforeStatus, String afterStatus) {
    if (beforeStatus.equals(afterStatus) && !afterStatus.equals(PERSONAL.getType())) {
      throw new IllegalArgumentException();
    }
  }

  private static void initAttendTime(SeminarAttendanceEntity attendance, String beforeStatus) {
    if (Objects.equals(beforeStatus, BEFORE_ATTENDANCE.getType())) {
      attendance.setSeminarAttendTime(LocalDateTime.now());
    }
  }

  @Transactional
  void deleteBeforeAbsence(SeminarAttendanceEntity attendance, MemberEntity member) {
    LocalDate attendDate = attendance.getSeminarAttendTime().toLocalDate();
    meritService.deleteAbsenceLog(member, attendDate);
  }

  @Transactional
  void processLateness(MemberEntity member, SeminarAttendanceEntity attendance) {
    // 지각 2회는 결석 처리
    if (getLatenessCount(member) % 2 == 1) {
      processAbsence(member, attendance);
    }
  }

  @Transactional
  void processAbsence(MemberEntity member, SeminarAttendanceEntity attendance) {
    MeritTypeEntity absence = meritTypeRepository.findByDetail(ABSENCE.getType())
        .orElseThrow(CustomMeritTypeNotFoundException::new);
    LocalDate attendDate = attendance.getSeminarAttendTime().toLocalDate();
    MeritAddRequestDto meritLogCreateRequestDto = getMeritAddRequestDto(
        member, absence, attendDate);
    meritService.addMeritWithLog(meritLogCreateRequestDto);
  }

  @Transactional
  void processPersonal(SeminarAttendanceEntity attendance, String absenceExcuse) {
    if (absenceExcuse == null) {
      throw new CustomAttendanceAbsenceExcuseIsNullException();
    }
    attendance.setSeminarAttendanceExcuseEntity(
        SeminarAttendanceExcuseEntity.builder()
            .seminarAttendanceEntity(attendance)
            .absenceExcuse(absenceExcuse)
            .build());
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
    String seminarName = openTime.toLocalDate().toString();
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
        .sorted(Comparator.comparing(SeminarAttendanceResponseDto::getGeneration)
            .thenComparing(SeminarAttendanceResponseDto::getMemberName))
        .toList();
  }

  @Transactional
  SeminarEntity generateSeminar(LocalDateTime openTime) {
    return seminarRepository.save(SeminarEntity.builder()
        .openTime(openTime)
        .build()
    );
  }

  @Transactional
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

  @Transactional
  public Long deleteSeminar(Long seminarId) {
    SeminarEntity find = seminarRepository.findById(seminarId)
        .orElseThrow(CustomSeminarNotFoundException::new);
    seminarRepository.delete(find);
    return find.getId();
  }

  public SeminarSearchByDateResponseDto findSeminarByDate(LocalDate searchDate) {
    String seminarName = searchDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return seminarRepository.findByName(seminarName)
        .map(SeminarSearchByDateResponseDto::from)
        .orElse(SeminarSearchByDateResponseDto.NONE);
  }

  @Transactional
  public AttendanceStartResponseDto startSeminarAttendance(AttendanceStartRequestDto request) {
    SeminarEntity seminar = seminarRepository.findById(request.getSeminarId())
        .orElseThrow(CustomSeminarNotFoundException::new);
    seminar.startAttendance(request.getAttendanceCloseTime(), request.getLatenessCloseTime(),
        generateRandomAttendanceCode());
    MemberEntity host = authService.getMemberEntityWithJWT();
    seminarHostAutoAttendance(seminar, host);
    autoAttendanceAfterDeadline(seminar, host);
    return AttendanceStartResponseDto.from(seminar);
  }

  private String generateRandomAttendanceCode() {
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < ATTENDANCE_CODE_LENGTH; i++) {
      code.append(random.nextInt(10));
    }
    return code.toString();
  }

  private void seminarHostAutoAttendance(SeminarEntity seminar, MemberEntity host) {
    SeminarAttendanceEntity seminarAttendance = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, host).orElseThrow(CustomSeminarAttendanceNotFoundException::new);
    SeminarAttendanceStatusEntity status = seminarAttendanceStatusRepository.findById(
        ATTENDANCE.getId()).orElseThrow(CustomSeminarAttendanceStatusNotFoundException::new);
    processAttendance(seminarAttendance, status, "");
  }

  private void autoAttendanceAfterDeadline(SeminarEntity seminar, MemberEntity host) {
    SeminarAttendanceStatusEntity beforeAttendance = seminarAttendanceStatusRepository.findById(
            BEFORE_ATTENDANCE.getId())
        .orElseThrow(CustomSeminarAttendanceStatusNotFoundException::new);
    SeminarAttendanceStatusEntity absence = seminarAttendanceStatusRepository.findById(
        ABSENCE.getId()).orElseThrow(CustomSeminarAttendanceStatusNotFoundException::new);
    Date date = Date.from(
        //* 테스트 진행 시 추가되는 시간을 30 -> 1초로 변경
        seminar.getLatenessCloseTime().plusSeconds(30).atZone(ZoneId.of("Asia/Seoul"))
            .toInstant());
    List<String> jobs = host.getJobs();
    Runnable task = new Runnable() {
      @Override
      public void run() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
          @Override
          protected void doInTransactionWithoutResult(TransactionStatus status) {
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(
                //* AdminSeminarControllerTest [SUCCESS] 세미나 출석 시작 - 자동 출석 확인
                //* 테스트 결과 확인을 위해서는 host.getId() -> 1L
                new UsernamePasswordAuthenticationToken(host.getId(),
                    host.getPassword(),
                    jobs.stream().map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())));
            List<SeminarAttendanceEntity> notAttendances = seminarAttendanceRepository.findAllBySeminarEntityAndSeminarAttendanceStatusEntity(
                seminar, beforeAttendance);
            notAttendances.forEach(
                attendance -> processAttendance(attendance, absence, "세미나 불참")
            );
          }
        });
      }
    };
    schedulerService.scheduleTask(task, date);
  }

  private static MeritAddRequestDto getMeritAddRequestDto(MemberEntity member,
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

}
