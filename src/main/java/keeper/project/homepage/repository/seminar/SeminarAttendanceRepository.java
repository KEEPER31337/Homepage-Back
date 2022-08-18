package keeper.project.homepage.repository.seminar;

import java.awt.print.Pageable;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.seminar.SeminarAttendanceEntity;
import keeper.project.homepage.entity.seminar.SeminarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceRepository extends JpaRepository<SeminarAttendanceEntity, Long> {

  SeminarAttendanceEntity findBySeminarEntityAndMemberEntity(SeminarEntity seminarEntity, MemberEntity memberEntity);
}
