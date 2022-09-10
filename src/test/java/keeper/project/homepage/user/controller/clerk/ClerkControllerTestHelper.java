package keeper.project.homepage.user.controller.clerk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.clerk.MeritLogRepository;
import keeper.project.homepage.repository.clerk.MeritTypeRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceExcuseRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceStatusRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ClerkControllerTestHelper extends ApiControllerTestHelper {

  @Autowired
  protected SeminarRepository seminarRepository;

  @Autowired
  protected SeminarAttendanceRepository seminarAttendanceRepository;

  @Autowired
  protected SeminarAttendanceExcuseRepository seminarAttendanceExcuseRepository;

  @Autowired
  protected SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;

  @Autowired
  protected MeritTypeRepository meritTypeRepository;

  @Autowired
  MeritLogRepository meritLogRepository;

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected String asJsonDateString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.registerModule(new JavaTimeModule()).writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  SeminarEntity generateSeminar(LocalDateTime openTime, LocalDateTime attendanceCloseTime,
      LocalDateTime latenessCloseTime, String code) {
    return seminarRepository.save(SeminarEntity.builder()
        .name(openTime.toLocalDate()
            .toString())
        .openTime(openTime)
        .attendanceCloseTime(attendanceCloseTime)
        .latenessCloseTime(latenessCloseTime)
        .attendanceCode(code)
        .build()
    );
  }

  SeminarAttendanceEntity generateSeminarAttendance(MemberEntity member,
      SeminarEntity seminar, SeminarAttendanceStatusEntity seminarAttendanceStatus) {
    return seminarAttendanceRepository.save(
        SeminarAttendanceEntity.builder()
            .memberEntity(member)
            .seminarEntity(seminar)
            .seminarAttendanceStatusEntity(seminarAttendanceStatus)
            .seminarAttendTime(LocalDateTime.now().withNano(0))
            .build()
    );
  }

  SeminarAttendanceExcuseEntity generateSeminarAttendanceExcuse(
      SeminarAttendanceEntity seminarAttendanceEntity) {
    return seminarAttendanceExcuseRepository.save(
        SeminarAttendanceExcuseEntity.builder()
            .seminarAttendanceEntity(seminarAttendanceEntity)
            .absenceExcuse("개인 사정")
            .build());
  }

  MemberEntity generateMember(String name, Float generation) {
    final long epochTime = System.nanoTime();
    return memberRepository.save(
        MemberEntity.builder()
            .loginId("abcd1234" + epochTime)
            .emailAddress("test1234@keeper.co.kr" + epochTime)
            .password("1234")
            .studentId("1234" + epochTime)
            .nickName("nick" + epochTime)
            .realName(name)
            .generation(generation)
            .build());
  }
}
