package keeper.project.homepage.clerk.repository;

import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceStatusRepository extends
    JpaRepository<SeminarAttendanceStatusEntity, Long> {

}
