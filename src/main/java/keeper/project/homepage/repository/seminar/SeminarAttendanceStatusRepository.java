package keeper.project.homepage.repository.seminar;

import keeper.project.homepage.entity.seminar.SeminarAttendanceStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceStatusRepository extends
    JpaRepository<SeminarAttendanceStatusEntity, Long> {

}
