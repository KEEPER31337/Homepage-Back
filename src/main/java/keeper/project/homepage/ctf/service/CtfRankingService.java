package keeper.project.homepage.ctf.service;

import static keeper.project.homepage.util.service.CtfUtilService.VIRTUAL_TEAM_ID;

import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.ctf.repository.CtfTeamRepository;
import keeper.project.homepage.ctf.dto.CtfRankingDto;
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

  public Page<CtfRankingDto> getRankingList(Long ctfId, Pageable pageable) {

    ctfUtilService.checkVirtualContest(ctfId);
    Page<CtfTeamEntity> teamEntityPage = teamRepository.findAllByIdIsNotAndCtfContestEntity_Id(
        VIRTUAL_TEAM_ID, ctfId, pageable);

    Long rank = (long) teamEntityPage.getNumber() * teamEntityPage.getSize() + 1;
    Page<CtfRankingDto> result = getCtfRankingDtoPage(teamEntityPage, rank);
    return result;
  }

  private Page<CtfRankingDto> getCtfRankingDtoPage(Page<CtfTeamEntity> teamEntityPage, Long rank) {
    Page<CtfRankingDto> result = teamEntityPage.map(team -> CtfRankingDto.toDto(team, null));
    for (var ctfRankingDto : result) {
      ctfRankingDto.setRank(rank++);
    }
    return result;
  }
}
