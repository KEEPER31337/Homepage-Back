package keeper.project.homepage.election.repository;

import static org.assertj.core.api.Assertions.assertThat;

import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ElectionRepositoryTest extends ElectionRepositoryTestHelper {

  @Test
  @DisplayName("선거의 투표자 목록 수")
  public void getVotersInElection() throws Exception {
    //given
    MemberEntity member = memberRepository.getById(1L);
    ElectionEntity election = generateElection(member, true);
    generateElectionVoter(member, election, false);

    em.flush();
    em.clear();

    //when
    ElectionEntity loadedElection = electionRepository.getById(election.getId());

    //then
    assertThat(loadedElection.getVoters().size()).isEqualTo(1);
  }

}
