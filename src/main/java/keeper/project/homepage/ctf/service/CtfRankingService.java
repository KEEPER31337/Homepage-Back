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
    Page<CtfTeamEntity> teamEntityPage = getAllTeamEntityInRange(ctfId, pageable);
    return getCtfRankingDtoPageAndSetRank(teamEntityPage);
  }

  private long getRank(Page<CtfTeamEntity> teamEntityPage) {
    return (long) teamEntityPage.getNumber() * teamEntityPage.getSize() + 1;
  }

  private Page<CtfTeamEntity> getAllTeamEntityInRange(Long ctfId, Pageable pageable) {
    return teamRepository.findAllByIdIsNotAndCtfContestEntity_Id(
        VIRTUAL_TEAM_ID, ctfId, pageable);
  }

  private Page<CtfRankingDto> getCtfRankingDtoPageAndSetRank(Page<CtfTeamEntity> teamEntityPage) {
    Page<CtfRankingDto> ctfRankingDtoPage = getCtfRankingDtoPage(teamEntityPage);
    setRank(teamEntityPage, ctfRankingDtoPage);
    return ctfRankingDtoPage;
  }

  private void setRank(Page<CtfTeamEntity> teamEntityPage, Page<CtfRankingDto> ctfRankingDtoPage) {
    long rank = getRank(teamEntityPage);
    for (var ctfRankingDto : ctfRankingDtoPage) {
      ctfRankingDto.setRank(rank++);
    }
  }

  private Page<CtfRankingDto> getCtfRankingDtoPage(Page<CtfTeamEntity> teamEntityPage) {
    Page<CtfRankingDto> result = teamEntityPage
        .map(team -> CtfRankingDto.toDto(team, null));
    return result;
  }
}
