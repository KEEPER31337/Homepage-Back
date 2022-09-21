package keeper.project.homepage.election.controller;

import javax.validation.Valid;
import keeper.project.homepage.election.dto.request.ElectionCandidateCreateRequestDto;
import keeper.project.homepage.election.dto.request.ElectionCreateRequestDto;
import keeper.project.homepage.election.dto.response.ElectionCandidateDeleteResponseDto;
import keeper.project.homepage.election.dto.response.ElectionDeleteResponseDto;
import keeper.project.homepage.election.dto.response.ElectionUpdateResponseDto;
import keeper.project.homepage.election.dto.response.ElectionVoterCreateResponseDto;
import keeper.project.homepage.election.dto.response.ElectionVoterResponseDto;
import keeper.project.homepage.election.service.AdminElectionService;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Secured("ROLE_회장")
@RequestMapping("/v1/admin/elections")
public class AdminElectionController {

  private final ResponseService responseService;
  private final AdminElectionService adminElectionService;

  @PostMapping("")
  public SingleResult<Long> createElection(
      @RequestBody @Valid ElectionCreateRequestDto requestDto) {
    return responseService.getSuccessSingleResult(adminElectionService.createElection(requestDto));
  }

  @DeleteMapping("/{id}")
  public SingleResult<ElectionDeleteResponseDto> deleteElection(
      @PathVariable("id") Long electionId
  ) {
    return responseService.getSuccessSingleResult(adminElectionService.deleteElection(electionId));
  }

  @PatchMapping("/{id}/open")
  public SingleResult<ElectionUpdateResponseDto> openElection(
      @PathVariable("id") Long electionId) {
    return responseService.getSuccessSingleResult(adminElectionService.openElection(electionId));
  }

  @PatchMapping("/{id}/close")
  public SingleResult<ElectionUpdateResponseDto> closeElection(
      @PathVariable("id") Long electionId) {
    ElectionUpdateResponseDto result = adminElectionService.closeElection(electionId);
    adminElectionService.sendVoteEnd(electionId);
    return responseService.getSuccessSingleResult(result);
  }

  @PostMapping("/candidate")
  public SingleResult<Long> registerCandidate(
      @RequestBody @Valid ElectionCandidateCreateRequestDto requestDto) {
    return responseService.getSuccessSingleResult(
        adminElectionService.registerCandidate(requestDto));
  }

  @DeleteMapping("/candidate/{id}")
  public SingleResult<ElectionCandidateDeleteResponseDto> deleteCandidate(
      @PathVariable("id") Long candidateId) {
    return responseService.getSuccessSingleResult(
        adminElectionService.deleteCandidate(candidateId));
  }

  @GetMapping("/{eid}/voters")
  public ListResult<ElectionVoterResponseDto> getVoters(
      @PathVariable("eid") Long electionId
  ) {
    return responseService.getSuccessListResult(adminElectionService.getVoters(electionId));
  }

  @PostMapping("/{eid}/voters/{vid}")
  public SingleResult<ElectionVoterCreateResponseDto> registerVoter(
      @PathVariable("eid") Long electionId,
      @PathVariable("vid") Long memberId
  ) {
    return responseService.getSuccessSingleResult(
        adminElectionService.registerVoter(electionId, memberId));
  }

  @DeleteMapping("/{eid}/voters/{vid}")
  public SingleResult<Long> deleteVoter(
      @PathVariable("eid") Long electionId,
      @PathVariable("vid") Long voterId
  ) {
    return responseService.getSuccessSingleResult(
        adminElectionService.deleteVoter(electionId, voterId)
    );
  }
}
