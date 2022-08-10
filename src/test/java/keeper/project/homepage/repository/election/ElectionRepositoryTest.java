package keeper.project.homepage.repository.election;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import keeper.project.homepage.controller.election.ElectionSpringTestHelper;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ElectionRepositoryTest extends ElectionSpringTestHelper {

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
  @DisplayName("선거의 투표자 목록 수")
  public void getVotersInElection() throws Exception {
    //given
    ElectionEntity election = generateElection(admin, true);
    generateElectionVoter(admin, election, false);
    generateElectionVoter(user, election, false);

    em.flush();
    em.clear();

    //when
    ElectionEntity loadedElection = electionRepository.getById(election.getId());

    //then
    assertThat(loadedElection.getVoters().size()).isEqualTo(2);
  }

}
