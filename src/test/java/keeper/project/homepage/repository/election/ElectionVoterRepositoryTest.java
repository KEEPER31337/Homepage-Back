package keeper.project.homepage.repository.election;

import static org.assertj.core.api.Assertions.assertThat;

import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.election.response.ElectionVoteStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ElectionVoterRepositoryTest extends ElectionRepositoryTestHelper {

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
    MemberEntity member = memberRepository.getById(1L);
    ElectionEntity election = generateElection(member, true);
    generateElectionVoter(member, election, true);

    em.flush();
    em.clear();

    //when
    Integer result = electionVoterRepository.countAllByElectionVoterPK_ElectionAndIsVotedIsTrue(
        election);

    //then
    assertThat(result).isEqualTo(1);
  }
}
