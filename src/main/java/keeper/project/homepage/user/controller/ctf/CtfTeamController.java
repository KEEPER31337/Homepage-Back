package keeper.project.homepage.user.controller.ctf;

import keeper.project.homepage.common.dto.result.PageResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.dto.ctf.CtfJoinTeamRequestDto;
import keeper.project.homepage.user.dto.ctf.CtfLeaveTeamRequestDto;
import keeper.project.homepage.user.dto.ctf.CtfTeamDetailDto;
import keeper.project.homepage.user.dto.ctf.CtfTeamDto;
import keeper.project.homepage.user.dto.ctf.CtfTeamHasMemberDto;
import keeper.project.homepage.user.service.ctf.CtfTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ctf/team")
@Secured("ROLE_회원")
public class CtfTeamController {

  private final ResponseService responseService;
  private final CtfTeamService ctfTeamService;

  @PostMapping("")
  public SingleResult<CtfTeamDetailDto> createTeam(
      @RequestBody CtfTeamDetailDto ctfTeamDetailDto) {
    return responseService.getSuccessSingleResult(ctfTeamService.createTeam(ctfTeamDetailDto));
  }

  @RequestMapping(value = "/{teamId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
  public SingleResult<CtfTeamDetailDto> modifyTeam(
      @PathVariable("teamId") Long teamId,
      @RequestBody CtfTeamDto ctfTeamDto) {
    return responseService.getSuccessSingleResult(ctfTeamService.modifyTeam(teamId, ctfTeamDto));
  }

  @PostMapping(value = "/member")
  public SingleResult<CtfTeamHasMemberDto> joinTeam(
      @RequestBody CtfJoinTeamRequestDto requestDto
  ) {
    return responseService.getSuccessSingleResult(
        ctfTeamService.joinTeam(requestDto.getTeamName()));
  }

  @DeleteMapping(value = "/member")
  public SingleResult<CtfTeamDetailDto> leaveTeam(
      @RequestBody CtfLeaveTeamRequestDto requestDto
  ) {
    return responseService.getSuccessSingleResult(ctfTeamService.leaveTeam(requestDto.getCtfId()));
  }

  @GetMapping(value = "/{teamId}")
  public SingleResult<CtfTeamDetailDto> getTeamDetail(
      @PathVariable("teamId") Long teamId) {
    return responseService.getSuccessSingleResult(ctfTeamService.getTeamDetail(teamId));
  }

  @GetMapping(value = "")
  public PageResult<CtfTeamDto> getTeamList(
      @PageableDefault Pageable pageable,
      @RequestParam Long ctfId
  ) {
    return responseService.getSuccessPageResult(ctfTeamService.getTeamList(pageable, ctfId));
  }

  @GetMapping(value = "/{ctfId}/my-team")
  public SingleResult<CtfTeamDetailDto> getMyTeam(
      @PathVariable("ctfId") Long ctfId) {
    return responseService.getSuccessSingleResult(ctfTeamService.getMyTeam(ctfId));
  }
}
