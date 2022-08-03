package keeper.project.homepage.repository.election;

import keeper.project.homepage.entity.election.ElectionChartLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionChartLogRepository extends JpaRepository<ElectionChartLogEntity, Long> {

}
