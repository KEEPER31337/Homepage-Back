package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;

import java.util.List;
import java.util.Random;
import keeper.project.homepage.admin.dto.clerk.request.AttendanceConditionRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.AttendanceConditionResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.LatestSeminarResponseDto;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.clerk.SeminarAttendanceExcuseRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceStatusRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import keeper.project.homepage.user.service.member.MemberUtilService;
import keeper.project.homepage.util.service.SurveyUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAutoAttendanceService {

  private static final Integer ABSENCE_DEMERIT = 3;
  private static final Random random = new Random();
  private final SeminarRepository seminarRepository;
  private final SeminarAttendanceRepository seminarAttendanceRepository;
  private final SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;
  private final SeminarAttendanceExcuseRepository seminarAttendanceExcuseRepository;
  private final SurveyUtilService surveyUtilService;
  private final MemberUtilService memberUtilService;

  public LatestSeminarResponseDto getLatestSeminar() {
    SeminarEntity seminar = seminarRepository.findTop1ByOrderByIdDesc().orElseThrow();
    return LatestSeminarResponseDto.from(seminar);
  }

  public AttendanceConditionResponseDto getAttendanceConditions(
      AttendanceConditionRequestDto requestDto) {
    //TODO: 찾을 수 없으면 오류
    SeminarEntity seminar = seminarRepository.findById(requestDto.getSeminarId()).orElseThrow();

    seminar.setAttendanceCode(generateAttendanceCode());
    seminar.setAttendanceCloseTime(requestDto.getAttendanceCloseTime());
    seminar.setLatenessCloseTime(requestDto.getLatenessCloseTime());

    //회장 출석 처리.
    processingAttendance(requestDto, seminar);

    return AttendanceConditionResponseDto.from(seminar);
  }

  private void processingAttendance(AttendanceConditionRequestDto requestDto,
      SeminarEntity seminar) {
    MemberEntity member = memberUtilService.getById(requestDto.getMemberId());
    SeminarAttendanceEntity seminarAttendanceEntity = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow();
    seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
        seminarAttendanceStatusRepository.getById(ATTENDANCE.getId()));
  }

  private String generateAttendanceCode() {
    int attendanceCode = random.nextInt(999) + 100;
    return Integer.toString(attendanceCode);
  }

  public int attendanceCheckEnd() {
    List<SeminarAttendanceEntity> seminarAttendanceEntities = seminarAttendanceRepository
        .findAllBySeminarAttendanceStatusEntity(
            seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId())).orElseThrow();

    for (SeminarAttendanceEntity seminarAttendanceEntity : seminarAttendanceEntities) {
      seminarAttendanceEntity.setSeminarAttendanceStatusEntity(
          seminarAttendanceStatusRepository.getById(ABSENCE.getId()));
      MemberEntity member = seminarAttendanceEntity.getMemberEntity();
      member.changeDemerit(member.getDemerit() + ABSENCE_DEMERIT);
    }

    return seminarAttendanceEntities.size();
  }
}
