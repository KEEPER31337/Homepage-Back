package keeper.project.homepage.admin.service.election;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.admin.dto.election.ElectionCandidateDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCandidateMultiSaveRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCandidateSaveRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCreateRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionVoterMultiSaveRequestDto;
import keeper.project.homepage.admin.dto.election.response.ElectionCandidateDeleteResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionCandidateMultiSaveResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionUpdateResponseDto;
import keeper.project.homepage.admin.dto.election.response.ElectionVoterMultiSaveResponseDto;
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
  public Long createElection(ElectionCreateRequestDto electionCreateRequestDto) {
    MemberEntity creator = authService.getMemberEntityWithJWT();
    electionCreateRequestDto.setRegisterTime(LocalDateTime.now());
    return electionRepository.save(electionCreateRequestDto.toEntity(creator)).getId();
  }

  @Transactional
  public ElectionUpdateResponseDto openElection(Long electionId) {
    ElectionEntity election = getElectionById(electionId);
    election.openElection();
    return ElectionUpdateResponseDto.from(election);
  }

  @Transactional
  public ElectionUpdateResponseDto closeElection(Long electionId) {
    ElectionEntity election = getElectionById(electionId);
    election.closeElection();
    return ElectionUpdateResponseDto.from(election);
  }

  @Transactional
  public ElectionCandidateMultiSaveResponseDto registerCandidates(
      ElectionCandidateMultiSaveRequestDto requestDto) {
    ElectionEntity election = getElectionById(requestDto.getElectionId());
    MemberJobEntity memberJob = adminMemberUtilService.getJobById(requestDto.getMemberJobId());
    ElectionCandidateMultiSaveResponseDto response = new ElectionCandidateMultiSaveResponseDto(0,
        memberJob);
    for (ElectionCandidateDto candidateDto : requestDto.getCandidates()) {
      MemberEntity member = adminMemberUtilService.getMemberById(candidateDto.getMemberId());
      if (isExistElectionCandidate(member, election, memberJob)) {
        continue;
      }
      ElectionCandidateEntity candidate = electionCandidateRepository.save(
          candidateDto.toEntity(member, election, memberJob)
      );
      response.increaseRegisterCount();
      response.registerCandidateId(candidate.getId());
    }
    return response;
  }

  @Transactional
  public Long registerCandidate(ElectionCandidateSaveRequestDto requestDto) {
    ElectionEntity election = getElectionById(requestDto.getElectionId());
    MemberEntity member = adminMemberUtilService.getMemberById(requestDto.getMemberId());
    MemberJobEntity memberJob = adminMemberUtilService.getJobById(requestDto.getMemberJobId());
    if (isExistElectionCandidate(member, election, memberJob)) {
      throw new CustomElectionCandidateExistException();
    }
    return electionCandidateRepository.save(requestDto.toEntity(member, election, memberJob))
        .getId();
  }

  @Transactional
  public ElectionCandidateDeleteResponseDto deleteCandidate(Long candidateId) {
    ElectionCandidateEntity candidate = electionCandidateRepository.findById(candidateId)
        .orElseThrow(CustomElectionCandidateNotFoundException::new);
    electionCandidateRepository.delete(candidate);
    return ElectionCandidateDeleteResponseDto.from(candidate);
  }

  @Transactional
  public ElectionVoterMultiSaveResponseDto registerVoters(
      ElectionVoterMultiSaveRequestDto electionVoterMultiSaveRequestDto) {
    ElectionEntity election = getElectionById(electionVoterMultiSaveRequestDto.getElectionId());
    ElectionVoterMultiSaveResponseDto response = new ElectionVoterMultiSaveResponseDto(election, 0);
    for (Long memberId : electionVoterMultiSaveRequestDto.getMemberIds()) {
      MemberEntity member = adminMemberUtilService.getMemberById(memberId);
      ElectionVoterPK pk = new ElectionVoterPK(member, election);
      if (electionVoterRepository.findById(pk).isPresent()) {
        continue;
      }
      electionVoterRepository.save(electionVoterMultiSaveRequestDto.toEntity(pk));
      response.increaseRegisterCount();
      response.addVoterId(member.getId());
    }
    return response;
  }

  @Transactional
  public ElectionVoterMultiSaveResponseDto registerVoter(Long electionId, Long memberId) {
    ElectionEntity election = getElectionById(electionId);
    MemberEntity member = adminMemberUtilService.getMemberById(memberId);
    ElectionVoterPK pk = new ElectionVoterPK(member, election);
    if (electionVoterRepository.findById(pk).isPresent()) {
      throw new CustomElectionVoterExistException();
    }
    ElectionVoterMultiSaveResponseDto response = new ElectionVoterMultiSaveResponseDto(election, 0);
    ElectionVoterEntity voter = ElectionVoterEntity.builder().electionVoterPK(pk).isVoted(false)
        .build();
    electionVoterRepository.save(voter);
    response.increaseRegisterCount();
    response.addVoterId(member.getId());
    return response;
  }

  @Transactional
  public Long deleteVoter(Long electionId, Long voterId) {
    ElectionEntity election = getElectionById(electionId);
    MemberEntity member = adminMemberUtilService.getMemberById(voterId);
    ElectionVoterPK pk = new ElectionVoterPK(member, election);
    ElectionVoterEntity voter = electionVoterRepository.findById(pk).orElseThrow(
        CustomElectionVoterNotFoundException::new);
    electionVoterRepository.delete(voter);
    return member.getId();
  }

}
