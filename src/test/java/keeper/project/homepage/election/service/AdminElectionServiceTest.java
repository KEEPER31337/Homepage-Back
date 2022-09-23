package keeper.project.homepage.election.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.election.controller.ElectionSpringTestHelper;
import keeper.project.homepage.election.entity.ElectionCandidateEntity;
import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AdminElectionServiceTest extends ElectionSpringTestHelper {

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
  @DisplayName("선거 오픈")
  public void openElection() throws Exception {
    //given
    MemberEntity member = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원,
        MemberRankName.일반회원);

    ElectionEntity election = electionRepository.save(
        ElectionEntity.builder()
            .name("선거")
            .creator(member)
            .registerTime(LocalDateTime.now())
            .isAvailable(false)
            .build());

    //when
    election.openElection();
    ElectionEntity findElection = electionRepository.getById(election.getId());

    //then
    assertThat(findElection.getIsAvailable()).isTrue();
  }

  @Test
  @DisplayName("선거 종료")
  public void closeElection() throws Exception {
    //given
    MemberEntity member = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원,
        MemberRankName.일반회원);

    ElectionEntity election = electionRepository.save(
        ElectionEntity.builder()
            .name("선거")
            .creator(member)
            .registerTime(LocalDateTime.now())
            .isAvailable(true)
            .build());

    //when
    election.closeElection();
    ElectionEntity findElection = electionRepository.getById(election.getId());

    //then
    assertThat(findElection.getIsAvailable()).isFalse();
  }

  @Test
  @DisplayName("선거 삭제 시 연관된 후보자 삭제")
  public void deleteElectionWithCandidate() throws Exception {
    //given
    Long VIRTUAL_ELECTION_ID = 1L;
    Long VIRTUAL_CANDIDATE_ID = 1L;
    ElectionEntity election = generateElection(admin, true);
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회원").get();
    generateElectionCandidate(admin, election, memberJob);
    generateElectionCandidate(user, election, memberJob);

    //when
    electionRepository.delete(election);

    em.flush();
    em.clear();

    //then
    List<ElectionEntity> elections = electionRepository.findAll();
    List<ElectionCandidateEntity> candidates = electionCandidateRepository.findAll();

    assertThat(elections).containsExactly(electionRepository.getById(VIRTUAL_ELECTION_ID));
    assertThat(elections.size()).isEqualTo(1);
    assertThat(candidates).containsExactly(electionCandidateRepository.getById(VIRTUAL_CANDIDATE_ID));
    assertThat(candidates.size()).isEqualTo(1);
  }

  @Test
  @DisplayName("후보 삭제 시 연관된 차트 로그 삭제")
  public void deleteElectionWithCascade() throws Exception {
    //given
    Long VIRTUAL_CANDIDATE_ID = 1L;
    ElectionEntity election = generateElection(admin, true);
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회장").get();
    ElectionCandidateEntity candidate = generateElectionCandidate(user, election, memberJob);
    generateElectionChartLog(candidate);
    generateElectionChartLog(candidate);

    //when
    electionCandidateRepository.delete(candidate);

    em.flush();
    em.clear();

    //then
    List<ElectionCandidateEntity> candidates = electionCandidateRepository.findAll();

    assertThat(candidates).containsExactly(electionCandidateRepository.getById(VIRTUAL_CANDIDATE_ID));
    assertThat(candidates.size()).isEqualTo(1);
    assertThat(electionChartLogRepository.findAll().size()).isEqualTo(0);
  }
}
