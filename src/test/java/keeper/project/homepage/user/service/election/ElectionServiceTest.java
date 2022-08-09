package keeper.project.homepage.user.service.election;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import keeper.project.homepage.controller.election.ElectionSpringTestHelper;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionChartLogEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ElectionServiceTest extends ElectionSpringTestHelper {

  @Autowired
  private EntityManager em;

  private MemberEntity user;
  private MemberEntity admin;

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
  }

  @Test
  @DisplayName("선거와 연관된 투표자 목록")
  public void getElectionWithVoters() throws Exception {
    //given
    ElectionEntity election = generateElection(admin, true);
    generateElectionVoter(user, election, false);
    generateElectionVoter(admin, election, false);

    em.flush();
    em.clear();

    //when
    ElectionEntity loadedElection = electionRepository.getById(election.getId());
    MemberEntity savedUser = memberRepository.getById(user.getId());
    MemberEntity savedAdmin = memberRepository.getById(admin.getId());

    //then
    assertThat(loadedElection.getVoters().size()).isEqualTo(2);
    assertThat(
        loadedElection.getVoters().stream().map(voter -> voter.getElectionVoterPK().getVoter())
            .toList()).contains(savedUser, savedAdmin);
  }

  @Test
  @DisplayName("선거와 연관된 후보자 목록")
  public void getElectionWithCandidates() throws Exception {
    //given
    ElectionEntity election = generateElection(admin, true);
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회장").get();
    generateElectionCandidate(user, election, memberJob);
    generateElectionCandidate(admin, election, memberJob);

    em.flush();
    em.clear();

    //when
    ElectionEntity loadedElection = electionRepository.getById(election.getId());
    MemberEntity savedUser = memberRepository.getById(user.getId());
    MemberEntity savedAdmin = memberRepository.getById(admin.getId());

    //then
    assertThat(loadedElection.getCandidates().size()).isEqualTo(2);
    assertThat(
        loadedElection.getCandidates().stream().map(ElectionCandidateEntity::getCandidate)
            .toList()).contains(savedUser, savedAdmin);
  }

  @Test
  @DisplayName("후보자와 연관된 차트 로그 목록")
  public void getCandidateWithChartLogs() throws Exception {
    //given
    ElectionEntity election = generateElection(admin, true);
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회장").get();
    ElectionCandidateEntity candidate = generateElectionCandidate(user, election, memberJob);
    ElectionChartLogEntity chartLog1 = generateElectionChartLog(candidate);
    ElectionChartLogEntity chartLog2 = generateElectionChartLog(candidate);

    em.flush();
    em.clear();

    //when
    ElectionCandidateEntity loadedCandidate = electionCandidateRepository.getById(candidate.getId());
    ElectionChartLogEntity savedChartLog1 = electionChartLogRepository.getById(chartLog1.getId());
    ElectionChartLogEntity savedChartLog2 = electionChartLogRepository.getById(chartLog2.getId());

    //then
    assertThat(loadedCandidate.getChartLogs().size()).isEqualTo(2);
    assertThat(loadedCandidate.getChartLogs()).contains(savedChartLog1, savedChartLog2);
  }

}
