package keeper.project.homepage.clerk.repository;

import keeper.project.homepage.clerk.entity.SeminarAttendanceExcuseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceExcuseRepository extends
    JpaRepository<SeminarAttendanceExcuseEntity, Long> {

}
