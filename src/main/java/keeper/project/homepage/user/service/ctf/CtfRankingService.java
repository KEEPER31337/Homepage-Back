package keeper.project.homepage.user.service.ctf;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_TEAM_ID;

import keeper.project.homepage.repository.ctf.CtfTeamRepository;
import keeper.project.homepage.user.dto.ctf.CtfTeamDto;
import keeper.project.homepage.util.service.CtfUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CtfRankingService {

  private final CtfTeamRepository teamRepository;
  private final CtfUtilService ctfUtilService;

  public Page<CtfTeamDto> getRankingList(Long ctfId, Pageable pageable) {

    ctfUtilService.checkVirtualContest(ctfId);

    return teamRepository.findAllByIdIsNotAndCtfContestEntity_Id(VIRTUAL_TEAM_ID, ctfId, pageable)
        .map(CtfTeamDto::toDto);
  }
}
