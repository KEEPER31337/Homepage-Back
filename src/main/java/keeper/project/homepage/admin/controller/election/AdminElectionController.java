package keeper.project.homepage.admin.controller.election;

import javax.validation.Valid;
import keeper.project.homepage.admin.dto.election.request.ElectionCandidateMultiSaveRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCandidateSaveRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCreateRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionVoterMultiSaveRequestDto;
import keeper.project.homepage.admin.dto.election.response.ElectionCandidateDeleteResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionCandidateMultiSaveResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionUpdateResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionVoterMultiSaveResponseDto;
import keeper.project.homepage.admin.service.election.AdminElectionService;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @PatchMapping("/{id}/open")
  public SingleResult<ElectionUpdateResponseDto> openElection(
      @PathVariable("id") Long electionId) {
    return responseService.getSuccessSingleResult(adminElectionService.openElection(electionId));
  }

  @PatchMapping("/{id}/close")
  public SingleResult<ElectionUpdateResponseDto> closeElection(
      @PathVariable("id") Long electionId) {
    return responseService.getSuccessSingleResult(adminElectionService.closeElection(electionId));
  }

  @PostMapping("/candidates")
  public SingleResult<ElectionCandidateMultiSaveResponseDto> registerCandidates(
      @RequestBody @Valid ElectionCandidateMultiSaveRequestDto requestDto
  ) {
    return responseService.getSuccessSingleResult(
        adminElectionService.registerCandidates(requestDto));
  }

  @PostMapping("/candidate")
  public SingleResult<Long> registerCandidate(
      @RequestBody @Valid ElectionCandidateSaveRequestDto requestDto) {
    return responseService.getSuccessSingleResult(
        adminElectionService.registerCandidate(requestDto));
  }

  @DeleteMapping("/candidate/{id}")
  public SingleResult<ElectionCandidateDeleteResponseDto> deleteCandidate(
      @PathVariable("id") Long candidateId) {
    return responseService.getSuccessSingleResult(
        adminElectionService.deleteCandidate(candidateId));
  }

  @PostMapping("/voters")
  public SingleResult<ElectionVoterMultiSaveResponseDto> registerVoters(
      @RequestBody @Valid ElectionVoterMultiSaveRequestDto requestDto
  ) {
    return responseService.getSuccessSingleResult(adminElectionService.registerVoters(requestDto));
  }

  @PostMapping("/voter")
  public SingleResult<ElectionVoterMultiSaveResponseDto> registerVoter(
      @RequestParam Long electionId,
      @RequestParam Long memberId
  ) {
    return responseService.getSuccessSingleResult(
        adminElectionService.registerVoter(electionId, memberId));
  }

  @DeleteMapping("/voter")
  public SingleResult<Long> deleteVoter(
      @RequestParam Long electionId,
      @RequestParam Long voterId
  ) {
    return responseService.getSuccessSingleResult(
        adminElectionService.deleteVoter(electionId, voterId)
    );
  }
}
