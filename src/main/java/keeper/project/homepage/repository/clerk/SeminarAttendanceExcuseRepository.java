package keeper.project.homepage.repository.clerk;

import keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceExcuseRepository extends
    JpaRepository<SeminarAttendanceExcuseEntity, Long> {

}
