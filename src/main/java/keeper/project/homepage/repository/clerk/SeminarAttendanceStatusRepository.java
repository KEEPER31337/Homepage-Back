package keeper.project.homepage.repository.clerk;

import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceStatusRepository extends
    JpaRepository<SeminarAttendanceStatusEntity, Long> {

}
