package keeper.project.homepage.repository.clerk;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class SeminarRepositoryTestHelper {

  @Autowired
  EntityManager em;

  @Autowired
  SeminarRepository seminarRepository;

  @Autowired
  SeminarAttendanceRepository seminarAttendanceRepository;

  @Autowired
  SeminarAttendanceExcuseRepository seminarAttendanceExcuseRepository;

  @Autowired
  SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;
  @Autowired
  MemberRepository memberRepository;

  SeminarEntity generateSeminar(LocalDateTime openTime) {
    return seminarRepository.save(SeminarEntity.builder()
        .openTime(openTime)
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


}
