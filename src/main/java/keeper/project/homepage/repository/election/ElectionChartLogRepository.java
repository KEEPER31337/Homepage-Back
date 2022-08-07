package keeper.project.homepage.repository.election;

import java.util.List;
import keeper.project.homepage.entity.election.ElectionChartLogEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionChartLogRepository extends JpaRepository<ElectionChartLogEntity, Long> {

  List<ElectionChartLogEntity> findAllByElectionCandidate_ElectionAndElectionCandidate_MemberJobOrderById(
      ElectionEntity election, MemberJobEntity memberJob);
}
