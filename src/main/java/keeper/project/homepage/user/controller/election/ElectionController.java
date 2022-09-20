package keeper.project.homepage.user.controller.election;

import javax.validation.Valid;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.PageResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.user.dto.election.request.ElectionVoteRequestDto;
import keeper.project.homepage.user.dto.election.response.ElectionCandidatesResponseDto;
import keeper.project.homepage.user.dto.election.response.ElectionResponseDto;
import keeper.project.homepage.user.dto.election.response.ElectionResultResponseDto;
import keeper.project.homepage.user.dto.election.response.ElectionVoteStatus;
import keeper.project.homepage.user.service.election.ElectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/elections")
@Secured("ROLE_회원")
public class ElectionController {

  private final ResponseService responseService;
  private final ElectionService electionService;

  @GetMapping("")
  public PageResult<ElectionResponseDto> getElections(
      @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable
  ) {
    return responseService.getSuccessPageResult(electionService.getElections(pageable));
  }

  @GetMapping("/open")
  public PageResult<ElectionResponseDto> getOpenElections(
      @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable
  ) {
    return responseService.getSuccessPageResult(electionService.getOpenElections(pageable));
  }

  @GetMapping("/close")
  public PageResult<ElectionResponseDto> getCloseElections(
      @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable
  ) {
    return responseService.getSuccessPageResult(electionService.getCloseElections(pageable));
  }

  @GetMapping("/join/{id}")
  public SingleResult<Boolean> joinElection(
      @PathVariable("id") Long electionId
  ) {
    return responseService.getSuccessSingleResult(electionService.joinElection(electionId));
  }

  @GetMapping("/{eid}/jobs/{jid}")
  public ListResult<ElectionCandidatesResponseDto> getCandidates(
      @PathVariable("eid") Long electionId,
      @PathVariable("jid") Long jobId
  ) {
    return responseService.getSuccessListResult(electionService.getCandidates(electionId, jobId));
  }

  @GetMapping("/{eid}/votes")
  public SingleResult<ElectionVoteStatus> getVoteStatus(
      @PathVariable("eid") Long electionId
  ) {
    return responseService.getSuccessSingleResult(electionService.getVoteStatus(electionId));
  }

  @PostMapping("/votes")
  public SingleResult<Boolean> voteElection(
      @RequestBody @Valid ElectionVoteRequestDto electionVoteRequestDto
  ) {
    Boolean result = electionService.voteElection(electionVoteRequestDto);
    electionService.sendVoteStatus(electionVoteRequestDto.getElectionId());
    return responseService.getSuccessSingleResult(result);
  }

  @GetMapping("/votes")
  public SingleResult<Boolean> isVoted(
      @RequestParam Long electionId,
      @RequestParam Long voterId
  ) {
    return responseService.getSuccessSingleResult(electionService.isVoted(electionId, voterId));
  }

  @GetMapping("/results")
  public ListResult<ElectionResultResponseDto> countVotes(
      @RequestParam Long electionId,
      @RequestParam Long jobId
  ) {
    return responseService.getSuccessListResult(electionService.countVotes(electionId, jobId));
  }

}
