package keeper.project.homepage.repository.election;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import keeper.project.homepage.controller.election.ElectionSpringTestHelper;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.election.response.ElectionVoteStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ElectionVoterRepositoryTest extends ElectionSpringTestHelper {

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
  @DisplayName("투표 현황 DTO")
  public void getVoteStatus() throws Exception {
    //given
    ElectionVoteStatus status = ElectionVoteStatus.createStatus(10, 5, true);

    //then
    assertThat(status.getTotal()).isEqualTo(10);
    assertThat(status.getVoted()).isEqualTo(5);
    assertThat(status.getRate()).isEqualTo("50.00");
    assertThat(status.getIsOpen()).isEqualTo(true);
  }

  @Test
  @DisplayName("투표를 진행한 투표자 목록 수")
  public void getVotersIsVoted() throws Exception {
    //given
    ElectionEntity election = generateElection(admin, true);
    generateElectionVoter(admin, election, true);
    generateElectionVoter(user, election, false);

    em.flush();
    em.clear();

    //when
    Integer result = electionVoterRepository.countAllByElectionVoterPK_ElectionAndIsVotedIsTrue(
        election);

    //then
    assertThat(result).isEqualTo(1);
  }
}
