package keeper.project.homepage.repository.seminar;

import keeper.project.homepage.entity.seminar.SeminarAttendanceExcuseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceExcuseRepository extends
    JpaRepository<SeminarAttendanceExcuseEntity, Long> {

}
