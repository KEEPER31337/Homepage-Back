package keeper.project.homepage.admin.service.election;

import java.time.LocalDateTime;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.election.ElectionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AdminElectionServiceTest extends ApiControllerTestHelper {

  @Autowired
  private ElectionRepository electionRepository;

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
    Assertions.assertThat(findElection.getIsAvailable()).isTrue();
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
    Assertions.assertThat(findElection.getIsAvailable()).isFalse();
  }

}
