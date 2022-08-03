package keeper.project.homepage.admin.controller.election;

import javax.validation.Valid;
import keeper.project.homepage.admin.dto.election.request.ElectionCandidateRegisterRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCandidateRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionVoterRegisterRequestDto;
import keeper.project.homepage.admin.dto.election.response.ElectionCandidateResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionCandidateRegisterResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionVoterRegisterResponseDto;
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
  public SingleResult<Long> setUpElection(
      @RequestBody @Valid ElectionRequestDto electionRequestDto) {
    return responseService.getSuccessSingleResult(
        adminElectionService.setUpElection(electionRequestDto));
  }

  @PatchMapping("/{id}/open")
  public SingleResult<ElectionResponseDto> openElection(
      @PathVariable("id") Long electionId) {
    return responseService.getSuccessSingleResult(adminElectionService.openElection(electionId));
  }

  @PatchMapping("/{id}/close")
  public SingleResult<ElectionResponseDto> closeElection(
      @PathVariable("id") Long electionId) {
    return responseService.getSuccessSingleResult(adminElectionService.closeElection(electionId));
  }

  @PostMapping("/candidates")
  public SingleResult<ElectionCandidateRegisterResponseDto> registerCandidates(
      @RequestBody @Valid ElectionCandidateRegisterRequestDto electionRegistryRequestDto
  ) {
    return responseService.getSuccessSingleResult(
        adminElectionService.registerCandidates(electionRegistryRequestDto));
  }

  @PostMapping("/candidate")
  public SingleResult<Long> registerCandidate(
      @RequestBody @Valid ElectionCandidateRequestDto electionCandidateRequestDto) {
    return responseService.getSuccessSingleResult(
        adminElectionService.registerCandidate(electionCandidateRequestDto));
  }

  @DeleteMapping("/candidate/{id}")
  public SingleResult<ElectionCandidateResponseDto> deleteCandidate(
      @PathVariable("id") Long candidateId) {
    return responseService.getSuccessSingleResult(
        adminElectionService.deleteCandidate(candidateId));
  }

  @PostMapping("/voters")
  public SingleResult<ElectionVoterRegisterResponseDto> registerVoters(
      @RequestBody @Valid ElectionVoterRegisterRequestDto electionVoterRegisterRequestDto
  ) {
    return responseService.getSuccessSingleResult(
        adminElectionService.registerVoters(electionVoterRegisterRequestDto));
  }

  @PostMapping("/voter")
  public SingleResult<ElectionVoterRegisterResponseDto> registerVoter(
      @RequestParam Long electionId,
      @RequestParam Long voterId
  ) {
    return responseService.getSuccessSingleResult(
        adminElectionService.registerVoter(electionId, voterId));
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
