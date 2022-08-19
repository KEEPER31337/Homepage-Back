package keeper.project.homepage.repository.clerk;

import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceRepository extends JpaRepository<SeminarAttendanceEntity, Long> {

  SeminarAttendanceEntity findBySeminarEntityAndMemberEntity(SeminarEntity seminarEntity, MemberEntity memberEntity);
}
