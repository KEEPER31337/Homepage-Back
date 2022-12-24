package keeper.project.homepage.ctf.dto;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_PROBLEM_ID;
import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_TEAM_ID;
import static org.assertj.core.api.Assertions.assertThat;

import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.repository.CtfChallengeRepository;
import keeper.project.homepage.ctf.repository.CtfFlagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CtfChallengeAdminDtoTest {

  @Autowired
  private CtfChallengeRepository challengeRepository;
  @Autowired
  private CtfFlagRepository flagRepository;

  @Test
  @DisplayName("Virtual Team Flag가 항상 존재하는지 테스트")
  void hasVirtualTeamFlag() {
    CtfFlagEntity virtualFlag = flagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
        VIRTUAL_PROBLEM_ID, VIRTUAL_TEAM_ID).get();
    CtfFlagEntity result = CtfChallengeAdminDto.getVirtualTeamFlag(
        challengeRepository.getById(VIRTUAL_PROBLEM_ID));
    assertThat(virtualFlag).isEqualTo(result);
  }
}