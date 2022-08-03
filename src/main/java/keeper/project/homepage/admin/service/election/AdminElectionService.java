package keeper.project.homepage.admin.service.election;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.admin.dto.election.ElectionCandidateDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCandidateRegisterRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCandidateRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionVoterRegisterRequestDto;
import keeper.project.homepage.admin.dto.election.response.ElectionCandidateResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionCandidateRegisterResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionVoterRegisterResponseDto;
import keeper.project.homepage.admin.service.member.AdminMemberUtilService;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.election.ElectionVoterPK;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.exception.election.CustomElectionCandidateExistException;
import keeper.project.homepage.exception.election.CustomElectionCandidateNotFoundException;
import keeper.project.homepage.exception.election.CustomElectionNotFoundException;
import keeper.project.homepage.exception.election.CustomElectionVoterExistException;
import keeper.project.homepage.exception.election.CustomElectionVoterNotFoundException;
import keeper.project.homepage.repository.election.ElectionCandidateRepository;
import keeper.project.homepage.repository.election.ElectionRepository;
import keeper.project.homepage.repository.election.ElectionVoterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminElectionService {

  private final AuthService authService;
  private final AdminMemberUtilService adminMemberUtilService;
  private final ElectionRepository electionRepository;
  private final ElectionCandidateRepository electionCandidateRepository;
  private final ElectionVoterRepository electionVoterRepository;

  private ElectionEntity getElectionById(Long electionId) {
    return electionRepository.findById(electionId)
        .orElseThrow(CustomElectionNotFoundException::new);
  }

  private boolean isExistElectionCandidate(MemberEntity candidate, ElectionEntity election,
      MemberJobEntity memberJob) {
    List<ElectionCandidateEntity> result = electionCandidateRepository.findByCandidateAndElectionAndMemberJob(
        candidate, election, memberJob);
    return result.size() > 0;
  }

  @Transactional
  public Long setUpElection(ElectionRequestDto request) {
    MemberEntity creator = authService.getMemberEntityWithJWT();
    request.setRegisterTime(LocalDateTime.now());
    return electionRepository.save(request.toEntity(creator)).getId();
  }

  @Transactional
  public ElectionResponseDto openElection(Long electionId) {
    ElectionEntity election = getElectionById(electionId);
    election.openElection();
    return ElectionResponseDto.from(election);
  }

  @Transactional
  public ElectionResponseDto closeElection(Long electionId) {
    ElectionEntity election = getElectionById(electionId);
    election.closeElection();
    return ElectionResponseDto.from(election);
  }

  @Transactional
  public ElectionCandidateRegisterResponseDto registerCandidates(
      ElectionCandidateRegisterRequestDto request) {
    ElectionEntity election = getElectionById(request.getElectionId());
    MemberJobEntity memberJob = adminMemberUtilService.getJobById(request.getMemberJobId());
    ElectionCandidateRegisterResponseDto result = new ElectionCandidateRegisterResponseDto(0,
        memberJob);
    for (ElectionCandidateDto candidateInfo : request.getCandidates()) {
      MemberEntity candidate = adminMemberUtilService.getMemberById(candidateInfo.getMemberId());
      if(isExistElectionCandidate(candidate, election, memberJob)) continue;
      ElectionCandidateEntity savedCandidate = electionCandidateRepository.save(
          candidateInfo.toEntity(candidate, election, memberJob)
      );
      result.increaseRegisterCount();
      result.registerCandidateId(savedCandidate.getId());
    }
    return result;
  }

  @Transactional
  public Long registerCandidate(ElectionCandidateRequestDto request) {
    ElectionEntity election = getElectionById(request.getElectionId());
    MemberEntity candidate = adminMemberUtilService.getMemberById(request.getMemberId());
    MemberJobEntity memberJob = adminMemberUtilService.getJobById(request.getMemberJobId());
    if (isExistElectionCandidate(candidate, election, memberJob))
      throw new CustomElectionCandidateExistException();
    return electionCandidateRepository.save(request.toEntity(candidate, election, memberJob))
        .getId();
  }

  @Transactional
  public ElectionCandidateResponseDto deleteCandidate(Long candidateId) {
    ElectionCandidateEntity electionCandidate = electionCandidateRepository.findById(candidateId)
        .orElseThrow(CustomElectionCandidateNotFoundException::new);
    electionCandidateRepository.delete(electionCandidate);
    return ElectionCandidateResponseDto.from(electionCandidate);
  }

  @Transactional
  public ElectionVoterRegisterResponseDto registerVoters(ElectionVoterRegisterRequestDto request) {
    ElectionEntity election = getElectionById(request.getElectionId());
    ElectionVoterRegisterResponseDto result = new ElectionVoterRegisterResponseDto(election, 0);
    for (Long voterId : request.getVoterIds()) {
      MemberEntity voter = adminMemberUtilService.getMemberById(voterId);
      ElectionVoterPK pk = new ElectionVoterPK(voter, election);
      if (electionVoterRepository.findById(pk).isPresent()) continue;
      electionVoterRepository.save(request.toEntity(pk));
      result.increaseRegisterCount();
      result.addVoterId(voter.getId());
    }
    return result;
  }

  @Transactional
  public ElectionVoterRegisterResponseDto registerVoter(Long electionId, Long voterId) {
    ElectionEntity election = getElectionById(electionId);
    MemberEntity voter = adminMemberUtilService.getMemberById(voterId);
    ElectionVoterPK pk = new ElectionVoterPK(voter, election);
    if (electionVoterRepository.findById(pk).isPresent())
      throw new CustomElectionVoterExistException();
    ElectionVoterRegisterResponseDto result = new ElectionVoterRegisterResponseDto(election, 0);
    electionVoterRepository.save(ElectionVoterEntity.builder()
        .electionVoterPK(pk)
        .isVoted(false)
        .build());
    result.increaseRegisterCount();
    result.addVoterId(voter.getId());
    return result;
  }

  @Transactional
  public Long deleteVoter(Long electionId, Long voterId) {
    ElectionEntity election = getElectionById(electionId);
    MemberEntity voter = adminMemberUtilService.getMemberById(voterId);
    ElectionVoterPK pk = new ElectionVoterPK(voter, election);
    ElectionVoterEntity voterEntity = electionVoterRepository.findById(pk).orElseThrow(
        CustomElectionVoterNotFoundException::new);
    electionVoterRepository.delete(voterEntity);
    return voter.getId();
  }

}
