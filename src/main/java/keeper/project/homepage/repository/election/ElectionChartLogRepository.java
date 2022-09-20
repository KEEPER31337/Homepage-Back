package keeper.project.homepage.repository.election;

import java.util.List;
import keeper.project.homepage.election.entity.ElectionChartLogEntity;
import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionChartLogRepository extends JpaRepository<ElectionChartLogEntity, Long> {

  List<ElectionChartLogEntity> findAllByElectionCandidate_ElectionAndElectionCandidate_MemberJobOrderById(
      ElectionEntity election, MemberJobEntity memberJob);
}
