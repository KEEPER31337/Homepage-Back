package keeper.project.homepage.election.repository;

import java.util.List;
import keeper.project.homepage.election.entity.ElectionCandidateEntity;
import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ElectionCandidateRepository extends JpaRepository<ElectionCandidateEntity, Long> {

  Boolean existsByCandidateAndElectionAndMemberJob(MemberEntity candidate, ElectionEntity election, MemberJobEntity memberJob);

  List<ElectionCandidateEntity> findAllByElectionAndMemberJob(ElectionEntity election,
      MemberJobEntity memberJob);

  @Query("select count(distinct e.memberJob) from ElectionCandidateEntity e where e.election = :election")
  Long getDistinctCountMemberJobByElection(@Param("election") ElectionEntity election);
}
